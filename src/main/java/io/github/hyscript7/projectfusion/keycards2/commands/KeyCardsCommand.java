package io.github.hyscript7.projectfusion.keycards2.commands;

import io.github.hyscript7.projectfusion.keycards2.data.interfaces.KeyCard;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.KeyCardAggregator;
import io.github.hyscript7.projectfusion.keycards2.items.impl.CardReaderItemImpl;
import io.github.hyscript7.projectfusion.keycards2.items.interfaces.CardReaderItemFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class KeyCardsCommand implements CommandExecutor {
    private final KeyCardAggregator keyCardAggregator;

    public KeyCardsCommand(KeyCardAggregator keyCardAggregator) {
        this.keyCardAggregator = keyCardAggregator;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (sender instanceof Player p) {
            String subCommand = args[0];
            return switch (subCommand) {
                case "add" -> addKeycard(p, args);
                case "get" -> getKeycard(p, args);
                case "give" -> false;
                case "list" -> false;
                case "remove" -> false;
                case "door" -> giveDoorTool(p, args);
                default -> invalidCommand(p);
            };
        }
        sender.sendMessage("This command can only be used by players! If you are trying to give a keycard to a player through the console, try using this command: /execute as Player123 run keycards get KeycardId");
        return false;
    }

    private boolean invalidCommand(Player senderPlayer) {
        // TODO: Send error message to user
        return false;
    }

    private boolean addKeycard(Player senderPlayer, String[] args) {
        if (args.length < 4) {
            // TODO: Send error to user (bad params)
            return false;
        }
        String levelString = args[1];
        String name = args[2];
        String description = args[3];
        String customModelDataString;
        if (args.length >= 5) {
            customModelDataString = args[4];
        } else {
            customModelDataString = "0";
        }
        int level;
        int customModelData;
        try {
            level = Integer.parseInt(levelString);
            customModelData = Integer.parseInt(customModelDataString);
        } catch (NumberFormatException e) {
            // TODO: Send error to user (bad number format)
            return false;
        }
        keyCardAggregator.create(level, Component.text(name), List.of(Component.text(description)), customModelData);
        // TODO: Send success message to user (and keycard id)
        return true;
    }

    private boolean getKeycard(Player senderPlayer, String[] args) {
        if (args.length < 2) {
            // TODO: Send error to user (bad params)
            return false;
        }
        String idString = args[1];
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            // TODO: Send error to user (bad number format)
            return false;
        }
        KeyCard keyCard = keyCardAggregator.fromId(id);
        if (Objects.isNull(keyCard)) {
            // TODO: Send error to user (invalid ID)
            return false;
        }
        ItemStack itemStack = keyCardAggregator.getItem(keyCard);
        senderPlayer.getInventory().addItem(itemStack);
        // TODO: Send success message to user
        return true;
    }

    private boolean giveDoorTool(Player senderPlayer, String[] args) {
        if (args.length < 4) {
            // TODO: Send error to user (bad params)
            return false;
        }
        String levelString = args[1];
        String durationString = args[2];
        String matchExactlyString = args[3];
        int level;
        long duration;
        try {
            level = Integer.parseInt(levelString);
            duration = Long.parseLong(durationString);
        } catch (NumberFormatException e) {
            // TODO: Send error to user (bad number format)
            return false;
        }
        boolean match = matchExactlyString.equalsIgnoreCase("true");
        ItemStack itemStack = new CardReaderItemImpl(level, duration, match).asItemStack();
        senderPlayer.getInventory().addItem(itemStack);
        // TODO: Send success message to user
        return true;
    }
}
