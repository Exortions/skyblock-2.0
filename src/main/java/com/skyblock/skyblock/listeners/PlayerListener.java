package com.skyblock.skyblock.listeners;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.features.enchantment.ItemEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.launchpads.LaunchPadHandler;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.bukkit.Material.*;

public class PlayerListener implements Listener {

    private final Skyblock plugin;
    public PlayerListener(Skyblock skyblock) {
        plugin = skyblock;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (!event.getFrom().getName().startsWith(IslandManager.ISLAND_PREFIX)) {
            SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

            if (player == null) return;

            this.plugin.getMinionHandler().reloadPlayer(player, false);

            return;
        }

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (player == null) return;

        player.getBukkitPlayer().getWorld().setGameRuleValue("randomTickSpeed", "0");

        this.plugin.getMinionHandler().deleteAll(player.getBukkitPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SkyblockPlayer.registerPlayer(player.getUniqueId(), e, (skyblockPlayer) -> {
            if (player.getItemInHand() != null) {
                skyblockPlayer.setHand(player.getItemInHand());
            }

            skyblockPlayer.tick();

            IChatBaseComponent header = new ChatComponentText(ChatColor.AQUA + "You are" + ChatColor.RED + " " + ChatColor.BOLD + "NOT" + ChatColor.RESET + " " +  ChatColor.AQUA + "playing on " + ChatColor.YELLOW + "" + ChatColor.BOLD + "MC.HYPIXEL.NET");
            IChatBaseComponent footer = new ChatComponentText(ChatColor.RED + "" + ChatColor.BOLD + "NO" + ChatColor.RESET + " " + ChatColor.GREEN + "Ranks, Boosters, & MORE!");

            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

            try {
                Field headerField = packet.getClass().getDeclaredField("a");
                Field footerField = packet.getClass().getDeclaredField("b");
                headerField.setAccessible(true);
                footerField.setAccessible(true);
                headerField.set(packet, header);
                footerField.set(packet, footer);
                headerField.setAccessible(!headerField.isAccessible());
                footerField.setAccessible(!footerField.isAccessible());
            } catch (Exception ex) {
                Skyblock.getPlugin().sendMessage("&cFailed to register tab list for &8" + player.getName() + "&c: &8" + ex.getMessage() + "&c!");
            }

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

            this.plugin.getMinionHandler().reloadPlayer(skyblockPlayer, false);

            for (ItemStack item : player.getInventory().getArmorContents()) {
                skyblockPlayer.updateStats(null, item);
            }

            Util.delay(() -> {
                if (!Skyblock.getPlugin().getFairySoulHandler().initialized) Skyblock.getPlugin().getFairySoulHandler().init();
            }, 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()){
                        skyblockPlayer.tick();
                    }else{
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 5L, 1);
        });
    }

    @EventHandler
    public void onArmorEquip(PlayerArmorEquipEvent e){
        SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());
        player.updateStats(e.getItemStack(), null);
    }

    @EventHandler
    public void onArmorUnEquip(PlayerArmorUnequipEvent e){
        if (!Arrays.asList(e.getPlayer().getInventory().getArmorContents()).contains(e.getItemStack())) {
            SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());
            player.updateStats(null, e.getItemStack());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity() == null) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getEntity());
        player.kill(event.getEntity().getLastDamageCause().getCause(), event.getEntity().getKiller());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if (e.getEntity().hasMetadata("merchant")) return;

        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) &&
            !e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            if (e.getEntity() instanceof Player){
                if (!e.isCancelled()) {
                    SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) e.getEntity());
                    double damage = e.getDamage();

                    e.setDamage(0);

                    player.damage(damage, e.getCause(), null);
                }
            } else {
                if (!e.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
                    if (e.getEntity().hasMetadata("skyblockEntityData")) {
                        SkyblockEntity sentity = plugin.getEntityHandler().getEntity(e.getEntity());
                        if (sentity == null) return;
                        int damage = (int) (e.getFinalDamage() / sentity.getEntityData().maximumHealth);

                        sentity.getEntityData().health = sentity.getEntityData().health - damage;
                    }

                    Util.setDamageIndicator(e.getEntity().getLocation(), ChatColor.GRAY + "" + Math.round(e.getFinalDamage()), true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ArmorStand || e.getEntity().hasMetadata("merchant")) return;

        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();

            SkyblockPlayer player = SkyblockPlayer.getPlayer(p);
            double damage = 5 + player.getStat(SkyblockStat.DAMAGE) + (player.getStat(SkyblockStat.STRENGTH) / 5F) * (1 + player.getStat(SkyblockStat.STRENGTH) / 100F);
            boolean crit = player.crit();

            for (BiFunction<SkyblockPlayer, Entity, Integer> func : player.getPredicateDamageModifiers()) {
                if (func == null) return;

                damage += (damage * func.apply(player, e.getEntity()) / 100);
            }

            double combat = 4 * Skill.getLevel((double) player.getValue("skill.combat.exp"));

            damage += damage * (combat / 100F);

            try {
                ItemBase base = new ItemBase(p.getItemInHand());

                for (ItemEnchantment ench : base.getEnchantments()) {
                    damage = ench.getBaseEnchantment().getModifiedDamage(player, e, damage);
                }
            } catch (Exception ignored) {}

            if (Util.notNull(p.getItemInHand()) && plugin.getSkyblockItemHandler().isRegistered(p.getItemInHand())) {
                damage = plugin.getSkyblockItemHandler().getRegistered(p.getItemInHand()).getModifiedDamage(player, e, damage);
            }

            if (player.getArmorSet() != null) {
                damage = player.getArmorSet().getModifiedDamage(player, e, damage);
            }

            double display = damage;

            if (e.getEntity().hasMetadata("skyblockEntityData")) {
                SkyblockEntity sentity = plugin.getEntityHandler().getEntity(e.getEntity());
                if (sentity == null) return;

                sentity.setLifeSpan(sentity.getLifeSpan() + 15 * 20);
                sentity.setLastDamager(player);

                if (crit) {
                    damage = (damage * (1 + player.getStat(SkyblockStat.CRIT_DAMAGE) / 100F)) / sentity.getEntityData().maximumHealth;
                } else {
                    damage = damage / sentity.getEntityData().maximumHealth;
                }

                display = damage * sentity.getEntityData().maximumHealth;

                sentity.onDamage(e, player, crit, display);

                sentity.getEntityData().health = (long) (sentity.getEntityData().health - display);
            } else {
                if (!e.getEntity().getType().equals(EntityType.ARMOR_STAND) && crit) {
                    damage = damage * (1 + player.getStat(SkyblockStat.CRIT_DAMAGE) / 100F);
                }
            }

            if (player.getExtraData("cleave_enchantment") != null) {
                float perc = (float) player.getExtraData("cleave_enchantment");
                damage = damage * perc;
                display = display * perc;
            }

            e.setDamage(damage);

            try {
                ItemBase base = new ItemBase(p.getItemInHand());

                for (ItemEnchantment ench : base.getEnchantments()) {
                    ench.getBaseEnchantment().onDamage(player, e, damage);
                }
            } catch (Exception ignored) {}

            if (crit) {
                Util.setDamageIndicator(e.getEntity().getLocation(), Util.addCritTexture((int) Math.round(display)), false);
            } else {
                Util.setDamageIndicator(e.getEntity().getLocation(), ChatColor.GRAY + "" + Math.round(display), true);
            }
        } else if (e.getDamager().hasMetadata("skyblockEntityData")) {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                SkyblockPlayer player = SkyblockPlayer.getPlayer(p);

                SkyblockEntity sentity = plugin.getEntityHandler().getEntity(e.getDamager());
                sentity.setLifeSpan(sentity.getLifeSpan() + 15 * 20);

                player.damage(sentity.getEntityData().damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, sentity.getVanilla());
            }
        } else if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            Entity entity = (Entity) arrow.getShooter();

            Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(entity, e.getEntity(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, e.getDamage()));
        }
    }

    private static final HashMap<String, String[]> discoveryMessages = new HashMap<String, String[]>() {{
        put("Auction House", new String[]{ "Auction off your special items", "Bid on other player's items" });
        put("Village", new String[]{ "Purchase items at the Market", "Visit the Auction House", "Manage your Coins in the Bank", "Enchant items at the Library" });
        put("Forest", new String[]{ "Visit the Lumberjack", "Chop down trees", "Travel to the Park" });
        put("Farm", new String[]{ "Visit the Farmer", "Gather wheat", "Travel to the barn" });
        put("Coal Mine", new String[]{ "Visit the Blacksmith", "Mine Coal", "Travel to the Gold Mine" });
        put("Bank", new String[]{ "Talk to the Banker", "Store your coins to keep them safe", "Earn interest on your coins" });
        put("Library", new String[]{ "Talk to the Librarian", "Enchant your items", "Buy items (Level 1 of Many Books)" });
        put("Flower House", new String[]{ "Talk to Marco", "Gather Rose Red Dye", "Get Spray Can" });
        put("Mountain", new String[]{ "Climb to the top!" });
        put("Graveyard", new String[]{ "Fight Zombies", "Travel to the Spiders Den", "Talk to Pat", "Investigate the Catacombs" });
        put("Ruins", new String[]{ "Explore the ancient ruins", "Watch out for the guard dogs!" });
        put("Wizard Tower", new String[]{ "Talk to the Wizard", "Use the Wizard Portal" });
        put("Blacksmith's House", new String[]{ "Upgrade equipment using reforges", "Combine items with the anvil" });
        put("Colosseum Arena", new String[]{ "Participate in special events" });
        put("Wilderness", new String[]{ "Fish", "Visit the Fisherman's Hut", "Visit the Fairy at the Fairy Pond", "Discover hidden secrets" });
        put("Mushroom Desert", new String[]{ "Harvest all the things" });
        put("The Barn", new String[]{ "Harvest wheat, carrots, potatoes, pumpkin, and melons", "Kill cows, chickens, and pigs", "Milk cows" });
        put("Spiders Den", new String[]{ "Talk to Haymitch", "Watch out for Spiders!", "Try to make it to the top of the hill" });
        put("The End", new String[]{ "Harvest wheat, carrots, potatoes, pumpkin, and melons", "Kill cows, chickens, and pigs", "Milk cows" });
        put("Blazing Fortress", new String[]{ "Battle against nether mobs", "Defeat the Magma Cube Boss", "Collect nether wart, blaze rods, and magma cream" });

    }};

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location to = e.getTo();
        Player player = e.getPlayer();
        Location bottom = new Location(to.getWorld(), to.getX(), to.getY() - 1, to.getZ());

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(player);
        SkyblockLocation location = skyblockPlayer.getCurrentLocation();

        if (location != null && skyblockPlayer.getExtraData("last_location") != location.getName() && !player.getWorld().getName().startsWith(IslandManager.ISLAND_PREFIX)) {
            ArrayList<String> found = (ArrayList<String>) skyblockPlayer.getValue("locations.found");

            if (!found.contains(location.getName())) {
                found.add(location.getName());
                skyblockPlayer.setValue("locations.found", found);
                skyblockPlayer.setExtraData("last_location", location.getName());

                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 0);

                IChatBaseComponent title = new ChatComponentText(location.getColor() + "" + ChatColor.BOLD + location.getName());
                IChatBaseComponent subtitle = new ChatComponentText(ChatColor.GREEN + "New Zone Discovered");
                PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, title);
                PacketPlayOutTitle packet2 = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitle);

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet2);

                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "  NEW ZONE " + ChatColor.RESET + "" + ChatColor.DARK_GRAY + "- " + location.getColor() + "" + ChatColor.BOLD + location.getName());
                player.sendMessage("  ");

                if (discoveryMessages.containsKey(location.getName())) {
                    for (String message : discoveryMessages.get(location.getName())) {
                        player.sendMessage(ChatColor.DARK_GRAY + "  > " + ChatColor.YELLOW + message);
                    }

                    player.sendMessage("   ");
                }

                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            }
        }

        if (bottom.getBlock().getType().equals(SLIME_BLOCK)) {
            LaunchPadHandler padHandler = plugin.getLaunchPadHandler();
            String pad = padHandler.closeTo(player);
            if (!pad.equals("NONE")) {
                padHandler.launch(player, pad);
            }
        } else if (bottom.getBlock().getType().equals(ENDER_PORTAL)) {
            player.performCommand("sb warp home");
        } else if (to.getBlock().getType().equals(PORTAL)) {
            player.performCommand("sb warp hub");
        }

        Skyblock.getPlugin().getFairySoulHandler().loadChunk(to.getChunk());
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent e) {
        if (e.getEntityType().equals(EntityType.ENDERMAN)) e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());

        player.onQuit();
    }

    @EventHandler
    public void onArmorStand(PlayerArmorStandManipulateEvent e) {
        if (e.getRightClicked().hasMetadata("isFairySoul")) {
            e.setCancelled(true);

            SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());
            List<Location> found = ((ArrayList<Location>) player.getValue("fairySouls.found")).stream().peek(location -> location.setWorld(Skyblock.getSkyblockWorld())).collect(Collectors.toList());

            if (!found.contains(e.getRightClicked().getLocation())) {
                e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "SOUL! " + ChatColor.RESET + "" + ChatColor.WHITE + "You found a " + ChatColor.LIGHT_PURPLE + "Fairy Soul" + ChatColor.WHITE + "!");
                found.add(e.getRightClicked().getLocation());
                player.setValue("fairySouls.found", found);
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.AMBIENCE_CAVE, 10, 2);
            } else {
                e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "You have already found that Fairy Soul!");
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.SILVERFISH_KILL, 10, 2);
            }
        }
    }

    @EventHandler
    public void onUpdate(BlockPhysicsEvent e){
        if (e.getChangedType().equals(AIR)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickup(PlayerPickupItemEvent e) {
        ItemStack item = e.getItem().getItemStack();

        if (item.getItemMeta().hasDisplayName()) {
            String name = item.getItemMeta().getDisplayName();
            if (name.startsWith("coins_")) {
                try {
                    SkyblockPlayer player = SkyblockPlayer.getPlayer(e.getPlayer());
                    int amount = Integer.parseInt(name.split("_")[1]);

                    player.addCoins(amount);
                    player.getBukkitPlayer().playSound(e.getItem().getLocation(), Sound.ORB_PICKUP, 10, 2);
                    player.setExtraData("lastpicked_coins", amount);

                    e.setCancelled(true);
                    e.getItem().remove();

                    Util.delay(() -> {
                        player.setExtraData("lastpicked_coins", null);
                    }, 80);

                    return;
                } catch (NumberFormatException ignored) { }
            }
        }
    }

    @EventHandler
    public void onCreative(InventoryCreativeEvent e) {
        ItemStack item = e.getCursor();

        if (!Util.notNull(item)) return;

        ItemStack clone = Util.toSkyblockItem(item).clone();
        clone.setAmount(item.getAmount());
        if (e.getCursor() != null) e.setCursor(clone);
    }

    @EventHandler
    public void onOpen(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(WORKBENCH)) {
                e.setCancelled(true);
                e.getPlayer().performCommand("sb craft");
            } else if (e.getClickedBlock().getType().equals(ENCHANTMENT_TABLE)) {
                e.setCancelled(true);
                e.getPlayer().performCommand("sb enchant");
            }
        }
    }

    @EventHandler
    public void onTrample(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.PHYSICAL)) return;
        if (!e.getClickedBlock().getType().equals(SOIL)) return;
        e.setCancelled(true);
    }
}
