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
        changeLogs.add(new ChangeLogEntry(ChangeType.ADD, "Added ShotbowMode on Timer"));
        changeLogs.add(new ChangeLogEntry(ChangeType.ADD, "Added Step On HoleSnap(Beta)"));
        changeLogs.add(new ChangeLogEntry(ChangeType.IMPROVE,"Improved Criticals(Beta)"));
        changeLogs.add(new ChangeLogEntry(ChangeType.IMPROVE,"Improved AntiDump"));
    }

    public static List<ChangeLogEntry> getChangeLogs() {
        return changeLogs;
    }
}