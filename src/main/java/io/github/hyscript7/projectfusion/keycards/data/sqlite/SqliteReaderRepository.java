package io.github.hyscript7.projectfusion.keycards.data.sqlite;

import io.github.hyscript7.projectfusion.keycards.data.InMemoryReaderRepository;
import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Persists {@link Reader} data to the shared SQLite database while keeping
 * the parent {@link InMemoryReaderRepository} as a fast read cache.
 * <p>
 * All disk writes are dispatched to {@link DatabaseManager#ioExecutor} so they
 * never block the main thread. Reads always use the in-memory cache and are
 * therefore O(1) on the main thread regardless of world count.
 */
public class SqliteReaderRepository extends InMemoryReaderRepository {

    private final JavaPlugin plugin;
    private final DatabaseManager db;

    public SqliteReaderRepository(JavaPlugin plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
        loadAll();
    }

    // -------------------------------------------------------------------------
    // Startup load
    // -------------------------------------------------------------------------

    private void loadAll() {
        String sql = "SELECT world, x, y, z, minimal_level, require_exact_match, pulse_duration_ticks FROM readers";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String worldName = rs.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().warning("[Keycards] Skipping reader in unloaded/unknown world: " + worldName);
                    continue;
                }
                Location loc = new Location(world, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
                Reader reader = new Reader(
                        loc,
                        rs.getInt("minimal_level"),
                        rs.getBoolean("require_exact_match"),
                        rs.getInt("pulse_duration_ticks")
                );
                super.save(reader);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Keycards] Failed to load readers from database", e);
        }
    }

    // -------------------------------------------------------------------------
    // Repository interface
    // -------------------------------------------------------------------------

    @Override
    public void save(Reader reader) {
        super.save(reader);
        db.ioExecutor.submit(() -> {
            // INSERT OR REPLACE acts as an upsert: inserts a new row or
            // overwrites all columns when the PK (world, x, y, z) already exists.
            String sql = """
                    INSERT INTO readers (world, x, y, z, minimal_level, require_exact_match, pulse_duration_ticks)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT(world, x, y, z) DO UPDATE SET
                        minimal_level        = excluded.minimal_level,
                        require_exact_match  = excluded.require_exact_match,
                        pulse_duration_ticks = excluded.pulse_duration_ticks
                    """;
            synchronized (db.getConnection()) {
                try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
                    Location loc = reader.getLocation();
                    ps.setString(1, loc.getWorld().getName());
                    ps.setInt(2, loc.getBlockX());
                    ps.setInt(3, loc.getBlockY());
                    ps.setInt(4, loc.getBlockZ());
                    ps.setInt(5, reader.getMinimalLevel());
                    ps.setBoolean(6, reader.isRequireExactLevelMatch());
                    ps.setInt(7, reader.getPulseDurationInTicks());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().log(Level.SEVERE, "[Keycards] Failed to save reader to database", e);
                }
            }
        });
    }

    @Override
    public void delete(Reader reader) {
        super.delete(reader);
        db.ioExecutor.submit(() -> {
            String sql = "DELETE FROM readers WHERE world = ? AND x = ? AND y = ? AND z = ?";
            synchronized (db.getConnection()) {
                try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
                    Location loc = reader.getLocation();
                    ps.setString(1, loc.getWorld().getName());
                    ps.setInt(2, loc.getBlockX());
                    ps.setInt(3, loc.getBlockY());
                    ps.setInt(4, loc.getBlockZ());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().log(Level.SEVERE, "[Keycards] Failed to delete reader from database", e);
                }
            }
        });
    }

    @Override
    public void shutdown() {
        db.shutdown();
    }
}
