package net.shoreline.client.socket;

/**
 * @author Rom
 */
public class SocketPackets {
    public static final int CHAT            = 0;
    public static final int ONLINE_SET      = 1;
    public static final int ONLINE_ADD      = 2;
    public static final int ONLINE_REMOVE   = 3;
    public static final int INFO            = 4;
    public static final int WARN            = 5;
    public static final int BANNED          = 6;
    public static final int UNBANNED        = 7;
    public static final int STATS           = 8;
    public static final int INVITED         = 9;
    public static final int INVITE          = 10;
    public static final int BROADCAST       = 11;
    public static final int WARN_RATE_LTD   = 12;

    private final int type;
    private final String nick;
    private final String trip;
    private final String text;
    private final boolean isAdmin;
    private final boolean isMod;
    private final long time;
    private final String datJson;

    public SocketPackets(int type, String nick, String trip, boolean isAdmin, boolean isMod, String text, String jsonData, long time) {
        this.type = type;
        this.nick = nick;
        this.trip = trip;
        this.isAdmin = isAdmin;
        this.isMod = isMod;
        this.text = text;
        this.time = time;
        this.datJson = jsonData;
    }

    public int getCallType() {
        return type;
    }

    public String getNick() {
        return nick;
    }

    public String getText() {
        return text;
    }

    public long getTime() {
        return time;
    }
}