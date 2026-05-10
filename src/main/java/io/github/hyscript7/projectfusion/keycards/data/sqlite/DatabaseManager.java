package io.github.hyscript7.projectfusion.keycards.data.sqlite;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * Manages the SQLite database connection and shared background I/O executor.
 * <p>
 * <b>Driver note:</b> Paper bundles {@code org.xerial:sqlite-jdbc} on its
 * classpath, so no additional dependency is required in your build file.
 * <p>
 * WAL journal mode is enabled on open, giving much better concurrent read
 * performance than the default DELETE mode — ideal for multi-world servers
 * with many simultaneous keycard swipes.
 * <p>
 * All writes are submitted to a single-threaded {@link ExecutorService} so
 * they are serialised and never race against each other. Reads always hit the
 * in-memory cache (populated at startup) and never touch the DB on the main
 * thread.
 */
public class DatabaseManager {

    /** Single-threaded executor shared by all repositories. */
    final ExecutorService ioExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "keycards-db-writer");
        t.setDaemon(true);
        return t;
    });

    private final JavaPlugin plugin;
    private Connection connection;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public DatabaseManager(JavaPlugin plugin) throws SQLException {
        this.plugin = plugin;
        plugin.getDataFolder().mkdirs();
        File dbFile = new File(plugin.getDataFolder(), "keycards.db");

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "SQLite JDBC driver not found. Make sure you are running Paper 1.19 or newer.", e);
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

        try (Statement stmt = connection.createStatement()) {
            // WAL = concurrent readers while a single writer is active
            stmt.execute("PRAGMA journal_mode=WAL");
            // Enforce FK constraints (used by the appliance_readers cascade)
            stmt.execute("PRAGMA foreign_keys=ON");
        }

        createSchema();
    }

    // -------------------------------------------------------------------------
    // Schema
    // -------------------------------------------------------------------------

    private void createSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS readers (
                        world  TEXT    NOT NULL,
                        x      INTEGER NOT NULL,
                        y      INTEGER NOT NULL,
                        z      INTEGER NOT NULL,
                        minimal_level          INTEGER NOT NULL DEFAULT 1,
                        require_exact_match    INTEGER NOT NULL DEFAULT 0,
                        pulse_duration_ticks   INTEGER NOT NULL DEFAULT 60,
                        PRIMARY KEY (world, x, y, z)
                    )
                    """);

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS appliances (
                        world  TEXT    NOT NULL,
                        x      INTEGER NOT NULL,
                        y      INTEGER NOT NULL,
                        z      INTEGER NOT NULL,
                        PRIMARY KEY (world, x, y, z)
                    )
                    """);

            // Linked-reader join table; cascades on appliance deletion so no
            // orphan rows are ever left behind.
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS appliance_readers (
                        appliance_world TEXT    NOT NULL,
                        appliance_x     INTEGER NOT NULL,
                        appliance_y     INTEGER NOT NULL,
                        appliance_z     INTEGER NOT NULL,
                        reader_world    TEXT    NOT NULL,
                        reader_x        INTEGER NOT NULL,
                        reader_y        INTEGER NOT NULL,
                        reader_z        INTEGER NOT NULL,
                        PRIMARY KEY (
                            appliance_world, appliance_x, appliance_y, appliance_z,
                            reader_world,    reader_x,    reader_y,    reader_z
                        ),
                        FOREIGN KEY (appliance_world, appliance_x, appliance_y, appliance_z)
                            REFERENCES appliances(world, x, y, z) ON DELETE CASCADE
                    )
                    """);
        }
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Returns the shared (and synchronised-on by callers) database connection. */
    public synchronized Connection getConnection() {
        return connection;
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Shuts down the I/O executor (waiting up to 10 s for in-flight writes)
     * then closes the connection. Idempotent — safe to call from multiple
     * repository {@code shutdown()} methods.
     */
    public void shutdown() {
        if (!shutdown.compareAndSet(false, true)) {
            return; // already shut down
        }
        ioExecutor.shutdown();
        try {
            if (!ioExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                plugin.getLogger().warning("[Keycards] Timed out waiting for DB writes to flush — some data may not have been saved.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "[Keycards] Failed to close database connection", e);
        }
    }
}
