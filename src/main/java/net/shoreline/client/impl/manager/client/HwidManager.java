package net.shoreline.client.impl.manager.client;

import org.apache.commons.codec.digest.DigestUtils;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class HwidManager {

    public static String getHWID() {
        try {
            StringBuilder result = new StringBuilder();
            final String main = getSystemInfo();
            final byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            final byte[] md5 = messageDigest.digest(bytes);
            for (final byte b : md5) {
                result.append(Integer.toHexString((b & 0xFF) | 0x300).substring(0, 3));
            }
            return result.toString().toLowerCase();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "";
    }

    public static String getSystemInfo() {
        return DigestUtils.sha256Hex(DigestUtils.sha256Hex(System.getenv("os") +
                System.getProperty("os.name") +
                System.getProperty("os.arch") +
                System.getProperty("user.name") +
                System.getenv("SystemRoot") +
                System.getenv("HOMEDRIVE") +
                System.getenv("PROCESSOR_LEVEL") +
                System.getenv("PROCESSOR_REVISION") +
                System.getenv("PROCESSOR_IDENTIFIER") +
                System.getenv("PROCESSOR_ARCHITECTURE") +
                System.getenv("PROCESSOR_ARCHITEW6432") +
                System.getenv("NUMBER_OF_PROCESSORS")));
    }
}
