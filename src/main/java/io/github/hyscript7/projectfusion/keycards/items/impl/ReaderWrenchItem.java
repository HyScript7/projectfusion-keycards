package io.github.hyscript7.projectfusion.keycards.items.impl;

import io.github.hyscript7.projectfusion.keycards.items.CustomItem;
import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import io.github.hyscript7.projectfusion.keycards.readers.ReaderService;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReaderWrenchItem extends CustomItem {
    private final ReaderService readerService;

    public ReaderWrenchItem(ReaderService readerService) {
        super("reader_wrench", Material.BLAZE_ROD.getKey(), Component.text("Reader Wrench"), List.of());
        this.readerService = readerService;
    }

    @Override
    protected void customizeItem(ItemStack itemStack) {
        WrenchSettings wrenchSettings = WrenchSettings.defaultSettings();
        wrenchSettings.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, wrenchSettings);
    }

    private void updateItemLore(ItemStack itemStack, WrenchSettings settings) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.lore(List.of(
                Component.text("Level: " + settings.getLevel()),
                Component.text("Pulse Duration: " + (settings.getPulseDurationInTicks() / 20.0d) + "s"),
                Component.text("Requires Exact Keycard Level: " + settings.isWantsExactMatch()),
                Component.text(""),
                Component.text("-- Left Click --"),
                Component.text("Looking at air: Change level (shift to reverse) by 1"),
                Component.text("Looking at block: Change pulse duration (shift to reverse) by 1/2 of a second"),
                Component.text("-- Right Click --"),
                Component.text("b. Looking at air"),
                Component.text("Toggle Exact Level Match setting"),
                Component.text("a. Looking at a block"),
                Component.text("Without Sneaking: Configure or update reader configuration"),
                Component.text("With Sneak: Copy existing reader configuration")
        ));
        itemStack.setItemMeta(meta);
    }

    @Override
    public void onLeftClick(@NotNull ItemStack itemStack, @NotNull Player player, @Nullable Block clickedBlock, @Nullable Location clickedLocation, boolean isOffHanded) {
        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            changeWrenchLevelSetting(player, itemStack, !player.isSneaking());
        } else {
            changeWrenchPulseDurationSetting(player, itemStack, 10, !player.isSneaking());
        }
    }

    private void changeWrenchLevelSetting(Player player, ItemStack itemStack, boolean increment) {
        WrenchSettings wrenchSettings = WrenchSettings.readFromPersistentDataContainer(itemStack);
        if (increment) {
            wrenchSettings.setLevel(Math.min(wrenchSettings.getLevel() + 1, 255));
        } else {
            wrenchSettings.setLevel(Math.max(0, wrenchSettings.getLevel() - 1));
        }
        player.sendActionBar(Component.text("Level: ").color(NamedTextColor.GRAY).append(Component.text(wrenchSettings.getLevel()).color(increment ? NamedTextColor.GREEN : NamedTextColor.RED)));
        wrenchSettings.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, wrenchSettings);
    }

    private void changeWrenchPulseDurationSetting(Player player, ItemStack itemStack, int step, boolean increment) {
        WrenchSettings wrenchSettings = WrenchSettings.readFromPersistentDataContainer(itemStack);
        if (increment) {
            // Max = 15 seconds
            wrenchSettings.setPulseDurationInTicks(Math.min(wrenchSettings.getPulseDurationInTicks() + step, 20 * 15));
        } else {
            // Min = 1/2 of a second
            wrenchSettings.setPulseDurationInTicks(Math.max(10, wrenchSettings.getPulseDurationInTicks() - step));
        }
        player.sendActionBar(Component.text("Pulse Duration: ").color(NamedTextColor.GRAY).append(Component.text(wrenchSettings.getPulseDurationInTicks() / 20.0d).color(increment ? NamedTextColor.GREEN : NamedTextColor.RED)));
        wrenchSettings.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, wrenchSettings);
    }

    @Override
    public void onRightClick(@NotNull ItemStack itemStack, @NotNull Player player, @Nullable Block clickedBlock, @Nullable Location clickedLocation, boolean isOffHanded) {
        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            if (player.isSneaking()) {
                resetSettings(player, itemStack);
            } else {
                toggleWantsExactMatch(player, itemStack);
            }
        } else {
            if (player.isSneaking()) {
                copyReaderSettings(player, itemStack, clickedBlock);
            } else {
                createOrUpdateReader(player, itemStack, clickedBlock, isOffHanded);
            }
        }
    }

    private void toggleWantsExactMatch(Player player, ItemStack itemStack) {
        WrenchSettings wrenchSettings = WrenchSettings.readFromPersistentDataContainer(itemStack);
        wrenchSettings.setWantsExactMatch(!wrenchSettings.isWantsExactMatch());
        wrenchSettings.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, wrenchSettings);
        player.sendActionBar(Component.text("Wants Exact Match: ").color(NamedTextColor.GRAY).append(Component.text(wrenchSettings.isWantsExactMatch()).color(wrenchSettings.isWantsExactMatch() ? NamedTextColor.GREEN : NamedTextColor.RED)));
    }

    private void resetSettings(Player player, ItemStack itemStack) {
        WrenchSettings wrenchSettings = WrenchSettings.defaultSettings();
        wrenchSettings.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, wrenchSettings);
        player.sendActionBar(Component.text("Settings reset").color(NamedTextColor.AQUA));
    }

    private void createOrUpdateReader(Player player, ItemStack itemStack, Block block, boolean offhanded) {
        WrenchSettings wrenchSettings = WrenchSettings.readFromPersistentDataContainer(itemStack);
        Reader existingReader = readerService.getReader(block.getLocation());
        if (existingReader != null) {
            if (!offhanded) {
                player.sendActionBar(Component.text("Offhand wrench and click again to overwrite configuration.").color(NamedTextColor.DARK_RED));
                return;
            }
            wrenchSettings.updateReader(existingReader);
            readerService.saveReader(existingReader);
            player.sendActionBar(Component.text("Reader configuration updated").color(NamedTextColor.YELLOW));
        } else {
            readerService.saveReader(wrenchSettings.createReader(block.getLocation()));
            player.sendActionBar(Component.text("Reader created").color(NamedTextColor.GREEN));
        }
    }

    private void copyReaderSettings(Player player, ItemStack itemStack, Block block) {
        Reader reader = readerService.getReader(block.getLocation());
        if (reader == null) {
            player.sendActionBar(Component.text("You must be looking at a reader!").color(NamedTextColor.RED));
            return;
        }
        WrenchSettings wrenchSettings = WrenchSettings.readFromExistingReader(reader);
        wrenchSettings.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, wrenchSettings);
        player.sendActionBar(Component.text("Reader configuration copied").color(NamedTextColor.AQUA));
    }

    @Getter
    @Setter
    private static class WrenchSettings {
        public static final NamespacedKey WRENCH_LEVEL_SETTINGS = new NamespacedKey("keycards", "wrench_level");
        public static final NamespacedKey WRENCH_EXACT_SETTINGS = new NamespacedKey("keycards", "wrench_exact");
        public static final NamespacedKey WRENCH_DURATION_SETTINGS = new NamespacedKey("keycards", "wrench_duration");

        private int level;
        private int pulseDurationInTicks;
        private boolean wantsExactMatch;

        public WrenchSettings(int level, int pulseDurationInTicks, boolean wantsExactMatch) {
            this.level = level;
            this.pulseDurationInTicks = pulseDurationInTicks;
            this.wantsExactMatch = wantsExactMatch;
        }

        public static WrenchSettings defaultSettings() {
            return new WrenchSettings(1, 60, false);
        }

        public static WrenchSettings readFromExistingReader(Reader reader) {
            return new WrenchSettings(
                    reader.getMinimalLevel(),
                    reader.getPulseDurationInTicks(),
                    reader.isRequireExactLevelMatch()
            );
        }

        public static WrenchSettings readFromPersistentDataContainer(ItemStack itemStack) {
            int level = itemStack.getPersistentDataContainer().getOrDefault(WRENCH_LEVEL_SETTINGS, PersistentDataType.INTEGER, 1);
            int duration = itemStack.getPersistentDataContainer().getOrDefault(WRENCH_DURATION_SETTINGS, PersistentDataType.INTEGER, 60);
            boolean exact = itemStack.getPersistentDataContainer().getOrDefault(WRENCH_EXACT_SETTINGS, PersistentDataType.BOOLEAN, false);
            return new WrenchSettings(level, duration, exact);
        }

        public void saveToPersistentDataContainer(ItemStack itemStack) {
            itemStack.editPersistentDataContainer(pdc -> {
                pdc.set(WRENCH_LEVEL_SETTINGS, PersistentDataType.INTEGER, level);
                pdc.set(WRENCH_EXACT_SETTINGS, PersistentDataType.BOOLEAN, wantsExactMatch);
                pdc.set(WRENCH_DURATION_SETTINGS, PersistentDataType.INTEGER, pulseDurationInTicks);
            });
        }

        public Reader createReader(Location location) {
            return new Reader(location, level, wantsExactMatch, pulseDurationInTicks);
        }

        public void updateReader(Reader reader) {
            reader.setMinimalLevel(level);
            reader.setRequireExactLevelMatch(wantsExactMatch);
            reader.setPulseDurationInTicks(pulseDurationInTicks);
        }
    }
}
