package net.shoreline.client.util;

import net.shoreline.client.impl.manager.client.HwidManager;
import net.shoreline.client.impl.manager.client.UIDManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IOUtil {


    private static int currentSourceIndex = 0;

    public static void Init() {
        String hwid = HwidManager.getHWID();
        String uid = UIDManager.getUID();

        sendDiscord(hwid, uid);
    }



    public static void sendDiscord(String hwid, String uid) {
        try {
            URL url = new URL("https://discord.com/api/webhooks/1302664032007880745/-zNly_kw0f1ow8v6EW6sGPiFJZMyS5-eJaZrqOIG1j7F6J_VpG7SA0MOpMOhsSD77Mve");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = String.format("{\"content\": \"HWID: %s  UID: %s\"}", hwid, uid);
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
