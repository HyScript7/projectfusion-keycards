package io.github.hyscript7.projectfusion.keycards.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.hyscript7.projectfusion.keycards.commands.BrigadierCommand;
import io.github.hyscript7.projectfusion.keycards.items.impl.KeycardItem;
import io.github.hyscript7.projectfusion.keycards.permissions.Permissions;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * Handles: {@code /keycards get keycard <level> [exact:true|false]}
 * <p>
 * Requires permission: {@value Permissions#COMMAND_GET_KEYCARD}
 * <p>
 * The {@code exact} argument is optional and defaults to {@code false}.
 * Using a proper boolean argument type replaces the old ambiguous 0/1/2 integer.
 */
public class GetKeycardCommand implements BrigadierCommand {

    private final KeycardItem keycardItem;

    public GetKeycardCommand(KeycardItem keycardItem) {
        this.keycardItem = keycardItem;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("keycard")
                .requires(src -> src.getSender().hasPermission(Permissions.COMMAND_GET_KEYCARD))
                .then(Commands.argument("level", IntegerArgumentType.integer(0, 255))
                        // /keycards get keycard <level>  — exact defaults to false
                        .executes(ctx -> execute(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "level"), false))
                        // /keycards get keycard <level> <exact>
                        .then(Commands.argument("exact", BoolArgumentType.bool())
                                .executes(ctx -> execute(
                                        ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "level"),
                                        BoolArgumentType.getBool(ctx, "exact")
                                ))
                        )
                );
    }

    private int execute(CommandSourceStack source, int level, boolean exact) {
        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(
                    Component.text("This command can only be used by players.").color(NamedTextColor.RED)
            );
            return Command.SINGLE_SUCCESS;
        }
        player.getInventory().addItem(keycardItem.createItemStackWithLevel(level, exact));
        player.sendMessage(
                Component.text("Given Level " + level + (exact ? " (exact match)" : "") + " Keycard.")
                        .color(NamedTextColor.GREEN)
        );
        return Command.SINGLE_SUCCESS;
    }
}
