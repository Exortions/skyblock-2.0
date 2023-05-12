package com.skyblock.skyblock.features.objectives.mines;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.events.SkyblockPlayerCollectItemEvent;
import com.skyblock.skyblock.features.npc.NPC;
import com.skyblock.skyblock.features.npc.NPCHandler;
import com.skyblock.skyblock.features.objectives.Objective;
import com.skyblock.skyblock.features.objectives.QuestLine;
import com.skyblock.skyblock.features.skills.Mining;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LostAndFoundQuest extends QuestLine {

    public LostAndFoundQuest() {
        super("lost_and_found", "Lost and Found", new TalkToLazyMinerObjective(), new FindPickaxeObjective(), new CollectIronAndGoldIngotsObjective());

        net.citizensnpcs.api.npc.NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARMOR_STAND, "", new Location(Skyblock.getSkyblockWorld(), -19.0, 24, -304.55, 90, -80));
        npc.data().set(net.citizensnpcs.api.npc.NPC.Metadata.NAMEPLATE_VISIBLE, false);

        ArmorStandTrait pickaxe = npc.getOrAddTrait(ArmorStandTrait.class);
        Equipment equipment = npc.getOrAddTrait(Equipment.class);

        pickaxe.setGravity(false);
        pickaxe.setVisible(false);

        equipment.set(Equipment.EquipmentSlot.HAND, new ItemBuilder("7f22f697-06a6-4a86-aa25-e86f82bf4219", Material.IRON_PICKAXE).addEnchantmentGlint().toItemStack());

        NPCHandler npcHandler = Skyblock.getPlugin().getNpcHandler();

        npcHandler.registerNPC(
                "lazy_miner",
                new NPC("Lazy Miner", true, true, false, null, new Location(Skyblock.getSkyblockWorld(), -12.5, 78, -338.5),
                        (p) -> {
                            SkyblockPlayer player = SkyblockPlayer.getPlayer(p);
                            boolean talkedAlready = (boolean) player.getValue("quests.lost_and_found.talkedToLazyMiner");

                            if (!talkedAlready) {
                                Util.sendDelayedMessages(player.getBukkitPlayer(), "Lazy Miner", (pl) -> {
                                    getObjective(player).complete(pl);
                                    player.setValue("quests.lost_and_found.talkedToLazyMiner", true);
                                }, "Whoops! I lost my good pickaxe again in the mines. That's why I always come prepared with a backup!",
                                        "It's probably down in the mine somewhere. Can you go find it?");

                                return;
                            }

                            Objective objective = getObjective(player);

                            if (objective == null) {
                                if (Skill.getLevel(Skill.getXP(new Mining(), player)) < 5) {
                                    Util.sendDelayedMessages(player.getBukkitPlayer(), "Lazy Miner", "The Deep Caverns are full of strange creatures and expensive treasures.", "Reach Mining Level V to gain access!");
                                }

                                if (!((List<String>) player.getValue("locations.found")).contains("Gunpowder Mines")) {
                                    Util.sendDelayedMessages(player.getBukkitPlayer(), "Lazy Miner",
                                            "Have you spelunked the Deep Caverns?", "They are full of bountiful treasures but watch out for those Lapis Zombies!");
                                }

                                return;
                            }

                            if (objective.getId().equals("find_pickaxe")) Util.sendDelayedMessage(player.getBukkitPlayer(), "Lazy Miner", "Find my pickaxe in the Gold Mine! I'm not going back down there...", 0);
                            if (objective.getId().equals("collect_iron_and_gold_ingots")) {
                                List<List<String>> messages = new ArrayList<List<String>>() {{
                                    add(Collections.singletonList("Smelting Touch is a really useful enchantment. It automatically smelts ores into ingots!"));

                                    add(Arrays.asList("Collect iron and gold ingots with that pickaxe you found!", "...Or do it the long way and smelt the ore yourself."));
                                }};

                                Random random = Skyblock.getPlugin().getRandom();
                                List<String> message = messages.get(random.nextInt(messages.size()));

                                Util.sendDelayedMessages(player.getBukkitPlayer(), "Lazy Miner", message.toArray(new String[0]));
                            }

                            },
                        "ewogICJ0aW1lc3RhbXAiIDogMTU5ODkwNDMxNDQxNywKICAicHJvZmlsZUlkIiA6ICJmYjNmNzkwMTVjZjU0NGFkODIyZDk2YmUwZTlkNzg0NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYWNpdXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDkzZDk2MzZhYTk1NTZhY2Y0YmUyY2M1OGQ5M2ZmMGI1MzJlNjgwOThmODBhN2RjMTUxYTBmMWQxNmI4ZTVmOCIKICAgIH0KICB9Cn0=",
                        "QP74duX5R3Owm4nqyDawIaIOq/j61geMjmu+0wHm2Gk7Pu8MNz2GciqeoPQIJrLOrmscju9ME7OAhgvV2NMTi9eqTceSMJNOBTBDqr/CYpA+qHAnl436eTo389+1Re3fKSumYukXIV176holRPLZDclssnLqqEpB4c1Sbwq0k0mlO1qcQcEht00vuRU5vsJAE5/018t1E8G9cBDDbgSSeOLh3g8Ad+ESY1ilVeuixlCkWTguNE/j8kp6PcQPLYFD6LzyJKoE6fhTjRhfA6BLIWZTPBfRiwoM1DyH+Rl48GCkfbgThjQVeVKO8F/54l21UARbNKR8XoDKdbXM+9ePt+U6o5VsiB2ezhDGcCdebjnhhU6P9jCqy8O34WGr8evjFti+YnZiWU1br8fwKWv8SEO1Ymz7dhQK7PY7WSpLGNv7NpIyjLCmr5572tzddTvcOQGp13P9hVs2/UvLtRN6GVHhgcg4OFah1zzSKiUoRhO5cv9OaXD3MKintyfqFvrzyCZMj3XlqLce429rcVLs1Yv5WQlhTB9l4B5BFfUxribjX4sOHimzrGkk9O/75L/GjxQdmlMBR3x5cU8Kaz5iGaMtERYO1L+FvaWa+bRU8zdAQUjZPq9go1EzpDK0om8c/4olRbB4Un9FQb4wwE/4Dxs7r6sTDXanRM7WJChn0mQ="
                )
        );

        npcHandler.registerNPC(
                "rusty",
                new NPC(
                        "Rusty",
                        true,
                        false,
                        true,
                        Villager.Profession.LIBRARIAN,
                        new Location(Skyblock.getSkyblockWorld(), -19.0, 78.0, -325.5),
                        (p) -> {
                            SkyblockPlayer player = SkyblockPlayer.getPlayer(p);

                            boolean alreadyTalked = (boolean) player.getValue("quests.lost_and_found.talkedToRusty");
                            Objective obj = getObjective(player);

                            if (obj == null) return;

                            if (!alreadyTalked && obj.getId().equals("talk_to_rusty")) {
                                Util.sendDelayedMessages(player.getBukkitPlayer(), "Rusty", (pl) -> {
                                    getObjective(player).complete(pl);
                                    player.setValue("quests.lost_and_found.talkedToRusty", true);
                                },
                                        "Hi, I'm the janitor of this mine.",
                                        "You would not believe how many people leave ingots and stones behind them!",
                                        "It drives me insane, everyone should be using " + ChatColor.DARK_PURPLE + "Telekinesis " + ChatColor.WHITE + "on their tools!",
                                        "If you want I can apply Telekinesis on any of your items for " + ChatColor.GOLD + "100 coins" + ChatColor.WHITE + ".");

                                return;
                            }

                            new RustyGui(player.getBukkitPlayer()).show(player.getBukkitPlayer());
                        },
                        null,
                        null
                )
        );
    }

    @Override
    protected boolean hasCompletionMessage() {
        return true;
    }

    private static final class TalkToLazyMinerObjective extends Objective {
        public TalkToLazyMinerObjective() {
            super("talk_to_lazy_miner", "Talk to the Lazy Miner");
        }
    }

    private static final class FindPickaxeObjective extends Objective {
        public FindPickaxeObjective() {
            super("find_pickaxe", "Find the pickaxe");
        }

        @EventHandler
        public void onPlayerClickArmorStand(PlayerInteractAtEntityEvent event) {
            if (event.getRightClicked() == null) return;

            if (event.getRightClicked().getType() != EntityType.ARMOR_STAND) return;

            ItemStack item = ((ArmorStand) event.getRightClicked()).getItemInHand();

            if (!item.hasItemMeta()) return;

            if (item.getItemMeta().getDisplayName().equals("7f22f697-06a6-4a86-aa25-e86f82bf4219")) {
                event.setCancelled(true);

                if ((boolean) SkyblockPlayer.getPlayer(event.getPlayer()).getValue("quests.lost_and_found.foundPickaxe")) return;

                event.getPlayer().performCommand("sb item IRON_PICKAXE.json");
                complete(event.getPlayer());

                SkyblockPlayer.getPlayer(event.getPlayer()).setValue("quests.lost_and_found.foundPickaxe", true);
            }
        }
    }

    private static final class CollectIronAndGoldIngotsObjective extends Objective {
        public CollectIronAndGoldIngotsObjective() {
            super("collect_iron_and_gold_ingots", "Collect iron and gold ingots");
        }

        @EventHandler
        public void onPickupItem(PlayerPickupItemEvent event) {
            if (event.getItem() == null || event.getItem().getItemStack().getType().equals(Material.AIR)) return;

            SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

            if (!isThisObjective(player.getBukkitPlayer())) return;

            player.setValue("quests.lost_and_found.mined", (int) player.getValue("quests.lost_and_found.mined") + 1);

            if ((int) player.getValue("quests.lost_and_found.mined") == 2) complete(player.getBukkitPlayer());
        }
    }

//    private static final class TalkToRustyObjective extends Objective {
//        public TalkToRustyObjective() {
//            super("talk_to_rusty", "Talk to Rusty");
//        }
//    }
//

    private static final class RustyGui extends Gui {
        public RustyGui(Player opener) {
            super("Rusty the Janitor", 45, new HashMap<String, Runnable>() {{
                put(ChatColor.RED + "Close", opener::closeInventory);
            }});

            Util.fillEmpty(this);
        }

    }

}
