package io.github.hyscript7.projectfusion.keycards2.items.interfaces;

import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CardReaderItemFactory {
    /*
     * Returns the CardReaderItem representation of the data stored in an ItemStack
     * Is null when the ItemStack does not represent a placeable CardReader
     */
    @Nullable CardReaderItem fromItemStack(ItemStack itemStack);

    /*
     * Returns the CardReaderItem representation of data stored in a CardReader.
     * Can be used to obtain a placeable CardReader ItemStack with identical settings.
     */
    @Nullable
    CardReaderItem fromExistingReader(CardReader cardReader);
}
