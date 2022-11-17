package com.skyblock.skyblock.utilities.chat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ChatMessageBuilder {

    private final List<String> messages;

    public ChatMessageBuilder(List<String> messages) {
        this.messages = messages;
    }

    public ChatMessageBuilder add(ChatColor color, String message) {
        Arrays.stream(message.split("\n")).forEach(s -> this.messages.add(color + s));
        return this;
    }

    public ChatMessageBuilder add(String message) {
        this.messages.addAll(Arrays.asList(message.split("\n")));
        return this;
    }

    public ChatMessageBuilder add(String... messages) {
        this.messages.addAll(Arrays.asList(messages));
        return this;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public void build(Player player) {
        player.sendMessage(this.messages.toArray(new String[0]));
    }

}
