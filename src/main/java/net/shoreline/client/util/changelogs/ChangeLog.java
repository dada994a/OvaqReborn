package net.shoreline.client.util.changelogs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class ChangeLog {
    private static final List<ChangeLogEntry> changeLogs = new ArrayList<>();

    static {
        changeLogs.add(new ChangeLogEntry(ChangeType.ADD, "Added Main Menu"));
        changeLogs.add(new ChangeLogEntry(ChangeType.ADD, "Added PearlESP"));
        changeLogs.add(new ChangeLogEntry(ChangeType.REMOVE, "Aura Circle Render Mode"));
    }

    public static List<ChangeLogEntry> getChangeLogs() {
        return changeLogs;
    }
}