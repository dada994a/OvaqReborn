package net.shoreline.client.impl.module.misc;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.util.chat.ChatUtil;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * AutoEZ Module
 * Sends a random insult in chat after killing a player.
 */
public class AutoEzModule extends ToggleModule {

    private final String[] customMessages = new String[]{
            "EZ $NAME",
            "Even Skid client can win LOL $NAME,",
            "You just got ez'd $NAME!",
            "Ez! OvaqReborn On Top! $NAME ",
            "GG $NAME"
    };

    private final Set<String> killedPlayers = new HashSet<>();
    private final Random random = new Random();

    public AutoEzModule() {
        super("AutoEz", "Sends a random EZ message after you kill a player.", ModuleCategory.MISC);
    }

    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        ClientPlayerEntity player = mc.player;

        for (PlayerEntity entity : mc.world.getPlayers()) {
            if (!entity.equals(player) && !entity.isAlive() && !killedPlayers.contains(entity.getName().getString())) {
                handleKill(player, entity);
                killedPlayers.add(entity.getName().getString());
            }
        }
    }


    private void handleKill(ClientPlayerEntity player, PlayerEntity killedEntity) {
        String selectedMessage = customMessages[random.nextInt(customMessages.length)]
                .replace("$NAME", killedEntity.getName().getString());

        ChatUtil.serverSendMessage(selectedMessage);
    }

    @Override
    public void onDisable() {
        killedPlayers.clear();
    }
}
