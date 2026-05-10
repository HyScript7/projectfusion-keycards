package io.github.hyscript7.projectfusion.keycards.commands;

import io.github.hyscript7.projectfusion.keycards.commands.subcommands.GetKeycardCommand;
import io.github.hyscript7.projectfusion.keycards.commands.subcommands.GetWrenchCommand;
import io.github.hyscript7.projectfusion.keycards.items.impl.KeycardItem;
import io.github.hyscript7.projectfusion.keycards.items.impl.LinkingWrenchItem;
import io.github.hyscript7.projectfusion.keycards.items.impl.ReaderWrenchItem;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Assembles and registers all {@code /keycards} sub-commands.
 * <p>
 * To add a new sub-command: implement {@link BrigadierCommand}, instantiate it
 * here, and attach its {@code .build()} result under the appropriate parent
 * literal (e.g. {@code get}).
 */
public class CommandManager {

    private final JavaPlugin plugin;
    private final KeycardItem keycardItem;
    private final LinkingWrenchItem linkingWrenchItem;
    private final ReaderWrenchItem readerWrenchItem;

    public CommandManager(
            JavaPlugin plugin,
            KeycardItem keycardItem,
            LinkingWrenchItem linkingWrenchItem,
            ReaderWrenchItem readerWrenchItem
    ) {
        this.plugin = plugin;
        this.keycardItem = keycardItem;
        this.linkingWrenchItem = linkingWrenchItem;
        this.readerWrenchItem = readerWrenchItem;
    }

    /** Registers all commands with Paper's lifecycle event system. */
    public void register() {
        BrigadierCommand getKeycard = new GetKeycardCommand(keycardItem);
        BrigadierCommand getWrench  = new GetWrenchCommand(linkingWrenchItem, readerWrenchItem);

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(
                        Commands.literal("keycards")
                                .then(Commands.literal("get")
                                        .then(getKeycard.build())
                                        .then(getWrench.build())
                                )
                                .build(),
                        "ProjectFusion Keycards — admin item commands"
                )
        );
    }
}
