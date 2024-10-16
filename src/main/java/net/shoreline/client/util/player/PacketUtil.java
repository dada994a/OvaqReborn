package net.shoreline.client.util.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.shoreline.client.util.Globals;

public class PacketUtil implements Globals {

    public static PacketUtil INSTANCE = new PacketUtil();

    public void sendPacket(Packet packet) {
        this.getMc().getNetworkHandler().sendPacket(packet);
    }

    public MinecraftClient getMc() {
        return MinecraftClient.getInstance();
    }

}
