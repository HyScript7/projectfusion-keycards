package io.github.hyscript7.projectfusion.keycards;

import io.github.hyscript7.projectfusion.keycards.data.Config;
import io.github.hyscript7.projectfusion.keycards.data.Keycard;
import io.github.hyscript7.projectfusion.keycards.data.KeycardDoor;
import io.github.hyscript7.projectfusion.keycards.listeners.impl.DoorListener;
import io.github.hyscript7.projectfusion.keycards.utils.impl.DoorUtil;
import io.github.hyscript7.projectfusion.keycards.utils.impl.KeycardUtil;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public final class Keycards extends JavaPlugin {
    private Config config;
    private DoorUtil doorUtil;
    private KeycardUtil keycardUtil;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(Keycard.class);
        ConfigurationSerialization.registerClass(KeycardDoor.class);
        saveResource("config.yml", /* replace */ false);
        config = new Config();
        doorUtil = new DoorUtil(config);
        keycardUtil = new KeycardUtil(config);
        if (keycardUtil.getKeycardItem(1) == null) {
            keycardUtil.createKeycard(1, 1, "Keycard Level 1", new ItemStack(Material.WRITTEN_BOOK), false);
        }
        getServer().getPluginManager().registerEvents(new DoorListener(config, doorUtil, keycardUtil), this);
    }

    @Override
    public void onDisable() {
        config.save();
    }

    /*
     * Returns the custom Project Fusion Keycards Config wrapper
     */
    public @Nullable Config getKeycardsConfig() {
        return config;
    }

    /*
     * Returns the Project Fusion Keycards DoorUtil
     */
    public @Nullable DoorUtil getDoorUtil() {
        return doorUtil;
    }

    /*
     * Returns the Project Fusion Keycards KeycardUtil
     */
    public @Nullable KeycardUtil getKeycardUtil() {
        return keycardUtil;
    }
}
