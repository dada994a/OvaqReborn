package net.shoreline.client.socket;

import net.shoreline.client.socket.exception.SocketNickErrorException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rom
 */
public class SocketClientUtility {

    public static boolean verifyNick(String nick) throws SocketNickErrorException {
        if(nick != null) {
            return nick.matches("^[a-zA-Z0-9_]{1,"+24+"}$");
        }
        throw new SocketNickErrorException();
    }

    public static boolean verifyURI(String URI) {
        try {
            new URI(URI);
        } catch (URISyntaxException ex) {
            Logger.getLogger(SocketClientUtility.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return (URI.startsWith("ws://")||URI.startsWith("wss://"));
    }
}
