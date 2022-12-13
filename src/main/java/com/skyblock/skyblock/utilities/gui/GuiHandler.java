package com.skyblock.skyblock.utilities.gui;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GuiHandler {

    private final HashMap<String, String> guiCommands;
    private final HashMap<String, Gui> guis;

    public GuiHandler(Skyblock skyblock) {
        this.guis = new HashMap<>();
        this.guiCommands = new HashMap<>();
    }

    public void registerGui(String name, Gui gui) {
        this.guis.put(name, gui);
    }

    public Gui getGui(String name) {
        return this.guis.get(name);
    }

    public void show(String name, Player player) {
        Gui gui = this.guis.get(name);
        String command = this.guiCommands.get(name);

        if (gui == null && command == null) return;

        if (gui != null) {
            gui.show(player);
        } else {
            player.performCommand(command);
        }
    }

    public void hide(String name, Player player) {
        Gui gui = this.guis.get(name);

        if (gui == null) return;

        gui.hide(player);
    }

    public void registerGuiCommand(String name, String command) {
        this.guiCommands.put(name, command);
    }

}
