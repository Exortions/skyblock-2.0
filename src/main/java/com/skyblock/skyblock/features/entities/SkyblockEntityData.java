package com.skyblock.skyblock.features.entities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.skyblock.skyblock.features.skills.Skill;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.StringReader;

public class SkyblockEntityData {

    public long health = 100;
    public long maximumHealth = 100;
    public int damage = 0;

    public int level = 1;

    public boolean isHostile = true;
    public boolean isUndead = false;
    public boolean isArthropod = false;

    public ItemStack helmet = null;
    public ItemStack chestplate = null;
    public ItemStack leggings = null;
    public ItemStack boots = null;

    public ItemStack hand = null;

    public String entityName = "";
    public Skill skill;
    public int xp;

    public int orbs;
    public int coins;

    public String dropId;

    @Override
    public String toString() {
        JsonObject object = new JsonObject();

        object.addProperty("health", health);
        object.addProperty("damage", damage);
        object.addProperty("maxHealth", maximumHealth);

        object.addProperty("level", level);
        object.addProperty("entityName", entityName);

        object.addProperty("isHostile", isHostile);
        object.addProperty("isUndead", isUndead);
        object.addProperty("isArthropod", isArthropod);

        object.addProperty("skill", skill.getName());
        object.addProperty("skillXP", xp);

        object.addProperty("orbs", orbs);
        object.addProperty("coins", coins);

        return object.toString();
    }

    public static SkyblockEntityData parse(Entity entity) {
        if (!entity.hasMetadata("skyblockEntityData")) throw new IllegalArgumentException("Entity isn't a skyblock entity.");

        String json = entity.getMetadata("skyblockEntityData").get(0).asString();

        JsonElement decoded = new JsonParser().parse(new JsonReader(new StringReader(json)));

        JsonObject jsonObject = decoded.getAsJsonObject();

        SkyblockEntityData data = new SkyblockEntityData();

        data.health = jsonObject.get("health").getAsLong();
        data.maximumHealth = jsonObject.get("maxHealth").getAsLong();
        data.damage = jsonObject.get("damage").getAsInt();

        data.level = jsonObject.get("level").getAsInt();
        data.entityName = jsonObject.get("entityName").getAsString();

        data.isHostile = jsonObject.get("isHostile").getAsBoolean();
        data.isUndead = jsonObject.get("isUndead").getAsBoolean();
        data.isArthropod = jsonObject.get("isArthropod").getAsBoolean();

        data.skill = Skill.parseSkill(jsonObject.get("skill").getAsString());
        data.xp = jsonObject.get("skillXP").getAsInt();

        data.orbs = jsonObject.get("orbs").getAsInt();
        data.coins = jsonObject.get("coins").getAsInt();

        return data;
    }
}
