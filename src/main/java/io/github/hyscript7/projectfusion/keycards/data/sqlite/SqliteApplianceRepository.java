package io.github.hyscript7.projectfusion.keycards.data.sqlite;

import io.github.hyscript7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscript7.projectfusion.keycards.data.InMemoryApplianceRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Persists {@link Appliance} data (including the many-to-many reader-link
 * table) to the shared SQLite database while keeping the parent
 * {@link InMemoryApplianceRepository} as a fast read cache.
 * <p>
 * Every save is wrapped in a transaction that atomically replaces the
 * appliance's reader-link rows, so the DB is never left in a half-written
 * state even if the server crashes mid-write.
 */
public class SqliteApplianceRepository extends InMemoryApplianceRepository {

    private final JavaPlugin plugin;
    private final DatabaseManager db;

    public SqliteApplianceRepository(JavaPlugin plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
        loadAll();
    }

    // -------------------------------------------------------------------------
    // Startup load
    // -------------------------------------------------------------------------

    private void loadAll() {
        // key = "world;x;y;z" — used to correlate the appliance_readers join rows
        Map<String, Appliance> byKey = new HashMap<>();

        // 1. Load all appliances
        String applianceSql = "SELECT world, x, y, z FROM appliances";
        try (PreparedStatement ps = db.getConnection().prepareStatement(applianceSql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String worldName = rs.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().warning("[Keycards] Skipping appliance in unloaded/unknown world: " + worldName);
                    continue;
                }
                int x = rs.getInt("x"), y = rs.getInt("y"), z = rs.getInt("z");
                Location loc = new Location(world, x, y, z);
                Appliance appliance = new Appliance(loc);
                byKey.put(worldName + ";" + x + ";" + y + ";" + z, appliance);
                super.save(appliance);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Keycards] Failed to load appliances from database", e);
            return;
        }

        // 2. Populate their linked-reader sets
        String linkSql = "SELECT appliance_world, appliance_x, appliance_y, appliance_z, "
                + "reader_world, reader_x, reader_y, reader_z FROM appliance_readers";
        try (PreparedStatement ps = db.getConnection().prepareStatement(linkSql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String appKey = rs.getString("appliance_world") + ";"
                        + rs.getInt("appliance_x") + ";"
                        + rs.getInt("appliance_y") + ";"
                        + rs.getInt("appliance_z");
                Appliance appliance = byKey.get(appKey);
                if (appliance == null) continue;

                String readerWorldName = rs.getString("reader_world");
                World readerWorld = Bukkit.getWorld(readerWorldName);
                if (readerWorld == null) continue;

                Location readerLoc = new Location(
                        readerWorld,
                        rs.getInt("reader_x"),
                        rs.getInt("reader_y"),
                        rs.getInt("reader_z")
                );
                appliance.getLinkedReaders().add(readerLoc);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Keycards] Failed to load appliance-reader links from database", e);
        }
    }

    // -------------------------------------------------------------------------
    // Repository interface
    // -------------------------------------------------------------------------

    @Override
    public void save(Appliance appliance) {
        super.save(appliance);

        // Snapshot the linked-reader set before going async to avoid a race
        // where the set is mutated between the main-thread call and the write.
        Location loc = appliance.getLocation();
        Set<Location> linkedReaders = new HashSet<>(appliance.getLinkedReaders());

        db.ioExecutor.submit(() -> {
            synchronized (db.getConnection()) {
                try {
                    db.getConnection().setAutoCommit(false);
                    try {
                        // Ensure the appliance row exists
                        try (PreparedStatement ps = db.getConnection().prepareStatement(
                                "INSERT OR IGNORE INTO appliances (world, x, y, z) VALUES (?, ?, ?, ?)")) {
                            ps.setString(1, loc.getWorld().getName());
                            ps.setInt(2, loc.getBlockX());
                            ps.setInt(3, loc.getBlockY());
                            ps.setInt(4, loc.getBlockZ());
                            ps.executeUpdate();
                        }

                        // Replace the reader-link rows with the current snapshot
                        try (PreparedStatement del = db.getConnection().prepareStatement(
                                "DELETE FROM appliance_readers "
                                        + "WHERE appliance_world = ? AND appliance_x = ? "
                                        + "AND appliance_y = ? AND appliance_z = ?")) {
                            del.setString(1, loc.getWorld().getName());
                            del.setInt(2, loc.getBlockX());
                            del.setInt(3, loc.getBlockY());
                            del.setInt(4, loc.getBlockZ());
                            del.executeUpdate();
                        }

                        if (!linkedReaders.isEmpty()) {
                            String ins = "INSERT INTO appliance_readers "
                                    + "(appliance_world, appliance_x, appliance_y, appliance_z, "
                                    + " reader_world,    reader_x,    reader_y,    reader_z) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement ps = db.getConnection().prepareStatement(ins)) {
                                for (Location rLoc : linkedReaders) {
                                    ps.setString(1, loc.getWorld().getName());
                                    ps.setInt(2, loc.getBlockX());
                                    ps.setInt(3, loc.getBlockY());
                                    ps.setInt(4, loc.getBlockZ());
                                    ps.setString(5, rLoc.getWorld().getName());
                                    ps.setInt(6, rLoc.getBlockX());
                                    ps.setInt(7, rLoc.getBlockY());
                                    ps.setInt(8, rLoc.getBlockZ());
                                    ps.addBatch();
                                }
                                ps.executeBatch();
                            }
                        }
                        db.getConnection().commit();
                    } catch (SQLException e) {
                        db.getConnection().rollback();
                        throw e;
                    } finally {
                        db.getConnection().setAutoCommit(true);
                    }
                } catch (SQLException e) {
                    plugin.getLogger().log(Level.SEVERE, "[Keycards] Failed to save appliance to database", e);
                }
            }
        });
    }

    @Override
    public void delete(Appliance appliance) {
        super.delete(appliance);
        Location loc = appliance.getLocation();
        db.ioExecutor.submit(() -> {
            // CASCADE on the FK deletes appliance_readers rows automatically
            String sql = "DELETE FROM appliances WHERE world = ? AND x = ? AND y = ? AND z = ?";
            synchronized (db.getConnection()) {
                try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
                    ps.setString(1, loc.getWorld().getName());
                    ps.setInt(2, loc.getBlockX());
                    ps.setInt(3, loc.getBlockY());
                    ps.setInt(4, loc.getBlockZ());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().log(Level.SEVERE, "[Keycards] Failed to delete appliance from database", e);
                }
            }
        });
    }

    @Override
    public void shutdown() {
        db.shutdown();
    }
}
