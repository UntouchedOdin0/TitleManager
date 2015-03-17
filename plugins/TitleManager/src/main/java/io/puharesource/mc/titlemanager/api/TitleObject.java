package io.puharesource.mc.titlemanager.api;

import io.puharesource.mc.titlemanager.Config;
import io.puharesource.mc.titlemanager.ReflectionManager;
import io.puharesource.mc.titlemanager.api.events.TitleEvent;
import io.puharesource.mc.titlemanager.api.iface.ITitleObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * The title object is an object used whenever a hovering message is sent to the player, whether it's an animation or not.
 */
public class TitleObject implements ITitleObject {

    private String rawTitle;
    private String rawSubtitle;

    private Object title;
    private Object subtitle;

    private int fadeIn = -1;
    private int stay = -1;
    private int fadeOut = -1;

    /**
     * Creates a title object with only one type of title title/subtitle
     * @param title The text of the title/subtitle.
     * @param type The type of the title/subtitle.
     */
    public TitleObject(String title, TitleType type) {
        if (type == TitleType.TITLE)
            setTitle(title);
        else if (type == TitleType.SUBTITLE)
            setSubtitle(title);
        updateTimes();
    }

    /**
     * Creates a title object with both the title/subtitle present.
     * @param title The text of the title.
     * @param subtitle The text of the subtitle.
     */
    public TitleObject(String title, String subtitle) {
        setTitle(title);
        setSubtitle(subtitle);
        updateTimes();
    }
    
    private void updateTimes() {
        if (Config.isUsingConfig()) return;

        try {
            fadeIn = Config.getConfig().getInt("welcome_message.fadeIn");
            stay = Config.getConfig().getInt("welcome_message.stay");
            fadeOut = Config.getConfig().getInt("welcome_message.fadeOut");
        } catch (Exception ignored) {}
    }
    
    @Override
    public void broadcast() {
        for (Player player : Bukkit.getOnlinePlayers())
            send(player);
    }

    @Override
    public void send(Player player) {
        final TitleEvent event = new TitleEvent(player, this);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;
        
        ReflectionManager manager = ReflectionManager.getInstance();

        manager.sendPacket(manager.constructTitleTimingsPacket(fadeIn, stay, fadeOut), player);
        if (rawTitle != null && title != null)
            manager.sendPacket(manager.constructTitlePacket(false, TextConverter.containsVariable(rawTitle) ? manager.getIChatBaseComponent(TextConverter.setVariables(player, rawTitle)) : title), player);
        if (rawSubtitle != null && subtitle != null)
            manager.sendPacket(manager.constructTitlePacket(true, TextConverter.containsVariable(rawSubtitle) ? manager.getIChatBaseComponent(TextConverter.setVariables(player, rawSubtitle)) : subtitle), player);
    }

    /**
     * Gets the raw text of the title.
     * @return The title text.
     */
    public String getTitle() {
        return rawTitle;
    }

    /**
     * Sets the text of the title.
     * @param title The text shown as the title.
     * @return The root object.
     */
    public TitleObject setTitle(String title) {
        rawTitle = title;
        this.title = ReflectionManager.getInstance().getIChatBaseComponent(title);
        return this;
    }

    /**
     * Gets the raw text of the subtitle.
     * @return The subtitle text.
     */
    public String getSubtitle() {
        return rawSubtitle;
    }

    /**
     * Sets the text of the subtitle.
     * @param subtitle The text shown as the subtitle.
     * @return The root object.
     */
    public TitleObject setSubtitle(String subtitle) {
        rawSubtitle = subtitle;
        this.subtitle = ReflectionManager.getInstance().getIChatBaseComponent(subtitle);
        return this;
    }

    /**
     * Gets the amount of ticks it takes for the object to fade into view.
     * @return The amount of ticks.
     */
    public int getFadeIn() {
        return fadeIn;
    }

    /**
     * Sets the amount of ticks it takes for the object to fade into view.
     * @param ticks The amount of ticks.
     * @return The root object.
     */
    public TitleObject setFadeIn(int ticks) {
        fadeIn = ticks;
        return this;
    }

    /**
     * Gets the amount of ticks it takes for the object to stay on screen without fading in or out of view.
     * @return The amount of ticks.
     */
    public int getStay() {
        return stay;
    }

    /**
     * Sets the amount of ticks it takes for the object to stay on screen without fading in or out of view.
     * @param ticks The amount of ticks.
     * @return The root object.
     */
    public TitleObject setStay(int ticks) {
        stay = ticks;
        return this;
    }

    /**
     * Gets the amount of ticks it takes for the object to fade out of view.
     * @return The amount of ticks.
     */
    public int getFadeOut() {
        return fadeOut;
    }

    /**
     * Sets the amount of ticks it takes for the object to fade out of view.
     * @param ticks The amount of ticks.
     * @return The root object.
     */
    public TitleObject setFadeOut(int ticks) {
        fadeOut = ticks;
        return this;
    }

    public enum TitleType {TITLE, SUBTITLE}
}