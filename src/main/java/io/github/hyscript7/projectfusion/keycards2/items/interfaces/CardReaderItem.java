package io.github.hyscript7.projectfusion.keycards2.items.interfaces;

import org.bukkit.inventory.ItemStack;

public interface CardReaderItem {
    int getLevel();
    long getDuration();
    boolean getRequiresExactMatch();
    ItemStack asItemStack();
}
