package io.github.hyscript7.projectfusion.keycards.items.impl;

import io.github.hyscript7.projectfusion.keycards.LinkManager;
import io.github.hyscript7.projectfusion.keycards.appliances.Appliance;
import io.github.hyscript7.projectfusion.keycards.appliances.ApplianceService;
import io.github.hyscript7.projectfusion.keycards.items.CustomItem;
import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import io.github.hyscript7.projectfusion.keycards.readers.ReaderService;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LinkingWrenchItem extends CustomItem {
    private final ReaderService readerService;
    private final ApplianceService applianceService;
    private final LinkManager linkManager;

    public LinkingWrenchItem(ReaderService readerService, ApplianceService applianceService, LinkManager linkManager) {
        super("linking_wrench", Material.BREEZE_ROD.getKey(), Component.text("Linking Wrench"), List.of());
        this.readerService = readerService;
        this.applianceService = applianceService;
        this.linkManager = linkManager;
    }

    @Override
    protected void customizeItem(ItemStack itemStack) {
        WrenchSettings wrenchSettings = WrenchSettings.defaultSettings();
        wrenchSettings.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, wrenchSettings);
    }

    private void updateItemLore(ItemStack itemStack, WrenchSettings settings) {
        ItemMeta meta = itemStack.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(
                Component.text("-- Selected Reader -- ")
        );
        if (settings.getSelectedLocation() != null) {
            if (readerService.getReader(settings.getSelectedLocation()) == null) {
                lore.add(
                        Component.text("Invalid")
                );
            } else {
                lore.add(
                        Component.text("World: " + settings.getSelectedLocation().getWorld().getKey())
                );
                lore.add(
                        Component.text("X: " + settings.getSelectedLocation().getBlockX() + " Y: " + settings.getSelectedLocation().getBlockY() + " Z: " + settings.getSelectedLocation().getBlockZ())
                );
            }
        } else {
            lore.add(
                    Component.text("None")
            );
        }
        lore.add(Component.text(""));
        lore.add(Component.text("Right click a reader to select it."));
        meta.lore(lore);
        itemStack.setItemMeta(meta);
    }

    @Override
    public void onLeftClick(@NotNull ItemStack itemStack, @NotNull Player player, @Nullable Block clickedBlock, @Nullable Location clickedLocation, boolean isOffHanded) {
        // No ops
    }

    @Override
    public void onRightClick(@NotNull ItemStack itemStack, @NotNull Player player, @Nullable Block clickedBlock, @Nullable Location clickedLocation, boolean isOffHanded) {
        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            clearSelection(itemStack, player);
        } else {
            Reader reader = readerService.getReader(clickedBlock.getLocation());
            if (reader != null) {
                selectReader(itemStack, player, reader);
            } else {
                linkAppliance(itemStack, player, clickedBlock);
            }
        }
    }

    private void clearSelection(ItemStack itemStack, Player player) {
        WrenchSettings wrenchSettings = WrenchSettings.defaultSettings();
        wrenchSettings.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, wrenchSettings);
        player.sendActionBar(Component.text("Selection cleared").color(NamedTextColor.AQUA));
    }

    private void selectReader(ItemStack itemStack, Player player, Reader reader) {
        WrenchSettings wrenchSettings = WrenchSettings.defaultSettings();
        wrenchSettings.setSelectedLocation(reader.getLocation());
        wrenchSettings.saveToPersistentDataContainer(itemStack);
        updateItemLore(itemStack, wrenchSettings);
        player.sendActionBar(Component.text("Reader selected").color(NamedTextColor.AQUA));
    }

    private void linkAppliance(ItemStack itemStack, Player player, Block block) {
        WrenchSettings wrenchSettings = WrenchSettings.readFromPersistentDataContainer(itemStack);
        if (wrenchSettings.getSelectedLocation() == null) {
            player.sendActionBar(Component.text("You must select a reader first!").color(NamedTextColor.RED));
            return;
        }
        Reader selectedReader = readerService.getReader(wrenchSettings.getSelectedLocation());
        if (selectedReader == null) {
            player.sendActionBar(Component.text("Invalid reader selected!").color(NamedTextColor.RED));
            updateItemLore(itemStack, wrenchSettings);
            return;
        }
        if (!player.isSneaking()) {
            player.sendActionBar(Component.text("Sneak and click again to confirm link").color(NamedTextColor.DARK_RED));
            return;
        }
        boolean isNew = false;
        Appliance appliance = applianceService.getAppliance(block.getLocation());
        if (appliance == null) {
            appliance = new Appliance(block.getLocation());
            isNew = true;
        }
        linkManager.link(selectedReader, appliance);
        if (isNew) {
            player.sendActionBar(Component.text("New appliance created and linked").color(NamedTextColor.GREEN));
        } else {
            player.sendActionBar(Component.text("Added new reader to appliance").color(NamedTextColor.YELLOW));
        }
    }

    @Getter
    @Setter
    private static class WrenchSettings {
        public static final NamespacedKey WRENCH_SELECTED_READER = new NamespacedKey("keycards", "wrench_selected_reader");

        private @Nullable Location selectedLocation;

        public WrenchSettings(@Nullable Location selectedLocation) {
            this.selectedLocation = selectedLocation;
        }

        public WrenchSettings() {
            this.selectedLocation = null;
        }

        private static String serializeLocation(Location location) {
            return location.getWorld().getKey() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
        }

        private static Location deserializeLocation(String serializedLocation) {
            String[] components = serializedLocation.split(";");
            String worldNamespacedKeyString = components[0];
            World world = Bukkit.getWorld(Objects.requireNonNull(NamespacedKey.fromString(worldNamespacedKeyString)));
            int x = Integer.parseInt(components[1]);
            int y = Integer.parseInt(components[2]);
            int z = Integer.parseInt(components[3]);
            return new Location(world, x, y, z);
        }

        public static WrenchSettings defaultSettings() {
            return new WrenchSettings();
        }


        public static WrenchSettings readFromPersistentDataContainer(ItemStack itemStack) {
            String serializedLocation = itemStack.getPersistentDataContainer().getOrDefault(WRENCH_SELECTED_READER, PersistentDataType.STRING, "");
            if (serializedLocation.isBlank()) {
                return new WrenchSettings();
            }
            return new WrenchSettings(deserializeLocation(serializedLocation));
        }

        public void saveToPersistentDataContainer(ItemStack itemStack) {
            String serializedLocation;
            if (selectedLocation == null) {
                serializedLocation = "";
            } else {
                serializedLocation = serializeLocation(selectedLocation);
            }
            itemStack.editPersistentDataContainer(pdc -> {
                pdc.set(WRENCH_SELECTED_READER, PersistentDataType.STRING, serializedLocation);
            });
        }
    }
}
