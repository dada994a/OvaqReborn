package net.shoreline.client.socket;

import net.shoreline.client.util.Globals;
import net.shoreline.client.util.Webhook;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author Rom
 */
//まだDiscord Bot client未対応。Chatをリレーできるのはゲーム内にいるプレイヤーのみ。
//(Webhookには送信だけ可能)
public class SocketWebhookManager implements Globals {
    public static void send(String mcid, String chat) {
        try {
            Webhook webhook = new Webhook("https://discord.com/api/webhooks/1296264013306662929/tuvKXgJOJWZNCMaXpqKHEhSXHgdGk22Wfa3oqs63zQ2cUMd9IPmEBv16u3Omn1QggV1E");
            Webhook.EmbedObject embed = new Webhook.EmbedObject();
            embed.setTitle(mcid);
            embed.setThumbnail("https://crafatar.com/avatars/" + mc.getSession().getUuidOrNull() + "?size=128&overlay");
            embed.setDescription(chat);
            embed.setColor(new Color(generateRandomColor()));
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);

            webhook.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return (formatter.format(date));
    }

    private static int generateRandomColor() {
        Random random = new Random();
        return random.nextInt(16777216);
    }
}