package com.skyblock.skyblock.features.items.misc;

import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.sound.SoundSequence;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MaddoxBatphone extends SkyblockItem {

    private static final List<String> SUCCESS = Arrays.asList("Hello?", "Someone answers!",
            "How does a lobster answer? Shello!",
            "Hey what do you need?",
            "You hear the line pick up...",
            "You again? What do you want this time?");

    public MaddoxBatphone() {
        super(plugin.getItemHandler().getItem("AATROX_BATPHONE.json"), "aatrox_batphone");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        Player player = event.getPlayer();

        player.sendMessage(ChatColor.YELLOW + "✆ Ringing...");
        Util.delay(() -> player.sendMessage(ChatColor.YELLOW + "✆ Ring... Ring..."), 18);
        Util.delay(() -> player.sendMessage(ChatColor.YELLOW + "✆ Ring... Ring... Ring..."), 35);

        SoundSequence.BATPHONE.play(player.getLocation());

        TextComponent message = new TextComponent("⓪ How does the lobster answer? Shello! ");
        message.setColor(net.md_5.bungee.api.ChatColor.GREEN);

        ComponentBuilder cb = new ComponentBuilder("Click!").color(net.md_5.bungee.api.ChatColor.YELLOW);

        TextComponent click = new TextComponent("[OPEN MENU]");
        click.setColor(net.md_5.bungee.api.ChatColor.DARK_GREEN);
        click.setBold(true);
        click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, cb.create()));
        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sb batphone"));

        message.addExtra(click);

        Util.delay(() -> player.spigot().sendMessage(message), 52);
    }
}
