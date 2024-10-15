package net.shoreline.client.impl.module.misc;

import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;

public class AutoEzModule extends ToggleModule {

    public AutoEzModule() {
        super("AutoEz", "Sends a random EZ message after you kill a player.", ModuleCategory.MISC);
    }

}