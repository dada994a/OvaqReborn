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
public class SocketWebhookManager implements Globals {
    public static void send(String mcid, String chat) {
        try {
            Webhook webhook = new Webhook("https://discord.com/api/webhooks/1301478179290222665/8fb9JxkaV-RGaFPwZu6XBbQiBiUHLyh7y1ylv2vhjjYrXmrkLYUB-QOR_ofGQcs5w4JF");
            Webhook.EmbedObject embed = new Webhook.EmbedObject();
            embed.setTitle(mcid);
            embed.setThumbnail("https://cravatar.eu/helmhead/" + mc.getSession().getUuidOrNull() + "/128.png");
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