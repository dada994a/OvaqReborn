package net.shoreline.client.impl.module.client;

import net.minecraft.util.Identifier;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public final class CapesModule extends ToggleModule {
    public static final Identifier TEXTURE = new Identifier("ovaqreborn", "capes/cape.png");
    //pastebinからuuidを取得する方式に変更　リモートで変更出来るように
    public static List<String> whitelist = new ArrayList<>();
    static {
        try {
            URL url = new URL("https://pastebin.com/raw/8tq43e69");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    whitelist.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Config<Boolean> userConfig = new BooleanConfig("User Cape", "show users", true);
    Config<Boolean> optifineConfig = new BooleanConfig("Optifine", "If to show optifine capes", true);

    public CapesModule() {
        super("Capes", "Shows player capes", ModuleCategory.CLIENT);
        enable();
    }

    public Config<Boolean> getOptifineConfig() {
        return optifineConfig;
    }

    public Config<Boolean> getUserConfig() {
        return userConfig;
    }
}
