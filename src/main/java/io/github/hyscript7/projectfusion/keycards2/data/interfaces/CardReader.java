package io.github.hyscript7.projectfusion.keycards2.data.interfaces;

import org.bukkit.Location;
import org.bukkit.block.Block;

/*
 * This interface describes operations possible on a CardReader object in stored Plugin Data.
 */
public interface CardReader {
    int getId();
    int getAccessLevel();
    Location getLocation();
    Block getBlock();
    long getDuration();
    boolean getRequiresExactMatch();

    void setAccessLevel(int accessLevel);
    void setDuration(long duration);
    void setRequiresExactMatch(boolean requiresExactMatch);
}
