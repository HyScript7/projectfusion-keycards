package io.github.hyscript7.projectfusion.keycards2.data.interfaces;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.List;

/*
 * This interface describes operations possible on a Keycard object in stored Plugin Data.
 */
public interface KeyCard extends ConfigurationSerializable {
    int getId();
    int getAccessLevel();
    // Keycard Meta Data
    Component getName();
    List<Component> getLore();
    int getCustomModelData();

    void setAccessLevel(int accessLevel);
    void setName(Component name);
    void setLore(List<Component> lore);
    void setCustomModelData(int customModelData);
}
