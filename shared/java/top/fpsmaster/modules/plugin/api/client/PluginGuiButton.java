package top.fpsmaster.modules.plugin.api.client;

public class PluginGuiButton {
    public int id;
    public String text;
    public int x,y;
    public int width,height;
    public Runnable runnable;

    public PluginGuiButton(int id, String text, int x, int y, int width, int height, Runnable runnable) {
        this.id = id;
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.runnable = runnable;
    }
}
