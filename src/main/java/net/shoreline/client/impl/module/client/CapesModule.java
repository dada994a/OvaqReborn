package net.shoreline.client.impl.module.client;

import net.minecraft.util.Identifier;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public final class CapesModule extends ToggleModule
{
    public static final Identifier TEXTURE = new Identifier("ovaqreborn", "capes/cape.png");


    public static final String[] whitelist = new String[]{
            //----------------------------------- Devs
            "6ab3d75d-cadb-4ff0-b01f-bacf006dd552", //hypinohaizin
            "3a70e7ca-3a3b-4b4d-af25-7229ae1a555d", //kisqra
            "76a84846-e11a-456c-b9ff-10caa425e421", //NaaNaa

            //----------------------------------- Users
            "6320125d-e6c4-42e5-9a29-25f8d91d049f", //taketyan
            "a0e86869-e2c8-43ed-a79c-2525df4e6e5d", //momo
            "00853f35-fdc3-4864-8d2f-f5283708af39", ///TouFu
            "e8ce8d65-0c41-407d-a27b-027a00eb57af", //izana
            "164b5535-ded6-4aba-8906-a44755e539de", //smoky
            "8699afac-ea67-43a4-a5a4-03c686a77a1b", //miso
            "45fa4452-d07c-424a-b7bf-addeb6d1942b" //Dacho
    };

    Config<Boolean> userConfig = new BooleanConfig("User Cape", "show users", true);
    Config<Boolean> optifineConfig = new BooleanConfig("Optifine", "If to show optifine capes", true);

    public CapesModule()
    {
        super("Capes", "Shows player capes", ModuleCategory.CLIENT);
        enable();
    }

    public Config<Boolean> getOptifineConfig()
    {
        return optifineConfig;
    }

    public Config<Boolean> getUserConfig()
    {
        return userConfig;
    }
}
