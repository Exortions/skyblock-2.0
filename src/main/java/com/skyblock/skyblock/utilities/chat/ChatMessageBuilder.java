package com.skyblock.skyblock.utilities.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatMessageBuilder {

    private final List<String> messages;

    public ChatMessageBuilder() {
        this.messages = new ArrayList<>();
    }

    public ChatMessageBuilder add(ChatColor color, String message) {
        Arrays.stream(ChatColor.translateAlternateColorCodes('&', message).split("\n")).forEach(s -> this.messages.add(color + s));
        return this;
    }

    public ChatMessageBuilder add(String message) {
        this.messages.addAll(Arrays.asList(ChatColor.translateAlternateColorCodes('&', message).split("\n")));
        return this;
    }

    public ChatMessageBuilder add(String... messages) {
        for (String message : messages) {
            this.messages.addAll(Arrays.asList(ChatColor.translateAlternateColorCodes('&', message).split("\n")));
        }
        return this;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public void build(Player player) {
        player.sendMessage(this.messages.toArray(new String[0]));
    }

}
