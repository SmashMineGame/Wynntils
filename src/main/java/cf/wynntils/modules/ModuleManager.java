package cf.wynntils.modules;


import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.modules.capes.CapesModule;
import cf.wynntils.modules.core.CoreModule;
import cf.wynntils.modules.richpresence.RichPresenceModule;
import cf.wynntils.modules.utilities.UtilitiesModule;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ModuleManager {

    public static void initModules() {
        FrameworkManager.registerModule(new RichPresenceModule());
        FrameworkManager.registerModule(new CoreModule());
        FrameworkManager.registerModule(new UtilitiesModule());
        FrameworkManager.registerModule(new CapesModule());
    }

}
