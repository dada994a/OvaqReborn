package net.shoreline.client.util.discord;

import java.util.Arrays;
import java.util.List;
import net.shoreline.client.util.discord.callbacks.JoinGameCallback;
import net.shoreline.client.util.discord.callbacks.ErroredCallback;
import net.shoreline.client.util.discord.callbacks.ReadyCallback;
import net.shoreline.client.util.discord.callbacks.SpectateGameCallback;
import net.shoreline.client.util.discord.callbacks.JoinRequestCallback;
import net.shoreline.client.util.discord.callbacks.DisconnectedCallback;
import com.sun.jna.Structure;

public class DiscordEventHandlers extends Structure {
    public DisconnectedCallback disconnected;
    public JoinRequestCallback joinRequest;
    public SpectateGameCallback spectateGame;
    public ReadyCallback ready;
    public ErroredCallback errored;
    public JoinGameCallback joinGame;
    
    protected List<String> getFieldOrder() {
        return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
    }
    
   
}
