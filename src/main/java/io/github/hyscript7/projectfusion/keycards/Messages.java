package io.github.hyscript7.projectfusion.keycards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public record Messages() {
    public static final Component PREFIX = Component.text("[Keycards] ");
    public static final Component NO_PERMISSION = Component.text("You do not have permission to use this command.", TextColor.color(0xFF0000));
    public static final Component ACCESS_DENIED = Component.text("Access denied.", TextColor.color(0xFF0000));
    public static final Component ACCESS_GRANTED = Component.text("Access granted.", TextColor.color(0x00FF00));
    public static final Component DOOR_CREATED = Component.text("Door created.", TextColor.color(0x00FF00));
    public static final Component DOOR_DELETED = Component.text("Door deleted.", TextColor.color(0xFF0000));
    public static final Component KEYCARD_CREATED = Component.text("Keycard created.", TextColor.color(0x00FF00));
    public static final Component KEYCARD_DELETED = Component.text("Keycard deleted.", TextColor.color(0xFF0000));
    public static final Component KEYCARD_GIVE_SUCCESS = Component.text("Keycard given.", TextColor.color(0x00FF00));
    public static final Component DOOR_ITEM_GIVE_SUCCESS = Component.text("Door item given.", TextColor.color(0x00FF00));
}
