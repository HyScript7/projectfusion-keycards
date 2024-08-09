package io.github.hyscript7.projectfusion.keycards.utils.interfaces;

import io.github.hyscript7.projectfusion.keycards.data.Keycard;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IKeycardUtil {
    void createKeycard(int id, int level, String name, ItemStack item, boolean requirePermission);
    ItemStack getKeycardItem(int id);

    void deleteKeycard(int id);

    boolean isKeycardItem(ItemStack item);
    boolean isKeycardItemValid(ItemStack item);
    Keycard getKeycardFromItem(ItemStack item);
    boolean canPlayerUseKeycard(Player player, Keycard keycard);
}
