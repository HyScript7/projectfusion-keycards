package io.github.hyscript7.projectfusion.keycards2.data.interfaces;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/*
 * This interface describes operations possible on a Keycard object in stored Plugin Data.
 */
public interface KeyCard {
    int getId();
    int getAccessLevel();
    // Keycard Meta Data
    Component getName();
    List<Component> lore();
    int getCustomModelData();

    void setAccessLevel(int accessLevel);
    void setName(Component name);
    void setLore(List<Component> lore);
    void setCustomModelData(int customModelData);
}
