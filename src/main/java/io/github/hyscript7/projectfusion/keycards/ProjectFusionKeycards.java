package io.github.hyscript7.projectfusion.keycards;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.hyscript7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscript7.projectfusion.keycards.appliances.ApplianceRepository;
import io.github.hyscript7.projectfusion.keycards.appliances.ApplianceService;
import io.github.hyscript7.projectfusion.keycards.appliances.handlers.ApplianceManager;
import io.github.hyscript7.projectfusion.keycards.appliances.handlers.types.DoorApplianceHandler;
import io.github.hyscript7.projectfusion.keycards.data.yaml.YamlApplianceRepository;
import io.github.hyscript7.projectfusion.keycards.data.yaml.YamlReaderRepository;
import io.github.hyscript7.projectfusion.keycards.items.CustomItemHandler;
import io.github.hyscript7.projectfusion.keycards.items.CustomItemService;
import io.github.hyscript7.projectfusion.keycards.items.impl.KeycardItem;
import io.github.hyscript7.projectfusion.keycards.items.impl.LinkingWrenchItem;
import io.github.hyscript7.projectfusion.keycards.items.impl.ReaderWrenchItem;
import io.github.hyscript7.projectfusion.keycards.listeners.BlockBreakListener;
import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import io.github.hyscript7.projectfusion.keycards.readers.ReaderRepository;
import io.github.hyscript7.projectfusion.keycards.readers.ReaderService;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProjectFusionKeycards extends JavaPlugin {

    public static final String namespace = "projectfusion";

    private ApplianceRepository applianceRepository;
    private ReaderRepository readerRepository;

    @Override
    public void onEnable() {
        // Plugin startup logic
        CustomItemService customItemService = new CustomItemService();
        CustomItemHandler customItemHandler = new CustomItemHandler(customItemService);

        ApplianceManager applianceManager = new ApplianceManager();
        applianceManager.registerHandler(new DoorApplianceHandler());

        ConfigurationSerialization.registerClass(Appliance.class);
        ConfigurationSerialization.registerClass(Reader.class);

        applianceRepository = new YamlApplianceRepository(this);
        ApplianceService applianceService = new ApplianceService(applianceRepository);

        readerRepository = new YamlReaderRepository(this);
        ReaderService readerService = new ReaderService(readerRepository);

        LinkManager linkManager = new LinkManager(readerService, applianceService);

        ReaderManager readerManager = new ReaderManager(linkManager, applianceManager);

        KeycardItem keycardItem = new KeycardItem(readerManager, readerService);
        ReaderWrenchItem readerWrenchItem = new ReaderWrenchItem(readerService);
        LinkingWrenchItem linkingWrenchItem = new LinkingWrenchItem(readerService, applianceService, linkManager);

        customItemService.registerItem(keycardItem);
        customItemService.registerItem(readerWrenchItem);
        customItemService.registerItem(linkingWrenchItem);

        Bukkit.getServer().getPluginManager().registerEvents(customItemHandler, this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakListener(applianceService, readerService, linkManager), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(Commands.literal("keycards")
                    .then(Commands.literal("get")
                            .then(Commands.literal("keycard")
                                    .then(Commands.argument("level", IntegerArgumentType.integer(0, 256))
                                            .then(Commands.argument("exact", IntegerArgumentType.integer(0, 2))
                                                .executes(ctx -> {
                                                    boolean exact;
                                                    try {
                                                        exact = ctx.getArgument("exact", int.class) == 1;
                                                    } catch (Exception e) {
                                                        exact = false;
                                                    }
                                                    int level = ctx.getArgument("level", int.class);
                                                    ((Player) ctx.getSource().getSender()).getInventory().
                                                            addItem(keycardItem.createItemStackWithLevel(level, exact));
                                                    return Command.SINGLE_SUCCESS;
                                                }))))
                            .then(Commands.literal("wrench")
                                    .then(Commands.literal("linking").executes(ctx -> {
                                        ((Player) ctx.getSource().getSender()).getInventory().
                                                addItem(linkingWrenchItem.createItemStack());
                                        return Command.SINGLE_SUCCESS;
                                    }))
                                    .then(Commands.literal("reader").executes(ctx -> {
                                        ((Player) ctx.getSource().getSender()).getInventory().
                                                addItem(readerWrenchItem.createItemStack());
                                        return Command.SINGLE_SUCCESS;
                                    }))
                            )
                    )
                    .build()
            );
        });
    }

    @Override
    public void onDisable() {
        // Ensure background threads finish writing to disk
        if (applianceRepository != null) applianceRepository.shutdown();
        if (readerRepository != null) readerRepository.shutdown();
    }
}
