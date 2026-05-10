package io.github.hyscript7.projectfusion.keycards.items.impl;

import io.github.hyscript7.projectfusion.keycards.LinkManager;
import io.github.hyscript7.projectfusion.keycards.ProjectFusionKeycards;
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
        lore.add(Component.text("-- Selected Reader --"));
        if (settings.getSelectedLocation() != null) {
            if (readerService.getReader(settings.getSelectedLocation()) == null) {
                lore.add(Component.text("Invalid").color(NamedTextColor.RED));
            } else {
                lore.add(Component.text("World: " + settings.getSelectedLocation().getWorld().getKey()));
                lore.add(Component.text(
                        "X: " + settings.getSelectedLocation().getBlockX()
                                + " Y: " + settings.getSelectedLocation().getBlockY()
                                + " Z: " + settings.getSelectedLocation().getBlockZ()
                ));
            }
        } else {
            lore.add(Component.text("None").color(NamedTextColor.GRAY));
        }
        lore.add(Component.text(""));
        lore.add(Component.text("Right click a reader to select it."));
        meta.lore(lore);
        itemStack.setItemMeta(meta);
    }

    @Override
    public void onLeftClick(@NotNull ItemStack itemStack, @NotNull Player player,
                            @Nullable Block clickedBlock, @Nullable Location clickedLocation,
                            boolean isOffHanded) {
        // No-op
    }

    @Override
    public void onRightClick(@NotNull ItemStack itemStack, @NotNull Player player,
                             @Nullable Block clickedBlock, @Nullable Location clickedLocation,
                             boolean isOffHanded) {
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
            player.sendActionBar(Component.text("Selected reader no longer exists!").color(NamedTextColor.RED));
            // Clear the stale selection
            WrenchSettings cleared = WrenchSettings.defaultSettings();
            cleared.saveToPersistentDataContainer(itemStack);
            updateItemLore(itemStack, cleared);
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

        // --- Bug fix: check whether this exact reader→appliance pair is already registered ---
        if (linkManager.isLinked(selectedReader, appliance)) {
            player.sendActionBar(
                    Component.text("This reader is already linked to this appliance!").color(NamedTextColor.YELLOW)
            );
            return;
        }

        linkManager.link(selectedReader, appliance);

        if (isNew) {
            player.sendActionBar(Component.text("New appliance created and linked").color(NamedTextColor.GREEN));
        } else {
            player.sendActionBar(Component.text("Reader linked to existing appliance").color(NamedTextColor.GREEN));
        }
    }

    // -------------------------------------------------------------------------
    // Wrench state stored in the item's PDC
    // -------------------------------------------------------------------------

    @Getter
    @Setter
    private static class WrenchSettings {

        // Namespace aligned with the rest of the plugin
        public static final NamespacedKey WRENCH_SELECTED_READER =
                new NamespacedKey(ProjectFusionKeycards.namespace, "wrench_selected_reader");

        private @Nullable Location selectedLocation;

        public WrenchSettings(@Nullable Location selectedLocation) {
            this.selectedLocation = selectedLocation;
        }

        public WrenchSettings() {
            this.selectedLocation = null;
        }

        public static WrenchSettings defaultSettings() {
            return new WrenchSettings();
        }

        public static WrenchSettings readFromPersistentDataContainer(ItemStack itemStack) {
            String serialized = itemStack.getPersistentDataContainer()
                    .getOrDefault(WRENCH_SELECTED_READER, PersistentDataType.STRING, "");
            if (serialized.isBlank()) return new WrenchSettings();
            return new WrenchSettings(deserializeLocation(serialized));
        }

        public void saveToPersistentDataContainer(ItemStack itemStack) {
            String serialized = selectedLocation == null ? "" : serializeLocation(selectedLocation);
            itemStack.editPersistentDataContainer(pdc ->
                    pdc.set(WRENCH_SELECTED_READER, PersistentDataType.STRING, serialized)
            );
        }

        private static String serializeLocation(Location location) {
            return location.getWorld().getKey()
                    + ";" + location.getBlockX()
                    + ";" + location.getBlockY()
                    + ";" + location.getBlockZ();
        }

        private static Location deserializeLocation(String s) {
            String[] parts = s.split(";");
            World world = Bukkit.getWorld(Objects.requireNonNull(NamespacedKey.fromString(parts[0])));
            return new Location(world,
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]));
        }
    }
}
