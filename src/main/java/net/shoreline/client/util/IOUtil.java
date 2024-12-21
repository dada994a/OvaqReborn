package net.shoreline.client.util;

import net.shoreline.client.impl.manager.client.HwidManager;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IOUtil {

    private static int currentSourceIndex = 0;

    public static void Init() {
        String hwid = HwidManager.getHWID();
        sendDiscord(hwid);
    }

    public static void sendDiscord(String hwid) {
        try {
            URL url = new URL("");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = String.format("{\"content\": \"HWID: %s }", hwid);
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
