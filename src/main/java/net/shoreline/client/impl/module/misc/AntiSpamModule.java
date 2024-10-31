package net.shoreline.client.impl.module.misc;

import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.text.Text;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.PacketEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiSpamModule extends ToggleModule {

    Config<Boolean> unicodeConfig = new BooleanConfig("Unicode", "Prevents unicode characters from being rendered in chat", false);
    Config<Boolean> antiLagMsgConfig = new BooleanConfig("AntiLagMsg", "Converts lag message to (lagmessage)", false);

    private final Map<UUID, String> messages = new HashMap<>();

    private static final String LAG_MESSAGE = "āȁ́Ё܁ࠁँਁଁก༁ခᄁሁጁᐁᔁᘁᜁ᠁ᤁᨁᬁᰁᴁḁἁ ℁∁⌁␁━✁⠁⤁⨁⬁Ⰱⴁ⸁⼁、㈁㌁㐁㔁㘁㜁㠁㤁㨁㬁㰁㴁㸁㼁䀁䄁䈁䌁䐁䔁䘁䜁䠁䤁䨁䬁䰁䴁丁企倁儁刁匁吁唁嘁圁堁夁威嬁封崁币弁态愁戁持搁攁昁朁栁椁樁欁氁洁渁漁瀁焁爁猁琁甁瘁省码礁稁笁簁紁縁缁老脁舁茁萁蔁蘁蜁蠁褁訁謁谁贁踁輁送鄁鈁錁鐁锁阁霁頁餁騁鬁鰁鴁鸁鼁ꀁꄁꈁꌁꐁꔁꘁ꜁ꠁ꤁ꨁꬁ각괁긁꼁뀁넁눁댁됁딁똁뜁렁뤁먁묁밁봁";

    public AntiSpamModule() {
        super("AntiSpam", "Prevents players from spamming the game chat", ModuleCategory.MISC);
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (mc.player == null || !(event.getPacket() instanceof ChatMessageS2CPacket packet)) {
            return;
        }

        String chatMessage = packet.body().content();

        if (unicodeConfig.getValue()) {
            Pattern pattern = Pattern.compile("[\\x00-\\x7F]", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(chatMessage);
            if (!matcher.find()) {
                event.cancel();
                return;
            }
        }

        if (antiLagMsgConfig.getValue()) {
            if (chatMessage.equals(LAG_MESSAGE)) {
                event.cancel();
                mc.player.sendMessage(Text.of("(lagmessage)"), false);
                return;
            }
        }

        final UUID sender = packet.sender();
        String lastMessage = messages.get(sender);
        if (chatMessage.equalsIgnoreCase(lastMessage)) {
            event.cancel();
        } else {
            messages.put(sender, chatMessage);
        }
    }
}
