package com.skyblock.skyblock.commands.misc;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.entities.dragon.DragonSequence;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSlime;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiresPlayer
@Usage(usage = "/sb test")
@Description(description = "Command for testing features")
public class TestCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
//        Escrow escrow = Skyblock.getPlugin().getBazaar().getEscrow();
//
//        EscrowTransaction transaction = escrow.createTransaction(player, player, 100, 300, escrow.getBazaar().getRawItems().get(0), Escrow.TransactionType.SELL, (trans) -> {
//            if (Bukkit.getPlayer(trans.getSeller().getUniqueId()) != null) {
//                Bukkit.getPlayer(trans.getSeller().getUniqueId()).sendMessage("order filled!");
//            }
//
//            if (Bukkit.getPlayer(trans.getBuyer().getUniqueId()) != null) {
//                Bukkit.getPlayer(trans.getBuyer().getUniqueId()).sendMessage("order filled!");
//            }
//        });
//
//        escrow.fillBuyOrder(transaction, 300);
//
//        player.sendMessage(escrow.getBuyOrders().toString());
//        player.sendMessage(escrow.getSellOrders().toString());
//
//        new CalendarEventsGUI(player).show(player);
//
//        player.teleport(new Location(Bukkit.createWorld(new WorldCreator(args[0])), 0, 100, 0));

//            MinionBase minion = new CobblestoneMinion();
//            minion.spawn(SkyblockPlayer.getPlayer(player), player.getLocation(), 6);

//        player.openInventory(new AnvilGUI());

//        DragonSequence.endingSequence();

//        ItemStack item = player.getItemInHand();
//
//        AuctionCategory category = AuctionCategory.valueOf(args[0]);
//        Rarity tier = Rarity.valueOf(args[1]);
//        String search = args[2];
//
//        if (!category.getCanPut().test(item)) Bukkit.broadcastMessage("Failed Category Test");
//        if (!Rarity.valueOf(ChatColor.stripColor(new NBTItem(item).getString("rarity")).split(" ")[0]).equals(tier)) Bukkit.broadcastMessage("Failed Rarity Test");
//        if (!ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase().contains(search.toLowerCase()) && !search.equals("")) Bukkit.broadcastMessage("Failed Search Test");

        long start = System.currentTimeMillis();

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);

        skyblockPlayer.getConfig().set(args[0], args[1]);

        player.sendMessage(ChatColor.AQUA + "[DEBUG] " + ChatColor.YELLOW + "Took " + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.YELLOW + " to set data: " + ChatColor.GREEN + args[0] + ChatColor.YELLOW + "!");

//        Entity entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.valueOf(args[0].toUpperCase()));
//
//        Slime slime = (Slime) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.SLIME);
////        slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, true));
//        slime.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 10, true, true));
//        slime.setSize(Integer.parseInt(args[1]));
//
//        ((CraftSlime) slime).getHandle().ai = false;
//
//        slime.setCustomName("name_slime_" + entity.getEntityId());
//        slime.setCustomNameVisible(false);
//
//        ArmorStand stand = (ArmorStand) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.ARMOR_STAND);
//        stand.setMarker(true);
//        stand.setCustomNameVisible(true);
//        stand.setGravity(false);
////        stand.setVisible(false);
//
//        slime.setPassenger(stand);
//        entity.setPassenger(slime);
//
//        player.sendMessage(String.format(ChatColor.AQUA + "[DEBUG] " + ChatColor.YELLOW + "Spawned a %s with size %s.", args[0], args[1]));
    }
}
