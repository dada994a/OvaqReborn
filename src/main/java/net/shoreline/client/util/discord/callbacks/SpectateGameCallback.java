package net.shoreline.client.util.discord.callbacks;

import com.sun.jna.Callback;

public interface SpectateGameCallback extends Callback {
    void apply(final String p0);
}