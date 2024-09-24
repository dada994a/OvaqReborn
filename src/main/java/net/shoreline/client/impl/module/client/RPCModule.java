package net.shoreline.client.impl.module.client;

import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.manager.client.DiscordManager;

/**
 * RPCModule - Enables or disables Discord RPC when the module is toggled
 * @since 1.0
 */
public class RPCModule extends ToggleModule {

    public static DiscordManager RPC = new DiscordManager();

    public RPCModule() {
        super("RPC", "Discord RPC", ModuleCategory.CLIENT);
    }

    @Override
    public void onEnable() {
        startDiscordRPC();
    }

    @Override
    public void onDisable() {
        stopDiscordRPC();
    }

    private void startDiscordRPC() {
        if (RPC != null) {
            RPC.startRPC();
        }
    }

    private void stopDiscordRPC() {
        if (RPC != null) {
            RPC.stopRPC();
        }
    }
}
