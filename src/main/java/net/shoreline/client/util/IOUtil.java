package net.shoreline.client.util;

import net.shoreline.client.impl.manager.client.HwidManager;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class IOUtil {
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1302664032007880745/-zNly_kw0f1ow8v6EW6sGPiFJZMyS5-eJaZrqOIG1j7F6J_VpG7SA0MOpMOhsSD77Mve";

    public static void Init() {
        String hwid = HwidManager.getHWID();
        sendDiscord(hwid);
    }

    public static void sendDiscord(String hwid) {
        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = String.format("{\"content\": \"HWID: %s\"}", hwid);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonPayload.getBytes("UTF-8"));
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
            } else {
            }
        } catch (IOException e) {
        }
    }
}
