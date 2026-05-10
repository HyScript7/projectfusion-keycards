package io.github.hyscript7.projectfusion.keycards;

import io.github.hyscript7.projectfusion.keycards.appliances.ApplianceRepository;
import io.github.hyscript7.projectfusion.keycards.appliances.ApplianceService;
import io.github.hyscript7.projectfusion.keycards.appliances.handlers.ApplianceManager;
import io.github.hyscript7.projectfusion.keycards.appliances.handlers.types.DoorApplianceHandler;
import io.github.hyscript7.projectfusion.keycards.commands.CommandManager;
import io.github.hyscript7.projectfusion.keycards.data.sqlite.DatabaseManager;
import io.github.hyscript7.projectfusion.keycards.data.sqlite.SqliteApplianceRepository;
import io.github.hyscript7.projectfusion.keycards.data.sqlite.SqliteReaderRepository;
import io.github.hyscript7.projectfusion.keycards.items.CustomItemHandler;
import io.github.hyscript7.projectfusion.keycards.items.CustomItemService;
import io.github.hyscript7.projectfusion.keycards.items.impl.KeycardItem;
import io.github.hyscript7.projectfusion.keycards.items.impl.LinkingWrenchItem;
import io.github.hyscript7.projectfusion.keycards.items.impl.ReaderWrenchItem;
import io.github.hyscript7.projectfusion.keycards.listeners.BlockBreakListener;
import io.github.hyscript7.projectfusion.keycards.readers.ReaderRepository;
import io.github.hyscript7.projectfusion.keycards.readers.ReaderService;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class ProjectFusionKeycards extends JavaPlugin {

    /** NamespacedKey namespace shared across the plugin. */
    public static final String namespace = "projectfusion";

    // Held so onDisable() can flush pending writes before the process exits
    private ApplianceRepository applianceRepository;
    private ReaderRepository readerRepository;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        // ── Database ──────────────────────────────────────────────────────────
        try {
            databaseManager = new DatabaseManager(this);
        } catch (SQLException e) {
            getLogger().severe("Failed to open the SQLite database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        applianceRepository = new SqliteApplianceRepository(this, databaseManager);
        readerRepository    = new SqliteReaderRepository(this, databaseManager);

        // ── Services ─────────────────────────────────────────────────────────
        ApplianceService applianceService = new ApplianceService(applianceRepository);
        ReaderService    readerService    = new ReaderService(readerRepository);

        // ── Appliance handlers ────────────────────────────────────────────────
        ApplianceManager applianceManager = new ApplianceManager();
        applianceManager.registerHandler(new DoorApplianceHandler());

        // ── Link / reader managers ────────────────────────────────────────────
        LinkManager  linkManager  = new LinkManager(readerService, applianceService);
        ReaderManager readerManager = new ReaderManager(linkManager, applianceManager);

        // ── Custom items ──────────────────────────────────────────────────────
        CustomItemService customItemService = new CustomItemService();
        CustomItemHandler customItemHandler = new CustomItemHandler(customItemService);

        KeycardItem       keycardItem       = new KeycardItem(readerManager, readerService);
        ReaderWrenchItem  readerWrenchItem  = new ReaderWrenchItem(readerService);
        LinkingWrenchItem linkingWrenchItem = new LinkingWrenchItem(readerService, applianceService, linkManager);

        customItemService.registerItem(keycardItem);
        customItemService.registerItem(readerWrenchItem);
        customItemService.registerItem(linkingWrenchItem);

        // ── Event listeners ───────────────────────────────────────────────────
        getServer().getPluginManager().registerEvents(customItemHandler, this);
        getServer().getPluginManager().registerEvents(
                new BlockBreakListener(applianceService, readerService, linkManager), this);

        // ── Commands ──────────────────────────────────────────────────────────
        new CommandManager(this, keycardItem, linkingWrenchItem, readerWrenchItem).register();
    }

    @Override
    public void onDisable() {
        // Repositories delegate to DatabaseManager.shutdown() which is idempotent,
        // so calling both is safe and ensures all queued writes are flushed.
        if (applianceRepository != null) applianceRepository.shutdown();
        if (readerRepository    != null) readerRepository.shutdown();
    }
}
