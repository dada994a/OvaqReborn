package net.shoreline.client.util.string;

import java.util.List;

/**
 * @author linus
 * @since 1.0
 */
public class StringUtil {
    /**
     * Capitalises a given string
     *
     * @param string The string to capitalise
     * @return The string with the first letter capitalised
     */
    public static String capitalize(final String string) {
        if (string.length() != 0) {
            return Character.toTitleCase(string.charAt(0)) + string.substring(1);
        }
        return "";
    }

    public static int indexOfStartingWith(List<String> list, String startingWith) {
        for (var i = 0; i < list.size(); i++) {
            var string = list.get(i);

            if (startingWith.isEmpty()) {
                if (string.isEmpty()) {
                    return i;
                }
            } else if (string.startsWith(startingWith)) {
                return i;
            }
        }

        return -1;
    }

}
