package com.skyblock.skyblock.features.pets.gui;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.pets.Pet;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.gui.Gui;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PetsGUI extends Gui {

    public PetsGUI(Player opener) {
        super("Pets", 54, new HashMap<String, Runnable>(){{
            SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(opener);
            for (ItemStack item : skyblockPlayer.getPets()) {
                put(item.getItemMeta().getDisplayName(), () -> {
                    ItemStack prev = (ItemStack) skyblockPlayer.getValue("pets.equip");
                    if (prev != null) {
                        if (prev.equals(item)) {
                            opener.sendMessage(ChatColor.GREEN + "You despawned your " + prev.getItemMeta().getDisplayName() + ChatColor.GREEN + "!");
                            opener.closeInventory();
                            return;
                        }

                        NBTItem nbtItem = new NBTItem(prev);
                        nbtItem.setBoolean("active", false);
                        skyblockPlayer.removePet(prev);
                        skyblockPlayer.addPet(nbtItem.getItem());
                        skyblockPlayer.setPet(null);
                    }

                    NBTItem nbtItem = new NBTItem(item);
                    nbtItem.setBoolean("active", true);

                    skyblockPlayer.removePet(item);
                    skyblockPlayer.addPet(nbtItem.getItem());

                    skyblockPlayer.setValue("pets.equip", nbtItem.getItem());
                    skyblockPlayer.setPet(Pet.getPet(nbtItem.getItem()));

                    opener.sendMessage(ChatColor.GREEN + "You summoned your " + item.getItemMeta().getDisplayName() + ChatColor.GREEN +"!");
                    opener.playSound(opener.getLocation(), Sound.CHICKEN_EGG_POP, 10, 1);
                    opener.closeInventory();
                });
            }
        }});

        SkyblockPlayer skyblockPlayer = SkyblockPlayer.getPlayer(opener);

        skyblockPlayer.setExtraData("pets.petToItem", false);

        Util.fillBorder(this);

        ItemStack equip = (ItemStack) skyblockPlayer.getValue("pets.equip");

        String selected = (skyblockPlayer.getPet() != null ? skyblockPlayer.getPet().getColoredName() : ChatColor.RED + "None");

        addItem(4, new ItemBuilder(ChatColor.GREEN + "Pets", Material.BONE).addLore("&7View and manage all of your", "&7Pets.", " ", "&7Level up your pets faster by", "&7gaining xp in their favorite", "&7skill!", " ", "&7Selected Pet: " + selected).toItemStack());

        addItem(48, Util.buildBackButton());
        addItem(49, Util.buildCloseButton());

        boolean petToItem = (boolean) skyblockPlayer.getExtraData("pets.petToItem");
        boolean hidePets = (boolean) skyblockPlayer.getValue("pets.hidePets");

        addItem(50, new ItemBuilder(ChatColor.GREEN + "Convert Pet to Item", Material.INK_SACK, 1, (short) 8).addLore("&7Enable this setting and click", "&7any pet to convert it to an", "&7item.", " ", (petToItem ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled")).toItemStack());
        addItem(51, new ItemBuilder((hidePets ? ChatColor.RED + "Hide Pets" : ChatColor.GREEN + "Hide Pets"), Material.STONE_BUTTON).addLore("&7Hide all pets which are little", "&7heads from being visible in the", "&7world.", " ", "&7Pet effects remain active.", " ", "&7Currently: " + (hidePets ? ChatColor.RED + "Pets hidden!" : ChatColor.GREEN + "Pets shown!"), " ", ChatColor.YELLOW + "Click to toggle!").toItemStack());

        int j = 0;
        for (int i = 0; i < 54; i++) {
            if (getItems().containsKey(i)) continue;

            if (j == skyblockPlayer.getPets().size()) break;

            ItemStack item = skyblockPlayer.getPets().get(j);
            Pet pet = Pet.getPet(item);
            pet.setInGui(true);
            pet.setActive(false);

            addItem(i, pet.toItemStack());
            j++;
        }
    }
}
