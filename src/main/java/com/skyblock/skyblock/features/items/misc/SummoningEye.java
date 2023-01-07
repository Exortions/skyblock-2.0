package com.skyblock.skyblock.features.items.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.entities.dragon.Dragon;
import com.skyblock.skyblock.features.entities.dragon.DragonAltar;
import com.skyblock.skyblock.features.entities.dragon.DragonSequence;
import com.skyblock.skyblock.features.items.SkyblockItem;
import com.skyblock.skyblock.utilities.Util;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;

public class SummoningEye extends SkyblockItem {
    public SummoningEye() {
        super(plugin.getItemHandler().getItem("SUMMONING_EYE.json"), "summoning_eye");
    }

    @Override
    public void onRightClick(PlayerInteractEvent event, HashMap<String, Object> data) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getClickedBlock().getType().equals(Material.ENDER_PORTAL_FRAME)) return;

        Block block = event.getClickedBlock();
        DragonAltar altar = DragonAltar.getMainAltar();

        if (!altar.getFrames().contains(event.getClickedBlock().getLocation().clone().add(0.5, 0, 0.5))) return;
        if (altar.isFilled(block)) return;

        BlockState state = block.getState();
        state.setRawData((byte) 4);
        state.update();

        Player player = event.getPlayer();

        int quantity = altar.getFilled();

        player.setItemInHand(plugin.getItemHandler().getItem("SLEEPING_EYE.json"));

        event.getClickedBlock().setMetadata("placer", new FixedMetadataValue(Skyblock.getPlugin(), player.getName()));

        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "☬ " + ChatColor.GREEN + player.getName() + ChatColor.LIGHT_PURPLE +
                " placed a Summoning Eye! " + (quantity == 8 ? "Brace yourselves! " : "") +
                ChatColor.GRAY + "(" +
                (quantity == 8 ? ChatColor.GREEN : ChatColor.YELLOW) + quantity +
                ChatColor.GRAY + "/" + ChatColor.GREEN + "8" + ChatColor.GRAY + ")");

        Location loc = block.getLocation();
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.PORTAL, true, (float) loc.getX() + 0.5f, (float) loc.getY() + 1.5f, (float) loc.getZ() + 0.5f, 0.1f, 0.2f, 0.1f, 0.01f, 100);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

        if (quantity >= 8) {
            DragonSequence.playSound(Sound.ENDERMAN_STARE, 1);
            Util.delay(DragonSequence::startSequence, 80);
        }
    }
}
