package net.shoreline.client.impl.manager.client;

import java.security.MessageDigest;

public class HwidManager {

    /**
     * Method to retrieve the hardware ID (HWID)
     * @return HWID in MD5 format
     */
    public static String getHWID() {
        try {
            String toEncrypt = System.getenv("COMPUTERNAME") +
                    System.getProperty("user.name") +
                    System.getenv("PROCESSOR_IDENTIFIER") +
                    System.getenv("PROCESSOR_LEVEL");

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());

            StringBuilder hexString = new StringBuilder();
            byte[] byteData = md.digest();
            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
