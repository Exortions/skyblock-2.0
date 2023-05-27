package com.skyblock.skyblock.features.entities;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.events.SkyblockEntityDeathEvent;
import com.skyblock.skyblock.features.skills.Skill;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemHandler;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class SkyblockEntity {

    protected static final ItemHandler handler = Skyblock.getPlugin().getItemHandler();
    protected static final SkyblockEntityHandler entityHandler = Skyblock.getPlugin().getEntityHandler();
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
    private final List<Player> damaged;
    private EntityNameTag nameTag;

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
        damaged = new ArrayList<>();
    }

    protected void loadStats(int health, int damage, boolean isUndead, boolean isArthropod,
                             boolean isHostile, Equipment equipment, String name, int level, int xp) {
        loadStats(health, damage, isUndead, isArthropod, isHostile, equipment, name, level, xp, "Combat", 0, 0, "");
    }
    protected void loadStats(int health, int damage, boolean isUndead, boolean isArthropod,
                             boolean isHostile, Equipment equipment, String name, int level, int xp, String dropId) {
        loadStats(health, damage, isUndead, isArthropod, isHostile, equipment, name, level, xp, "Combat", 0, 0, dropId);
    }

    protected void loadStats(int health, int damage, boolean isUndead, boolean isArthropod,
                             boolean isHostile, Equipment equipment, String name, int level, int xp, int orbs, int coins, String dropId) {
        loadStats(health, damage, isUndead, isArthropod, isHostile, equipment, name, level, xp, "Combat", orbs, coins, dropId);
    }

    protected void loadStats(int health, int damage, boolean isUndead, boolean isArthropod,
                           boolean isHostile, Equipment equipment, String name, int level, int xp, String skill, int orbs, int coins, String dropId) {
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

        entityData.orbs = orbs;
        entityData.coins = coins;

        entityData.helmet = equipment.helmet;
        entityData.chestplate = equipment.chest;
        entityData.leggings = equipment.legs;
        entityData.boots = equipment.boots;
        entityData.hand = equipment.hand;

        entityData.dropId = dropId;
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

            if (living.getType().equals(EntityType.ENDERMAN) && getEntityData().hand != null) {
                ((Enderman) vanilla).setCarriedMaterial(getEntityData().hand.getData());
            }

            entityHandler.registerEntity(this);

            nameTag = new EntityNameTag(this);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (vanilla.isDead()){
                        entityHandler.unregisterEntity(vanilla.getEntityId());

                        onDeath();
                        cancel();
                    } else {
                        String name = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + getEntityData().level + ChatColor.DARK_GRAY + "] " + ChatColor.RED + getEntityData().entityName + " " + ChatColor.GREEN + Math.max(0, getEntityData().health) + ChatColor.DARK_GRAY + "/" + ChatColor.GREEN + (getEntityData().maximumHealth) + ChatColor.RED + "❤";

                        nameTag.tick(name);

                        if (!(vanilla instanceof LivingEntity)) return;

                        living.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 5, true, false));

                        if (getEntityData().health <= 0) {
                            if (getLastDamager() != null) {
                                getLastDamager().setExtraData("lastKilledLoc", getVanilla().getLocation());
                                getLastDamager().setExtraData("lastKilledType", getVanilla().getType());
                                Skill.reward(getEntityData().skill, getEntityData().xp, getLastDamager());
                            }

                            living.setHealth(0);
                        }

                        if (getEntityData().isHostile && living instanceof Monster) {
                            Monster monster = (Monster) living;

                            if (monster.getTarget() != null && monster.getTarget().getLocation().distance(monster.getLocation()) > 10) {
                                monster.setTarget(null);

                                for (Entity entity : getVanilla().getNearbyEntities(5, 2, 5)){
                                    if (entity instanceof Player && !entity.hasMetadata("NPC")) {
                                        monster.setTarget((LivingEntity) entity);
                                    }
                                }

                                if (((Monster) living).getTarget() != null) lifeSpan += 15 * 20;
                            }
                        }

                        lifeSpan--;

                        if (lifeSpan < 0) {
                            nameTag.death();
                            entityHandler.unregisterEntity(vanilla.getEntityId());
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

    public String getDropId() {
        return getEntityData().dropId;
    }

    public List<EntityDrop> getDrops() {
        return new ArrayList<>();
    }
    public List<EntityDrop> getRareDrops() { return new ArrayList<>(); }

    protected void tick() {}

    protected void onDeath() {
        nameTag.death();

        if (getLastDamager() != null) {
            boolean foundRareDrop = false;

            SkyblockPlayer lastDamager = getLastDamager();
            boolean hasTelekinesis = lastDamager.hasTelekinesis();

            Bukkit.getPluginManager().callEvent(new SkyblockEntityDeathEvent(this, lastDamager));

            List<EntityDrop> drops = getDrops();

            if (!getDropId().equals("") && drops.isEmpty()) drops = Skyblock.getPlugin().getEntityHandler().getMobDropHandler().getDrops(getDropId());

            drops.addAll(getRareDrops());

            Util.shuffle(drops);

            for (EntityDrop drop : drops) {
                EntityDropRarity type = drop.getRarity();
                double r = Util.random(0.0, 100);
                double magicFind = getLastDamager().getStat(SkyblockStat.MAGIC_FIND);
                double c = drop.getChance() + magicFind;
                if (r > c) continue;
                if (foundRareDrop && type != EntityDropRarity.COMMON) continue;
                ItemStack stack = Util.toSkyblockItem(drop.getItem());
                stack.setAmount((int) Util.random(drop.getMin(), drop.getMax()));

                if (type != EntityDropRarity.COMMON && getLastDamager() != null) {
                    getLastDamager().getBukkitPlayer().playSound(getLastDamager().getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 10, 2);
                    getLastDamager().getBukkitPlayer().sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "RARE DROP " + drop.getItem().getItemMeta().getDisplayName() + (magicFind > 0 ? " " + ChatColor.AQUA + "(" + magicFind + "% Magic Find)" : ""));
                }

                if (!hasTelekinesis) getLastDamager().dropItem(stack, getVanilla().getLocation());
                else {
                    if (lastDamager.getBukkitPlayer().getInventory().firstEmpty() == -1) {
                        lastDamager.getBukkitPlayer().getWorld().dropItem(lastDamager.getBukkitPlayer().getLocation(), stack);
                    } else {
                        lastDamager.getBukkitPlayer().getInventory().addItem(stack);
                    }
                }

                if (type != EntityDropRarity.COMMON) foundRareDrop = true;
            }

            if (getEntityData().orbs > 0) {
                int orbs = getEntityData().orbs;

                if (!hasTelekinesis) {
                    ExperienceOrb orb = getVanilla().getWorld().spawn(getVanilla().getLocation(), ExperienceOrb.class);
                    orb.setExperience(orbs);
                } else {
                    lastDamager.getBukkitPlayer().giveExp(orbs);
                }
            }

            int amount = Skyblock.getPlugin().getEntityHandler().getMobDropHandler().getCoins(getEntityData().dropId);

            if (amount > 0) {
                if (!hasTelekinesis) {
                    getLastDamager().dropItems(Util.createCoins(amount), getVanilla().getLocation());
                } else {
                    lastDamager.addCoins(amount);
                    lastDamager.getBukkitPlayer().playSound(lastDamager.getBukkitPlayer().getLocation(), Sound.ORB_PICKUP, 10, 2);
                    lastDamager.setExtraData("lastpicked_coins", amount);

                    Util.delay(() -> lastDamager.setExtraData("lastpicked_coins", null), 80);
                }
            }
        }
    }

    public void setHealth(long health) {
        getEntityData().health = health;
    }

    public void subtractHealth(long health) {
        setHealth(getEntityData().health - health);
    }

    public void damage(long damage, SkyblockPlayer damager, boolean crit) {
        damage(damage, damager, crit, ChatColor.GRAY);
    }

    public void damage(long damage, SkyblockPlayer damager, boolean crit, ChatColor colorOverride) {
        if (getVanilla().isDead()) return;

        this.subtractHealth(damage);
        this.setLastDamager(damager);

        if (getVanilla() instanceof LivingEntity) {
            PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(((CraftEntity) getVanilla()).getHandle(), (byte) 2);

            Bukkit.getOnlinePlayers().forEach((p) -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
        }

        Util.setDamageIndicator(this.vanilla.getLocation(), crit ? Util.addCritTexture(Math.round(damage)) : colorOverride + "" + Util.formatLong(damage), false);
    }

    public void onDespawn() { }
    public void onDamage(EntityDamageByEntityEvent event, SkyblockPlayer player, boolean crit, double damage) { }

    public boolean isUndead() {
        return getVanilla().getType().equals(EntityType.ZOMBIE) ||
                getVanilla().getType().equals(EntityType.SKELETON) ||
                getVanilla().getType().equals(EntityType.PIG_ZOMBIE);
    }
    
}
