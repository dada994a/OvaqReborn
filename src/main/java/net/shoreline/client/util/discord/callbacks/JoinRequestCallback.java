package net.shoreline.client.util.discord.callbacks;

import net.shoreline.client.util.discord.DiscordUser;
import com.sun.jna.Callback;

public interface JoinRequestCallback extends Callback {
    void apply(final DiscordUser p0);
}
