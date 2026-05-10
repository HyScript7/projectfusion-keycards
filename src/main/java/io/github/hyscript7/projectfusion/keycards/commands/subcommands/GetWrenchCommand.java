package io.github.hyscript7.projectfusion.keycards.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.hyscript7.projectfusion.keycards.commands.BrigadierCommand;
import io.github.hyscript7.projectfusion.keycards.items.impl.LinkingWrenchItem;
import io.github.hyscript7.projectfusion.keycards.items.impl.ReaderWrenchItem;
import io.github.hyscript7.projectfusion.keycards.permissions.Permissions;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * Handles: {@code /keycards get wrench <linking|reader>}
 * <p>
 * Requires permission: {@value Permissions#COMMAND_GET_WRENCH}
 */
public class GetWrenchCommand implements BrigadierCommand {

    private final LinkingWrenchItem linkingWrenchItem;
    private final ReaderWrenchItem readerWrenchItem;

    public GetWrenchCommand(LinkingWrenchItem linkingWrenchItem, ReaderWrenchItem readerWrenchItem) {
        this.linkingWrenchItem = linkingWrenchItem;
        this.readerWrenchItem = readerWrenchItem;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("wrench")
                .requires(src -> src.getSender().hasPermission(Permissions.COMMAND_GET_WRENCH))
                .then(Commands.literal("linking")
                        .executes(ctx -> giveItem(ctx.getSource(), "linking"))
                )
                .then(Commands.literal("reader")
                        .executes(ctx -> giveItem(ctx.getSource(), "reader"))
                );
    }

    private int giveItem(CommandSourceStack source, String type) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(
                    Component.text("This command can only be used by players.").color(NamedTextColor.RED)
            );
            return Command.SINGLE_SUCCESS;
        }
        switch (type) {
            case "linking" -> {
                player.getInventory().addItem(linkingWrenchItem.createItemStack());
                player.sendMessage(Component.text("Given Linking Wrench.").color(NamedTextColor.GREEN));
            }
            case "reader" -> {
                player.getInventory().addItem(readerWrenchItem.createItemStack());
                player.sendMessage(Component.text("Given Reader Wrench.").color(NamedTextColor.GREEN));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
