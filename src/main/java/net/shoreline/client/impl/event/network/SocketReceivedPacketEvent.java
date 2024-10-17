package net.shoreline.client.impl.event.network;

import net.shoreline.client.api.event.Event;

/**
 * @author OvaqReborn
 */
public class SocketReceivedPacketEvent extends Event {
    private final String nick;
    private final String text;

    public SocketReceivedPacketEvent(String nick, String text) {
        this.nick = nick;
        this.text = text;
    }

    public String getNick() {
        return this.nick;
    }

    public String getText() {
        return this.text;
    }
}
