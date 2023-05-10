package com.skyblock.skyblock.features.npc;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerNPCClickEvent;
import com.skyblock.skyblock.utilities.Util;
import lombok.Data;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.function.Consumer;

@Data
public class NPC implements Listener {

    private final String name;

    private final boolean doesLookClose;
    private final boolean hasSkin;

    private final boolean villager;
    private final Villager.Profession profession;

    private final Location location;

    private final Consumer<Player> action;

    private final String skinValue;
    private final String skinSignature;

    private net.citizensnpcs.api.npc.NPC npc;
    private ArmorStand stand;
    private ArmorStand click;

    public void spawn() {
        this.location.getWorld().loadChunk(this.location.getChunk());

        List<Object> npcData = Util.spawnSkyblockNpc(this.location, this.name, this.skinValue, this.skinSignature, this.hasSkin, this.doesLookClose, this.villager, this.profession);

        this.npc = (net.citizensnpcs.api.npc.NPC) npcData.get(0);
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(this.npc)) return;

        Player player = event.getClicker();

        this.action.accept(player);

        Bukkit.getPluginManager().callEvent(new SkyblockPlayerNPCClickEvent(player, this));
    }

    public static void sendMessages(Player player, String npc, String... messages) {
        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        if (skyblockPlayer.isTalkingToNPC()) return;

        skyblockPlayer.setExtraData("isInteracting", true);

        int i = 0;
        for (String message : messages) {
            Util.delay(() -> sendMessage(player, npc, message), i * 20);
            i++;
        }

        Util.delay(() -> skyblockPlayer.setExtraData("isInteracting", false), messages.length * 20);
    }
    public static void sendMessage(Player player, String npc, String message) {
        sendMessage(player, npc, message, true);
    }
    public static void sendMessage(Player player, String npc, String message, boolean sound) {
        player.sendMessage(ChatColor.YELLOW + "[NPC] " + npc + ChatColor.WHITE + ": " + message);
        if (sound) player.playSound(player.getLocation(), Sound.VILLAGER_YES, 10, 1);
    }
}
