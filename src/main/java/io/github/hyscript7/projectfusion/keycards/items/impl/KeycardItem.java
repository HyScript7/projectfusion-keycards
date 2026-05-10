package io.github.hyscript7.projectfusion.keycards.items.impl;

import io.github.hyscript7.projectfusion.keycards.ProjectFusionKeycards;
import io.github.hyscript7.projectfusion.keycards.ReaderManager;
import io.github.hyscript7.projectfusion.keycards.items.CustomItem;
import io.github.hyscript7.projectfusion.keycards.permissions.Permissions;
import io.github.hyscript7.projectfusion.keycards.readers.Reader;
import io.github.hyscript7.projectfusion.keycards.readers.ReaderService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KeycardItem extends CustomItem {

    public static final NamespacedKey KEYCARD_LEVEL =
            new NamespacedKey(ProjectFusionKeycards.namespace, "keycard_level");
    public static final NamespacedKey KEYCARD_WANTS_EXACT_MATCH =
            new NamespacedKey(ProjectFusionKeycards.namespace, "keycard_exact_match");

    private final ReaderManager readerManager;
    private final ReaderService readerService;

    public KeycardItem(ReaderManager readerManager, ReaderService readerService) {
        super("keycard", Material.BOOK.getKey(), Component.text("Keycard"), List.of());
        this.readerManager = readerManager;
        this.readerService = readerService;
    }

    public ItemStack createItemStackWithLevel(int level, boolean wantsExactMatch) {
        ItemStack stack = createItemStack();
        ItemMeta itemMeta = stack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(KEYCARD_LEVEL, PersistentDataType.INTEGER, level);
        itemMeta.getPersistentDataContainer().set(KEYCARD_WANTS_EXACT_MATCH, PersistentDataType.BOOLEAN, wantsExactMatch);

        List<Component> lore = new ArrayList<>();
        if (itemMeta.hasLore() && itemMeta.lore() != null) {
            lore.addAll(itemMeta.lore());
        }
        lore.add(Component.text("Level: " + level));
        if (wantsExactMatch) {
            lore.add(Component.text("Wants exact match"));
        }
        itemMeta.lore(lore);
        itemMeta.itemName(Component.text("Lv" + level + " Keycard"));
        stack.setItemMeta(itemMeta);
        return stack;
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
        if (clickedBlock == null || clickedBlock.getType().equals(Material.AIR)) return;

        int level = getLevel(itemStack);
        if (level == -1) return;

        // --- Permission check ---
        // Players need projectfusion.keycards.use.<level> to swipe this keycard.
        // Permission managers can grant projectfusion.keycards.use.* as a wildcard.
        if (!player.hasPermission(Permissions.useKeycard(level))) {
            accessDenied(player);
            return;
        }

        Reader reader = readerService.getReader(clickedBlock.getLocation());
        if (reader == null) return;

        boolean exactRequired = reader.isRequireExactLevelMatch() || getWantsExactMatch(itemStack);
        if (exactRequired && reader.getMinimalLevel() != level) {
            accessDenied(player);
            return;
        }
        if (reader.getMinimalLevel() > level) {
            accessDenied(player);
            return;
        }

        readerManager.trigger(reader);
        accessGranted(player);
    }

    // -------------------------------------------------------------------------
    // Feedback helpers
    // -------------------------------------------------------------------------

    private void accessGranted(Player player) {
        player.sendActionBar(Component.text("Access Granted").color(NamedTextColor.GREEN));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f, 1f);
    }

    private void accessDenied(Player player) {
        player.sendActionBar(Component.text("Access Denied").color(NamedTextColor.DARK_RED));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
    }

    // -------------------------------------------------------------------------
    // PDC helpers
    // -------------------------------------------------------------------------

    private boolean getWantsExactMatch(ItemStack itemStack) {
        var pdc = itemStack.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(KEYCARD_WANTS_EXACT_MATCH)) return false;
        Boolean v = pdc.get(KEYCARD_WANTS_EXACT_MATCH, PersistentDataType.BOOLEAN);
        return v != null && v;
    }

    private int getLevel(ItemStack itemStack) {
        var pdc = itemStack.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(KEYCARD_LEVEL)) return -1;
        Integer v = pdc.get(KEYCARD_LEVEL, PersistentDataType.INTEGER);
        return v != null ? v : -1;
    }
}
