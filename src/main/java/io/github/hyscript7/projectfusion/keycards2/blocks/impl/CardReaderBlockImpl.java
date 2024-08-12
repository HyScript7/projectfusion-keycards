package io.github.hyscript7.projectfusion.keycards2.blocks.impl;

import io.github.hyscript7.projectfusion.keycards2.KeyCardsPlugin;
import io.github.hyscript7.projectfusion.keycards2.blocks.interfaces.CardReaderBlock;
import io.github.hyscript7.projectfusion.keycards2.data.interfaces.CardReader;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

public class CardReaderBlockImpl implements CardReaderBlock {
    private final KeyCardsPlugin plugin;
    private final CardReader cardReader;

    public CardReaderBlockImpl(CardReader cardReader) {
        this.plugin = KeyCardsPlugin.getPlugin(KeyCardsPlugin.class);
        this.cardReader = cardReader;
    }

    private @Nullable Block getSecondBlockIfExists(Block block) {
        if (block.getType().equals(Material.IRON_DOOR)) {
            if (block.getBlockData() instanceof Door d) {
                if(d.getHalf().equals(Bisected.Half.TOP)) {
                    Block below = block.getRelative(BlockFace.DOWN);
                    if (below.getType().equals(Material.IRON_DOOR)) {
                        return below;
                    }
                } else if (d.getHalf().equals(Bisected.Half.BOTTOM)) {
                    Block above = block.getRelative(BlockFace.UP);
                    if (above.getType().equals(Material.IRON_DOOR)) {
                        return above;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void open() {
        Block block = cardReader.getBlock();
        Block secondBlock = getSecondBlockIfExists(block);
        handleOpen(block, (Door) block.getBlockData());
        if (secondBlock != null) {
            handleOpen(secondBlock, (Door) secondBlock.getBlockData());
        }
        close();
    }

    private void handleOpen(Block block, Door door) {
        door.setOpen(true);
        block.setBlockData(door);
    }

    private void handleClose(Block block, Door door) {
        door.setOpen(false);
        block.setBlockData(door);
    }

    @Override
    public void close() {
        Block block = cardReader.getBlock();
        Block secondBlock = getSecondBlockIfExists(block);
        new BukkitRunnable() {
            @Override
            public void run() {
                handleClose(block, (Door) block.getBlockData());
                if (secondBlock != null) {
                    handleClose(secondBlock, (Door) secondBlock.getBlockData());
                }
            }
        }.runTaskLater(plugin, cardReader.getDuration() * 20L);

    }

    @Override
    public void destroy() {
        plugin.getKeyCardsConfig().getPluginData().getCardReaderAggregator().delete(cardReader);
    }

    @Override
    public @Nullable CardReader getCardReader() {
        return cardReader;
    }
}
