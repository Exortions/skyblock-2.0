package com.skyblock.skyblock.features.entities;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

@Getter
public abstract class SkyblockEntity {

    public static final String HELMET = "HELMET";
    public static final String CHEST = "CHEST";
    public static final String LEGS = "LEGS";
    public static final String BOOTS = "BOOTS";
    public static final String HAND = "HAND";
    private Entity vanilla;
    private final EntityType entityType;
    private SkyblockEntityData entityData;
    private final Skyblock plugin;
    protected int tick;
    @Setter
    private int lifeSpan;
    @Setter
    private SkyblockPlayer lastDamager;

    public SkyblockEntity(Skyblock sb, EntityType type) {
        entityType = type;
        plugin = sb;
        tick = 0;
        lifeSpan = 15 * 20;
        lastDamager = null;
    }

    protected void loadStats(int health, int damage, boolean isUndead, boolean isArthropod,
                           boolean isHostile, HashMap<String, ItemStack> equipment, String name, int level) {
        entityData = new SkyblockEntityData();

        entityData.maximumHealth = health;
        entityData.health = health;
        entityData.damage = damage;

        entityData.isUndead = isUndead;
        entityData.isArthropod = isArthropod;
        entityData.isHostile = isHostile;

        entityData.entityName = name;
        entityData.level = level;

        entityData.helmet = equipment.get(HELMET);
        entityData.chestplate = equipment.get(CHEST);
        entityData.leggings = equipment.get(LEGS);
        entityData.boots = equipment.get(BOOTS);
        entityData.hand = equipment.get(HAND);
    }

    public Entity spawn(Location location) {
        vanilla = location.getWorld().spawn(location, entityType.getEntityClass());

        if (vanilla instanceof LivingEntity){
            LivingEntity living = (LivingEntity) vanilla;

            living.getEquipment().setItemInHand(getEntityData().hand);
            living.getEquipment().setHelmet(getEntityData().helmet);
            living.getEquipment().setChestplate(getEntityData().chestplate);
            living.getEquipment().setLeggings(getEntityData().leggings);
            living.getEquipment().setBoots(getEntityData().boots);

            if (living.getType().equals(EntityType.ENDERMAN)){
                if (getEntityData().hand != null) {
                    ((Enderman) vanilla).setCarriedMaterial(getEntityData().hand.getData());
                }
            }

            plugin.getEntityHandler().registerEntity(this);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (vanilla.isDead()){
                        cancel();
                        plugin.getEntityHandler().unregisterEntity(vanilla.getEntityId());
                    }else{
                        vanilla.setCustomNameVisible(true);
                        vanilla.setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + getEntityData().level + ChatColor.DARK_GRAY + "] " + ChatColor.RED + getEntityData().entityName + " " + ChatColor.GREEN + (getEntityData().health) + ChatColor.DARK_GRAY + "/" + ChatColor.GREEN + (getEntityData().maximumHealth) + ChatColor.RED + "❤");
                        ((LivingEntity) vanilla).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 5, true, false));

                        if (getEntityData().health <= 0) {
                            vanilla.setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + getEntityData().level + ChatColor.DARK_GRAY + "] " + ChatColor.RED + getEntityData().entityName + " " + ChatColor.GREEN + (0) + ChatColor.DARK_GRAY + "/" + ChatColor.GREEN + (getEntityData().maximumHealth) + ChatColor.RED + "❤");
                            plugin.getEntityHandler().unregisterEntity(vanilla.getEntityId());
                            living.setHealth(0);
                        }

                        lifeSpan--;

                        if (lifeSpan < 0) {
                            plugin.getEntityHandler().unregisterEntity(vanilla.getEntityId());
                            vanilla.remove();
                            cancel();
                        }

                        tick();
                        tick++;
                    }
                }
            }.runTaskTimer(plugin, 5L, 1);
        }

        vanilla.setMetadata("skyblockEntityData", new FixedMetadataValue(plugin, getEntityData().toString()));

        return vanilla;
    }

    protected abstract void tick();

    public void setHealth(long health) {
        getEntityData().health = health;
    }

    public void subtractHealth(long health) {
        setHealth(getEntityData().health - health);
    }
}
