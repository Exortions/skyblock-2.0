package com.skyblock.skyblock.utilities.gui;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GuiHandler {

    private final HashMap<String, Gui> guis;
    private final Skyblock skyblock;

    public GuiHandler(Skyblock skyblock) {
        this.skyblock = skyblock;
        this.guis = new HashMap<>();
    }

    public void registerGui(Gui gui) {
        this.guis.put(gui.getName(), gui);
    }

    public Gui getGui(String name) {
        return this.guis.get(name);
    }

    public void show(String name, Player player) {
        this.guis.get(name).show(player);
    }

    public void hide(String name, Player player) {
        this.guis.get(name).hide(player);
    }

}
