package com.skyblock.skyblock.listeners;

import com.inkzzz.spigot.armorevent.PlayerArmorEquipEvent;
import com.inkzzz.spigot.armorevent.PlayerArmorUnequipEvent;
import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockPlayerBowShootEvent;
import com.skyblock.skyblock.events.SkyblockPlayerDamageByEntityEvent;
import com.skyblock.skyblock.events.SkyblockPlayerDamageEntityEvent;
import com.skyblock.skyblock.events.SkyblockPlayerItemHeldChangeEvent;
import com.skyblock.skyblock.features.enchantment.AnvilGUI;
import com.skyblock.skyblock.features.enchantment.EnchantingTableGUI;
import com.skyblock.skyblock.features.enchantment.ItemEnchantment;
import com.skyblock.skyblock.features.entities.SkyblockEntity;
import com.skyblock.skyblock.features.guis.ProfileGui;
import com.skyblock.skyblock.features.island.IslandManager;
import com.skyblock.skyblock.features.launchpads.LaunchPadHandler;
import com.skyblock.skyblock.features.location.SkyblockLocation;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.bukkit.Material.*;

public class PlayerListener implements Listener {

    private final Skyblock plugin;

    public PlayerListener(Skyblock skyblock) {
        this.plugin = skyblock;
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
    public void onPlayerClickPlayer(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (!(event.getRightClicked() instanceof Player)) return;
        if (event.getRightClicked().hasMetadata("NPC") || !player.getItemInHand().getType().equals(AIR) || player.isSneaking())
            return;

        new ProfileGui(player, (Player) event.getRightClicked()).show(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        long start = System.currentTimeMillis();

        SkyblockPlayer.registerPlayer(player.getUniqueId(), event, (skyblockPlayer) -> {
            if (player.getItemInHand() != null) {
                skyblockPlayer.setHand(player.getItemInHand());
            }

//            new BukkitRunnable() {
//                @Override
//                public void run() {
                    skyblockPlayer.tick();
//                }
//            }.runTaskLater(plugin, 20L);

            this.plugin.getMinionHandler().reloadPlayer(skyblockPlayer, false);

            for (ItemStack item : player.getInventory().getArmorContents()) {
                skyblockPlayer.updateStats(null, item);
            }

            Util.delay(() -> {
                if (!Skyblock.getPlugin().getFairySoulHandler().initialized) Skyblock.getPlugin().getFairySoulHandler().init();

                if (skyblockPlayer.isOnIsland()) player.performCommand("is");

                player.teleport(Util.getSpawnLocation(skyblockPlayer.getCurrentLocationName()));
            }, 1);

            skyblockPlayer.loadCache();

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        skyblockPlayer.tick();
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 5L, 1);
        });

        /* 0ms */ player.sendMessage(ChatColor.AQUA + "[DEBUG] " + ChatColor.YELLOW + "Took " + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + ChatColor.YELLOW + " to retrieve your Skyblock Data!");
    }

    @EventHandler
    public void onArmorEquip(PlayerArmorEquipEvent event) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());
        player.updateStats(event.getItemStack(), null);
    }

    @EventHandler
    public void onArmorUnEquip(PlayerArmorUnequipEvent event) {
        if (!Arrays.asList(event.getPlayer().getInventory().getArmorContents()).contains(event.getItemStack())) {
            SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());
            player.updateStats(null, event.getItemStack());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity() == null) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getEntity());
        player.kill(event.getEntity().getLastDamageCause().getCause(), event.getEntity().getKiller());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().hasMetadata("merchant")) return;

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) &&
                !event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            if (event.getEntity() instanceof Player) {
                if (!event.isCancelled()) {
                    SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getEntity());
                    double damage = event.getDamage();

                    event.setDamage(0);

                    if (Util.notNull(player.getBukkitPlayer().getItemInHand()) && plugin.getSkyblockItemHandler().isRegistered(player.getBukkitPlayer().getItemInHand()))
                        damage = plugin.getSkyblockItemHandler().getRegistered(player.getBukkitPlayer().getItemInHand()).getModifiedIncomingDamage(player, event, damage);

                    player.damage(damage, event.getCause(), null);
                }
            } else {
                if (!event.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
                    if (event.getEntity().hasMetadata("skyblockEntityData")) {
                        SkyblockEntity sentity = plugin.getEntityHandler().getEntity(event.getEntity());
                        if (sentity == null) return;
                        int damage = (int) (event.getFinalDamage() / sentity.getEntityData().maximumHealth);

                        sentity.getEntityData().health = sentity.getEntityData().health - damage;
                    }

                    Util.setDamageIndicator(event.getEntity().getLocation(), ChatColor.GRAY + "" + Math.round(event.getFinalDamage()), true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ArmorStand || entity.hasMetadata("merchant")) return;
        if (entity instanceof Player && event.getDamager() instanceof Player) {
            event.setCancelled(true);
            return;
        }

        // Nametag stuff
        if (entity.getCustomName() != null && entity.getType().equals(EntityType.SLIME) && entity.getCustomName().startsWith("name_slime_")) {
            int id = Integer.parseInt(entity.getCustomName().split("_")[2]);
            SkyblockEntity sb = Skyblock.getPlugin().getEntityHandler().getEntity(id);

            if (sb == null) return;

            event.setCancelled(true);

            ((LivingEntity) sb.getVanilla()).damage(0, event.getDamager());

            return;
        }

        if (event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();

            SkyblockPlayer player = SkyblockPlayer.getPlayer(p);
            double damage = 5 + player.getStat(SkyblockStat.DAMAGE) + (player.getStat(SkyblockStat.STRENGTH) / 5F) * (1 + player.getStat(SkyblockStat.STRENGTH) / 100F);
            boolean crit = player.crit();

            for (BiFunction<SkyblockPlayer, Entity, Integer> func : player.getPredicateDamageModifiers()) {
                if (func == null) return;

                damage += (damage * func.apply(player, event.getEntity()) / 100);
            }

            double combat = 4 * Skill.getLevel((double) player.getValue("skill.combat.exp"));

            damage += damage * (combat / 100F);

            try {
                ItemBase base = new ItemBase(p.getItemInHand());

                for (ItemEnchantment ench : base.getEnchantments())
                    damage = ench.getBaseEnchantment().getModifiedDamage(player, event, damage);
            } catch (Exception ignored) {
            }

            if (Util.notNull(p.getItemInHand()) && plugin.getSkyblockItemHandler().isRegistered(p.getItemInHand()))
                damage = plugin.getSkyblockItemHandler().getRegistered(p.getItemInHand()).getModifiedDamage(player, event, damage);

            if (player.getArmorSet() != null) damage = player.getArmorSet().getModifiedDamage(player, event, damage);

            double display = damage;

            if (event.getEntity().hasMetadata("skyblockEntityData")) {
                SkyblockEntity sentity = plugin.getEntityHandler().getEntity(event.getEntity());
                if (sentity == null) return;

                sentity.setLifeSpan(sentity.getLifeSpan() + 15 * 20);
                sentity.setLastDamager(player);

                if (crit)
                    damage = (damage * (1 + player.getStat(SkyblockStat.CRIT_DAMAGE) / 100F)) / sentity.getEntityData().maximumHealth;
                else damage = damage / sentity.getEntityData().maximumHealth;

                display = damage * sentity.getEntityData().maximumHealth;

                SkyblockPlayerDamageEntityEvent e = new SkyblockPlayerDamageEntityEvent(player, sentity, display, event);
                Bukkit.getPluginManager().callEvent(e);

                if (player.getPet() != null) player.getPet().onDamage(e);

                display = e.getDamage();
                damage = e.getDamage() / sentity.getEntityData().maximumHealth;

                sentity.onDamage(event, player, crit, display);

                sentity.getEntityData().health = (long) (sentity.getEntityData().health - display);
            } else {
                if (!event.getEntity().getType().equals(EntityType.ARMOR_STAND) && crit)
                    damage = damage * (1 + player.getStat(SkyblockStat.CRIT_DAMAGE) / 100F);
            }

            event.setDamage(damage);

            try {
                ItemBase base = new ItemBase(p.getItemInHand());

                for (ItemEnchantment ench : base.getEnchantments()) {
                    ench.getBaseEnchantment().onDamage(player, event, damage);
                }
            } catch (Exception ignored) {
            }

            if (event.getEntity() == null) return;
            if (event.getEntity() instanceof Player) return;
            if (event.getEntity().hasMetadata("NPC")) return;

            if (crit) {
                Util.setDamageIndicator(event.getEntity().getLocation(), Util.addCritTexture((int) Math.round(display)), false);
            } else {
                Util.setDamageIndicator(event.getEntity().getLocation(), ChatColor.GRAY + "" + Math.round(display), true);
            }
        } else if (event.getDamager().hasMetadata("skyblockEntityData")) {
            if (event.getEntity() instanceof Player && !event.getEntity().hasMetadata("NPC")) {
                Player p = (Player) event.getEntity();
                SkyblockPlayer player = SkyblockPlayer.getPlayer(p);

                SkyblockEntity sentity = plugin.getEntityHandler().getEntity(event.getDamager());
                sentity.setLifeSpan(sentity.getLifeSpan() + 15 * 20);

                SkyblockPlayerDamageByEntityEvent e = new SkyblockPlayerDamageByEntityEvent(player, sentity, false, sentity.getEntityData().damage);
                Bukkit.getPluginManager().callEvent(e);

                double damage = e.getDamage();

                player.damage(damage, EntityDamageEvent.DamageCause.ENTITY_ATTACK, sentity.getVanilla(), e.isTrueDamage());
            }
        } else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            Entity shooter = (Entity) arrow.getShooter();

            Bukkit.getPluginManager().callEvent(new EntityDamageByEntityEvent(shooter, event.getEntity(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, event.getDamage()));
        }
    }

    private static final HashMap<String, String[]> discoveryMessages = new HashMap<String, String[]>() {{
        put("Auction House", new String[]{"Auction off your special items", "Bid on other player's items"});
        put("Village", new String[]{"Purchase items at the Market", "Visit the Auction House", "Manage your Coins in the Bank", "Enchant items at the Library"});
        put("Forest", new String[]{"Visit the Lumberjack", "Chop down trees", "Travel to the Park"});
        put("Farm", new String[]{"Visit the Farmer", "Gather wheat", "Travel to the barn"});
        put("Coal Mine", new String[]{"Visit the Blacksmith", "Mine Coal", "Travel to the Gold Mine"});
        put("Bank", new String[]{"Talk to the Banker", "Store your coins to keep them safe", "Earn interest on your coins"});
        put("Library", new String[]{"Talk to the Librarian", "Enchant your items", "Buy items (Level 1 of Many Books)"});
        put("Flower House", new String[]{"Talk to Marco", "Gather Rose Red Dye", "Get Spray Can"});
        put("Mountain", new String[]{"Climb to the top!"});
        put("Graveyard", new String[]{"Fight Zombies", "Travel to the Spiders Den", "Talk to Pat", "Investigate the Catacombs"});
        put("Ruins", new String[]{"Explore the ancient ruins", "Watch out for the guard dogs!"});
        put("Wizard Tower", new String[]{"Talk to the Wizard", "Use the Wizard Portal"});
        put("Blacksmith's House", new String[]{"Upgrade equipment using reforges", "Combine items with the anvil"});
        put("Colosseum Arena", new String[]{"Participate in special events"});
        put("Wilderness", new String[]{"Fish", "Visit the Fisherman's Hut", "Visit the Fairy at the Fairy Pond", "Discover hidden secrets"});
        put("Mushroom Desert", new String[]{"Harvest all the things"});
        put("The Barn", new String[]{"Harvest wheat, carrots, potatoes, pumpkin, and melons", "Kill cows, chickens, and pigs", "Milk cows"});
        put("Spiders Den", new String[]{"Talk to Haymitch", "Watch out for Spiders!", "Try to make it to the top of the hill"});
        put("The End", new String[]{"Fight Enderman and Endermites", "Mine End Stone and Obsidian", "Travel to the Dragon's Nest!"});
        put("Blazing Fortress", new String[]{"Battle against nether mobs", "Defeat the Magma Cube Boss", "Collect nether wart, blaze rods, and magma cream"});
    }};

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Player player = event.getPlayer();
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
        killRemovables(to.getChunk());
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent event) {
        if (event.getEntityType().equals(EntityType.ENDERMAN)) event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        player.onQuit();
    }

    @EventHandler
    public void onArmorStand(NPCRightClickEvent event) {
        if (event.getNPC().getEntity().hasMetadata("isFairySoul")) {
            event.setCancelled(true);

            SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getClicker());
            List<Location> found = ((ArrayList<Location>) player.getValue("fairySouls.found")).stream().peek(location -> location.setWorld(Skyblock.getSkyblockWorld())).collect(Collectors.toList());

            if (!found.contains(event.getNPC().getEntity().getLocation())) {
                event.getClicker().sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "SOUL! " + ChatColor.RESET + "" + ChatColor.WHITE + "You found a " + ChatColor.LIGHT_PURPLE + "Fairy Soul" + ChatColor.WHITE + "!");
                found.add(event.getNPC().getEntity().getLocation());
                player.setValue("fairySouls.found", found);
                event.getClicker().playSound(event.getClicker().getLocation(), Sound.AMBIENCE_CAVE, 10, 2);
            } else {
                event.getClicker().sendMessage(ChatColor.LIGHT_PURPLE + "You have already found that Fairy Soul!");
                event.getClicker().playSound(event.getClicker().getLocation(), Sound.SILVERFISH_KILL, 10, 2);
            }
        }
    }

    @EventHandler
    public void onUpdate(BlockPhysicsEvent event) {
        if (event.getChangedType().equals(AIR)) event.setCancelled(true);
    }

    @EventHandler
    public void onCombust(EntityCombustEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();

        if (item.getItemMeta().hasDisplayName()) {
            String name = item.getItemMeta().getDisplayName();
            if (name.startsWith("coins_")) {
                try {
                    SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());
                    int amount = Integer.parseInt(name.split("_")[1]);

                    player.addCoins(amount);
                    player.getBukkitPlayer().playSound(event.getItem().getLocation(), Sound.ORB_PICKUP, 10, 2);
                    player.setExtraData("lastpicked_coins", amount);

                    event.setCancelled(true);
                    event.getItem().remove();

                    Util.delay(() -> player.setExtraData("lastpicked_coins", null), 80);
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    @EventHandler
    public void onCreative(InventoryCreativeEvent event) {
        ItemStack item = event.getCursor();

        if (!Util.notNull(item)) return;

        ItemStack clone = Util.toSkyblockItem(item).clone();
        clone.setAmount(item.getAmount());
        if (event.getCursor() != null) event.setCursor(clone);
    }

    @EventHandler
    public void onOpen(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().getType().equals(WORKBENCH)) {
                e.setCancelled(true);
                e.getPlayer().performCommand("sb craft");
            } else if (e.getClickedBlock().getType().equals(ENCHANTMENT_TABLE)) {
                e.setCancelled(true);
                e.getPlayer().openInventory(new EnchantingTableGUI(e.getPlayer(), e.getClickedBlock().getLocation()));
            } else if (e.getClickedBlock().getType().equals(ANVIL)) {
                e.setCancelled(true);
                e.getPlayer().openInventory(new AnvilGUI());
            }
        }
    }

    @EventHandler
    public void onTrample(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) return;
        if (!event.getClickedBlock().getType().equals(SOIL)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();

        if (!Util.notNull(item)) return;

        if (item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Skyblock Menu") ||
            item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Quiver Arrow")) {
            ((Player) e.getWhoClicked()).performCommand("sb gui skyblock_menu");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        ItemStack stack = item.getItemStack();

        if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains("Skyblock Menu") ||
            stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains("Quiver Arrow")) {
            event.setCancelled(true);
        }

        if (Skyblock.getPlugin().getSkyblockItemHandler().isRegistered(item.getItemStack()) && (Boolean) player.getValue("settings.doubleTapDrop")) {
            if (!player.hasExtraData("lastDropAttempt")) {
                Map<String, Long> data = new HashMap<>();
                data.put("time", 0L);
                data.put("slot", 10L);
                player.setExtraData("lastDropAttempt", data);
            }
            HashMap<String, Long> data = (HashMap<String, Long>) player.getExtraData("lastDropAttempt");
            if (data.get("time") > System.currentTimeMillis() - 500 && data.get("slot").equals((long) event.getPlayer().getInventory().getHeldItemSlot()))
                return;
            event.getPlayer().sendMessage(ChatColor.RED + "You must double tap the drop button to drop this item!\nYou can disable this item in the "
                    + ChatColor.WHITE + "Settings"
                    + ChatColor.RED + " in your "
                    + ChatColor.GREEN + "Skyblock Menu"
                    + ChatColor.RED + "!");
            data = new HashMap<>();
            data.put("time", System.currentTimeMillis());
            data.put("slot", (long) event.getPlayer().getInventory().getHeldItemSlot());
            player.setExtraData("lastDropAttempt", data);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getEntity());

        Material arrow = ARROW;

        ItemStack next = player.getNextQuiverItem();

        if (next != null) {
            arrow = next.getType();
        }

        SkyblockPlayerBowShootEvent e = new SkyblockPlayerBowShootEvent(player, arrow, event.getForce());

        Bukkit.getPluginManager().callEvent(e);
    }

    @EventHandler
    public void onSwitchItem(SkyblockPlayerItemHeldChangeEvent e) {
        SkyblockPlayer player = e.getPlayer();

        if (e.getNewItem().getType().equals(BOW) && player.getQuiverAmount() != 0) {
            ItemBuilder quiver = new ItemBuilder(ChatColor.DARK_GRAY + "Quiver Arrow", ARROW).addLore("&7This item is in your inventory", "&7because you are holding your bow", "&7currently. Switch your held item", "&7to see the item that was here", "&7before.");

            quiver.addEnchantmentGlint();
            quiver.setAmount(player.getQuiverAmount());

            player.getBukkitPlayer().getInventory().setItem(8, quiver.toItemStack());

            new BukkitRunnable() {
                @Override
                public void run() {
                    ItemStack arrow = player.getBukkitPlayer().getInventory().getItem(8);

                    if (!arrow.getType().equals(ARROW)) {
                        cancel();
                        return;
                    }

                    if (arrow.getAmount() != player.getQuiverAmount()) {
                        player.removeFromQuiver();

                        ItemBuilder quiver = new ItemBuilder(ChatColor.DARK_GRAY + "Quiver Arrow", ARROW).addLore("&7This item is in your inventory", "&7because you are holding your bow", "&7currently. Switch your held item", "&7to see the item that was here", "&7before.");

                        quiver.addEnchantmentGlint();
                        quiver.setAmount(player.getQuiverAmount());

                        player.getBukkitPlayer().getInventory().setItem(8, quiver.toItemStack());
                    }
                }
            }.runTaskTimer(Skyblock.getPlugin(), 1, 1);
        } else if (e.getOldItem().getType().equals(BOW)) {
            player.getBukkitPlayer().getInventory().setItem(8, Util.createSkyblockMenu());
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        killRemovables(event.getChunk());
    }

    @EventHandler
    public void onPortal(EntityCreatePortalEvent e) {
        e.setCancelled(true);
    }

    private void killRemovables(Chunk chunk) {
        Entity[] entities = chunk.getEntities();

        File cacheFile = new File(Skyblock.getPlugin().getDataFolder(), ".cache.yml");
        FileConfiguration cache = YamlConfiguration.loadConfiguration(cacheFile);

        List<String> removables = cache.getStringList("removeables");

        for (Entity en : entities) {
            if (!removables.contains(en.getUniqueId().toString())) continue;

            Bukkit.getConsoleSender().sendMessage("[DEBUG]: Successfully removed entity: " + en.getCustomName());
            removables.remove(en.getUniqueId().toString());
            en.remove();
        }

        cache.set("removeables", removables);

        try {
            cache.save(cacheFile);
        } catch (IOException ignored) {}
    }
}
