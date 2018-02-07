package cf.wynntils.core.framework.instances;

import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.ModuleBase;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class Module implements ModuleBase {

    private Logger logger;

    @Override
    public void onEnable() {

    }

    @Override
    public void postInit() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean isActive() {
        return true;
    }

    public void registerEvents(Listener selectedListener) {
        FrameworkManager.registerEvents(this, selectedListener);
    }

    public void registerHudOverlay(HudOverlay hudOverlay) {
        FrameworkManager.registerHudOverlay(this, hudOverlay);
    }

    public void registerKeyBinding(String name, int key, String tab, boolean press, Runnable onPress) {
        FrameworkManager.registerKeyBinding(this, new KeyHolder(name, key, tab, press, onPress));
    }

    public Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}
