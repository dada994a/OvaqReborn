package net.shoreline.client.impl.manager.client;

import net.shoreline.client.OvaqReborn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class UIDManager {
    public String getUID() {
        String a = HwidManager.getHWID();
        if (a.equals("Error")) {
            return "-1";
        }
        try {
            URL url = new URL("https://pastebin.com/raw/AtsAtG0Y");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            int i = 1;
            while ((line = reader.readLine()) != null) {
                if (line.contains(a)) {
                    return String.valueOf(i);
                }
                i++;
            }
            reader.close();
        } catch (Exception e) {
            OvaqReborn.LOGGER.error("error i: {}", e.getMessage());
        }
        return "-1";
    }
}