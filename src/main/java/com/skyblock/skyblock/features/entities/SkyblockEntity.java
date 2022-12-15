package com.skyblock.skyblock.features.entities;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public abstract class SkyblockEntity {

    public static final String HELMET = "HELMET";
    public static final String CHEST = "CHEST";
    public static final String LEGS = "LEGS";
    public static final String BOOTS = "BOOTS";
    public static final String HAND = "HAND";
    private Entity vanilla;
    private final EntityType entityType;
    @Setter
    private SkyblockEntityData entityData;
    private final Skyblock plugin;
    protected int tick;
    @Setter
    protected int lifeSpan;
    @Setter
    private SkyblockPlayer lastDamager;

    public static class Equipment {
        public ItemStack hand;
        public ItemStack helmet;
        public ItemStack chest;
        public ItemStack legs;
        public ItemStack boots;

        public Equipment(ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots, ItemStack hand){
            this.hand = hand;
            this.helmet = helmet;
            this.chest = chest;
            this.legs = legs;
            this.boots = boots;
        }

        public Equipment() { }
    }

    public SkyblockEntity(EntityType type) {
        entityType = type;
        plugin = Skyblock.getPlugin();
        tick = 0;
        lifeSpan = 15 * 20;
        lastDamager = null;
    }

    protected void loadStats(int health, int damage, boolean isUndead, boolean isArthropod,
                             boolean isHostile, Equipment equipment, String name, int level, int xp) {
        loadStats(health, damage, isUndead, isArthropod, isHostile, equipment, name, level, xp, "Combat");
    }

    protected void loadStats(int health, int damage, boolean isUndead, boolean isArthropod,
                           boolean isHostile, Equipment equipment, String name, int level, int xp, String skill) {
        entityData = new SkyblockEntityData();

        entityData.maximumHealth = health;
        entityData.health = health;
        entityData.damage = damage;

        entityData.isUndead = isUndead;
        entityData.isArthropod = isArthropod;
        entityData.isHostile = isHostile;

        entityData.entityName = name;
        entityData.level = level;

        entityData.skill = Skill.parseSkill(skill);
        entityData.xp = xp;

        entityData.helmet = equipment.helmet;
        entityData.chestplate = equipment.chest;
        entityData.leggings = equipment.legs;
        entityData.boots = equipment.boots;
        entityData.hand = equipment.hand;
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
                        onDeath();
                        cancel();
                        plugin.getEntityHandler().unregisterEntity(vanilla.getEntityId());
                    }else{
                        vanilla.setCustomNameVisible(true);
                        vanilla.setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + getEntityData().level + ChatColor.DARK_GRAY + "] " + ChatColor.RED + getEntityData().entityName + " " + ChatColor.GREEN + (getEntityData().health) + ChatColor.DARK_GRAY + "/" + ChatColor.GREEN + (getEntityData().maximumHealth) + ChatColor.RED + "❤");
                        ((LivingEntity) vanilla).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 5, true, false));

                        if (getEntityData().health <= 0) {
                            if (getLastDamager() != null) {
                                getLastDamager().setExtraData("lastKilledLoc", getVanilla().getLocation());
                                getLastDamager().setExtraData("lastKilledType", getVanilla().getType());
                                Skill.reward(getEntityData().skill, getEntityData().xp, getLastDamager());
                            }

                            vanilla.setCustomName(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + getEntityData().level + ChatColor.DARK_GRAY + "] " + ChatColor.RED + getEntityData().entityName + " " + ChatColor.GREEN + (0) + ChatColor.DARK_GRAY + "/" + ChatColor.GREEN + (getEntityData().maximumHealth) + ChatColor.RED + "❤");
                            plugin.getEntityHandler().unregisterEntity(vanilla.getEntityId());
                            living.setHealth(0);
                        }

                        if (getEntityData().isHostile) {
                            for (Entity entity : getVanilla().getNearbyEntities(5, 2, 5)){
                                if (entity instanceof Player && living instanceof Monster) {
                                    ((Monster) living).setTarget((LivingEntity) entity);
                                }
                            }
                        }

                        lifeSpan--;

                        if (lifeSpan < 0) {
                            plugin.getEntityHandler().unregisterEntity(vanilla.getEntityId());
                            vanilla.remove();
                            onDespawn();
                            onDeath();
                            cancel();
                        }

                        tick();
                        tick++;
                    }
                }
            }.runTaskTimer(plugin, 5L, 1);
        }

        vanilla.setMetadata("skyblockEntityData", new FixedMetadataValue(plugin, getEntityData().toString()));
        Skyblock.getPlugin().addRemoveable(vanilla);

        return vanilla;
    }

    protected abstract void tick();

    protected void onDeath() {}

    public void setHealth(long health) {
        getEntityData().health = health;
    }

    public void subtractHealth(long health) {
        setHealth(getEntityData().health - health);
    }

    public void damage(long damage, SkyblockPlayer damager, boolean crit) {
        this.subtractHealth(damage);
        this.setLastDamager(damager);

        Util.setDamageIndicator(this.vanilla.getLocation(), crit ? Util.addCritTexture(Math.round(damage)) : ChatColor.GRAY + "" + Math.round(damage), false);
    }

    public void onDespawn() { }
    public void onDamage(EntityDamageByEntityEvent event, SkyblockPlayer player, boolean crit, double damage) { }
}
