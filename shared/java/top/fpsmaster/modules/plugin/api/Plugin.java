package top.fpsmaster.modules.plugin.api;

import top.fpsmaster.event.EventDispatcher;
import top.fpsmaster.modules.logger.Logger;

public class Plugin {
    public void init() {
        Logger.info("plugins", "Plugin initialized: " + this.getClass().getName());
        EventDispatcher.registerListener(this);
    }

    public boolean onJoin(String serverId) {
        return false;
    }
}
