package io.github.hyscript7.projectfusion.keycards.items.impl;

import io.github.hyscript7.projectfusion.keycards.ProjectFusionKeycards;
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
                Component.text("Looking at block: Change pulse duration (shift to reverse) by ½s"),
                Component.text("-- Right Click --"),
                Component.text("Looking at air (no sneak): Toggle Exact Level Match"),
                Component.text("Looking at air (sneak): Reset all settings"),
                Component.text("Looking at block (no sneak): Create or update reader"),
                Component.text("Looking at block (sneak): Copy reader's configuration")
        ));
        itemStack.setItemMeta(meta);
    }

    @Override
    public void onLeftClick(@NotNull ItemStack itemStack, @NotNull Player player,
                            @Nullable Block clickedBlock, @Nullable Location clickedLocation,
                            boolean isOffHanded) {
        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            changeWrenchLevelSetting(player, itemStack, !player.isSneaking());
        } else {
            changeWrenchPulseDurationSetting(player, itemStack, 10, !player.isSneaking());
        }
    }

    @Override
    public void onRightClick(@NotNull ItemStack itemStack, @NotNull Player player,
                             @Nullable Block clickedBlock, @Nullable Location clickedLocation,
                             boolean isOffHanded) {
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

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void changeWrenchLevelSetting(Player player, ItemStack itemStack, boolean increment) {
        WrenchSettings s = WrenchSettings.readFromPersistentDataContainer(itemStack);
        s.setLevel(increment
                ? Math.min(s.getLevel() + 1, 255)
                : Math.max(0, s.getLevel() - 1));
        player.sendActionBar(Component.text("Level: ").color(NamedTextColor.GRAY)
                .append(Component.text(s.getLevel())
                        .color(increment ? NamedTextColor.GREEN : NamedTextColor.RED)));
        s.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, s);
    }

    private void changeWrenchPulseDurationSetting(Player player, ItemStack itemStack, int step, boolean increment) {
        WrenchSettings s = WrenchSettings.readFromPersistentDataContainer(itemStack);
        s.setPulseDurationInTicks(increment
                ? Math.min(s.getPulseDurationInTicks() + step, 20 * 15) // max 15 s
                : Math.max(10, s.getPulseDurationInTicks() - step));      // min 0.5 s
        player.sendActionBar(Component.text("Pulse Duration: ").color(NamedTextColor.GRAY)
                .append(Component.text(s.getPulseDurationInTicks() / 20.0d + "s")
                        .color(increment ? NamedTextColor.GREEN : NamedTextColor.RED)));
        s.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, s);
    }

    private void toggleWantsExactMatch(Player player, ItemStack itemStack) {
        WrenchSettings s = WrenchSettings.readFromPersistentDataContainer(itemStack);
        s.setWantsExactMatch(!s.isWantsExactMatch());
        s.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, s);
        player.sendActionBar(Component.text("Wants Exact Match: ").color(NamedTextColor.GRAY)
                .append(Component.text(s.isWantsExactMatch())
                        .color(s.isWantsExactMatch() ? NamedTextColor.GREEN : NamedTextColor.RED)));
    }

    private void resetSettings(Player player, ItemStack itemStack) {
        WrenchSettings s = WrenchSettings.defaultSettings();
        s.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, s);
        player.sendActionBar(Component.text("Settings reset").color(NamedTextColor.AQUA));
    }

    private void createOrUpdateReader(Player player, ItemStack itemStack, Block block, boolean offhanded) {
        WrenchSettings s = WrenchSettings.readFromPersistentDataContainer(itemStack);
        Reader existingReader = readerService.getReader(block.getLocation());
        if (existingReader != null) {
            if (!offhanded) {
                player.sendActionBar(
                        Component.text("Offhand wrench and click again to overwrite configuration.")
                                .color(NamedTextColor.DARK_RED));
                return;
            }
            s.updateReader(existingReader);
            readerService.saveReader(existingReader);
            player.sendActionBar(Component.text("Reader configuration updated").color(NamedTextColor.YELLOW));
        } else {
            readerService.saveReader(s.createReader(block.getLocation()));
            player.sendActionBar(Component.text("Reader created").color(NamedTextColor.GREEN));
        }
    }

    private void copyReaderSettings(Player player, ItemStack itemStack, Block block) {
        Reader reader = readerService.getReader(block.getLocation());
        if (reader == null) {
            player.sendActionBar(Component.text("You must be looking at a reader!").color(NamedTextColor.RED));
            return;
        }
        WrenchSettings s = WrenchSettings.readFromExistingReader(reader);
        s.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, s);
        player.sendActionBar(Component.text("Reader configuration copied").color(NamedTextColor.AQUA));
    }

    // -------------------------------------------------------------------------
    // Wrench state stored in the item's PDC
    // -------------------------------------------------------------------------

    @Getter
    @Setter
    private static class WrenchSettings {

        // Namespace aligned with the rest of the plugin
        public static final NamespacedKey WRENCH_LEVEL_SETTINGS =
                new NamespacedKey(ProjectFusionKeycards.namespace, "wrench_level");
        public static final NamespacedKey WRENCH_EXACT_SETTINGS =
                new NamespacedKey(ProjectFusionKeycards.namespace, "wrench_exact");
        public static final NamespacedKey WRENCH_DURATION_SETTINGS =
                new NamespacedKey(ProjectFusionKeycards.namespace, "wrench_duration");

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
            var pdc = itemStack.getPersistentDataContainer();
            int level    = pdc.getOrDefault(WRENCH_LEVEL_SETTINGS,    PersistentDataType.INTEGER, 1);
            int duration = pdc.getOrDefault(WRENCH_DURATION_SETTINGS, PersistentDataType.INTEGER, 60);
            boolean exact = pdc.getOrDefault(WRENCH_EXACT_SETTINGS,   PersistentDataType.BOOLEAN, false);
            return new WrenchSettings(level, duration, exact);
        }

        public void saveToPersistentDataContainer(ItemStack itemStack) {
            itemStack.editPersistentDataContainer(pdc -> {
                pdc.set(WRENCH_LEVEL_SETTINGS,    PersistentDataType.INTEGER, level);
                pdc.set(WRENCH_EXACT_SETTINGS,    PersistentDataType.BOOLEAN, wantsExactMatch);
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
