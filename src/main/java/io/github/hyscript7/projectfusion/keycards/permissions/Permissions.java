package io.github.hyscript7.projectfusion.keycards.permissions;

/**
 * Central registry of all permission nodes used by this plugin.
 * <p>
 * Permission structure:
 * <pre>
 *   projectfusion.keycards
 *   ├── use.<level>   — right to swipe a keycard of that specific access level
 *   └── command
 *       ├── get.keycard  — /keycards get keycard
 *       └── get.wrench   — /keycards get wrench
 * </pre>
 * Permission managers (LuckPerms, etc.) that support wildcards can grant
 * {@code projectfusion.keycards.use.*} to allow all keycard levels at once.
 */
public final class Permissions {

    private Permissions() {}

    private static final String ROOT = "projectfusion.keycards";

    // -------------------------------------------------------------------------
    // Keycard use
    // -------------------------------------------------------------------------

    /** Root node for keycard-use permissions (useful for wildcard grants). */
    public static final String USE_ROOT = ROOT + ".use";

    /**
     * Returns the permission node that allows swiping a keycard of the given level.
     * <p>
     * Example: {@code projectfusion.keycards.use.5}
     *
     * @param level the keycard level (0–255)
     * @return the fully-qualified permission node
     */
    public static String useKeycard(int level) {
        return USE_ROOT + "." + level;
    }

    // -------------------------------------------------------------------------
    // Commands
    // -------------------------------------------------------------------------

    /** Root for all command permissions. */
    public static final String COMMAND_ROOT = ROOT + ".command";

    /** Permission required to run {@code /keycards get keycard}. */
    public static final String COMMAND_GET_KEYCARD = COMMAND_ROOT + ".get.keycard";

    /** Permission required to run {@code /keycards get wrench}. */
    public static final String COMMAND_GET_WRENCH = COMMAND_ROOT + ".get.wrench";
}
