package com.skyblock.skyblock.features.enchantment.enchantments.sword;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.enchantment.ItemEnchantment;
import com.skyblock.skyblock.features.enchantment.types.SwordEnchantment;
import com.skyblock.skyblock.utilities.item.ItemBase;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.ToIntFunction;


public class EnderSlayerEnchantment extends SwordEnchantment {

    private final HashMap<SkyblockPlayer, Integer> predicateDamageModifiers;

    public EnderSlayerEnchantment() {
        super("ender_slayer", "Ender Slayer", (level) -> {
            String description = "Increases damage dealt to\nEnder Dragons and\nEnderman by {damage}%.";

            int damage;

            if (level <= 4) damage = level * 15;
            else if (level == 5 || level == 6) damage = 60 + (level == 5 ? 20 : 40);
            else damage =  130;

            return description.replace("{damage}", String.valueOf(damage));
        }, 7);

        this.predicateDamageModifiers = new HashMap<>();
    }

    @EventHandler
    public void onPlayerSwapMainItem(PlayerItemHeldEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (item == null || item.getType().equals(Material.AIR)) return;

        ItemBase base;
        try {
            base = new ItemBase(item);
        } catch (IllegalArgumentException ex) {
            return;
        }

        SkyblockPlayer player = SkyblockPlayer.getPlayer(event.getPlayer());

        if (!base.hasEnchantment(this) || this.predicateDamageModifiers.containsKey(player)) {
            if (this.predicateDamageModifiers.get(player) == null) return;

            int index = this.predicateDamageModifiers.get(player);

            player.removePredicateDamageModifier(index);

            return;
        }

        this.predicateDamageModifiers.put(player, player.getPredicateDamageModifiers().size() + 1);

        player.addPredicateDamageModifier((skyblockPlayer, entity) -> {
            ItemBase b;

            if (player.getBukkitPlayer().getItemInHand() == null || player.getBukkitPlayer().getItemInHand().getType().equals(Material.AIR)) return 0;

            try {
                b = new ItemBase(player.getBukkitPlayer().getItemInHand());
            } catch (IllegalArgumentException ex) {
                return 0;
            }

            if (!entity.getType().equals(EntityType.ENDERMAN)) return 0;

            ToIntFunction<Integer> levelToDamage = (level -> {
                if (level <= 4) return level * 15;
                else if (level == 5 || level == 6) return 60 + (level == 5 ? 20 : 40);
                else return 130;
            });

            ItemEnchantment enchantment = b.getEnchantment("ender_slayer");

            if (enchantment == null || enchantment.getBaseEnchantment() == null) return 0;

            return levelToDamage.applyAsInt(
                    b.getEnchantment("ender_slayer")
                            .getLevel());
        });
    }

}
