package io.github.hyscript7.projectfusion.keycards.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

/**
 * A self-contained Brigadier command subtree that can be composed into the
 * root {@code /keycards} command by {@link CommandManager}.
 * <p>
 * Each implementation is responsible for a single logical branch
 * (e.g. {@code get keycard …}) and handles its own permission requirements
 * via {@code .requires(…)}.
 */
@FunctionalInterface
public interface BrigadierCommand {

    /**
     * Builds the {@link LiteralArgumentBuilder} for this command branch.
     * The returned builder will be attached as a child of the parent node
     * by {@link CommandManager}.
     */
    LiteralArgumentBuilder<CommandSourceStack> build();
}
