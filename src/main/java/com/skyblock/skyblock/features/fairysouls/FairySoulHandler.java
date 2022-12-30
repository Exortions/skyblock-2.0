package com.skyblock.skyblock.features.fairysouls;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.Util;
import de.tr7zw.nbtapi.NBTEntity;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

@Getter
public class FairySoulHandler {
    public boolean initialized;
    private final ArrayList<ArmorStand> souls;
    private final HashMap<String, ArrayList<Location>> stands;
    private final ArrayList<Location> locations = new ArrayList<>();

    public FairySoulHandler() {
        souls = new ArrayList<>();
        stands = new HashMap<>();
        initialized = false;
    }

    public void init() {
        File file = new File(Skyblock.getPlugin().getDataFolder(), "fairy_souls.json");

        if (!file.exists()) {
            return;
        }

        try {
            JSONArray souls = (JSONArray) ((JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8)))).get("souls");

            for (Object object : souls) {
                String soul = (String) object;
                String[] split = soul.split(", ");

                int x = Integer.parseInt(split[0]);
                int y = Integer.parseInt(split[1]);
                int z = Integer.parseInt(split[2]);

                Location spawn = new Location(Skyblock.getSkyblockWorld(), x, y, z);

                Chunk chunk = spawn.getChunk();
                if (!stands.containsKey(getId(chunk))) stands.put(getId(chunk), new ArrayList<>());

                stands.get(getId(chunk)).add(spawn);
                locations.add(spawn);
            }
        } catch (IOException | ParseException | NumberFormatException ex) {
            ex.printStackTrace();

            return;
        }

        initialized = true;
    }

    public void loadChunk(Chunk chunk) {
        if (stands.containsKey(getId(chunk))) {
            for (Location location : stands.get(getId(chunk))) {
                int xMult = location.getX() <= 0 ? 1 : 1;
                int zMult = location.getZ() <= 0 ? 1 : 1;
                Location spawn = new Location(location.getWorld(), location.getX() + (0.5 * xMult), location.getY() - 1.5, location.getZ() + (0.5 * zMult));
                addStand(spawn);
            }

            stands.remove(getId(chunk));
        }
    }

    public void addStand(Location l) {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ARMOR_STAND, "", l);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        ArmorStandTrait stand = npc.getOrAddTrait(ArmorStandTrait.class);
        Equipment equipment = npc.getOrAddTrait(Equipment.class);

        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(false);

        equipment.set(Equipment.EquipmentSlot.HELMET, Util.idToSkull(new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal()), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2OTIzYWQyNDczMTAwMDdmNmFlNWQzMjZkODQ3YWQ1Mzg2NGNmMTZjMzU2NWExODFkYzhlNmIyMGJlMjM4NyJ9fX0="));
        npc.getEntity().setMetadata("isFairySoul", new FixedMetadataValue(Skyblock.getPlugin(), false));
    }

    public String getId(Chunk chunk) {
        return chunk.getWorld().getName() + "." + chunk.getX() + "." + chunk.getZ();
    }
}
