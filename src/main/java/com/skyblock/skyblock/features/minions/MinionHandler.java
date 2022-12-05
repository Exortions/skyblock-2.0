package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.MiningMinionType;
import com.skyblock.skyblock.enums.MinionType;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.ArmorStand;

import java.util.*;

public class MinionHandler {

    @Data
    @SerializableAs(value = "Minion")
    public static class MinionSerializable implements ConfigurationSerializable {

        private final MinionBase base;
        private final MinionType<?> type;
        private final Location location;
        private final UUID owner;
        private final UUID uuid;
        private final int level;

        @Override
        public Map<String, Object> serialize() {
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();

            result.put("type", this.base.getType().name());
            result.put("location", this.location);
            result.put("owner", this.owner.toString());
            result.put("uuid", this.uuid.toString());
            result.put("level", this.level);

            return result;
        }

        public static MinionSerializable deserialize(Map<String, Object> args) {
            MinionBase base;

            MinionType<?> type;
            Location location;
            UUID owner;
            UUID uuid;
            int level;

            if (args.containsKey("type")) {
                type = MinionType.getEnumValue((String) args.get("type"));
            } else {
                throw new IllegalArgumentException("Could not find serializable class MinionType<?> in serialized data");
            }

            if (args.containsKey("location")) {
                location = (Location) args.get("location");
            } else {
                throw new IllegalArgumentException("Could not find serializable class Location in serialized data");
            }

            if (args.containsKey("owner")) {
                owner = UUID.fromString((String) args.get("owner"));
            } else {
                throw new IllegalArgumentException("Could not find serializable class UUID in serialized data");
            }

            if (args.containsKey("uuid")) {
                uuid = UUID.fromString((String) args.get("uuid"));
            } else {
                throw new IllegalArgumentException("Could not find serializable class UUID in serialized data");
            }

            if (args.containsKey("level")) {
                level = (int) args.get("level");
            } else {
                throw new IllegalArgumentException("Could not find level in serialized data");
            }

            if (type instanceof MiningMinionType) base = new MiningMinion((MiningMinionType) type, uuid);
            else base = null;

            return new MinionSerializable(base, type, location, owner, uuid, level);
        }

    }

    private final HashMap<UUID, List<MinionSerializable>> minions;

    public MinionHandler() {
        this.minions = new HashMap<>();

        this.reloadPlayers();
    }

    public void reloadPlayers() {
        for (SkyblockPlayer player : SkyblockPlayer.playerRegistry.values()) {
            this.reloadPlayer(player);
        }
    }

    public void reloadPlayer(SkyblockPlayer player) {
        this.minions.put(player.getBukkitPlayer().getUniqueId(), new ArrayList<>());

        for (MinionSerializable minion : (List<MinionSerializable>) player.getValue("island.minions")) {
            boolean found = false;

            for (ArmorStand stand : minion.getLocation().getWorld().getEntitiesByClass(ArmorStand.class)) {
                if (stand.hasMetadata("minion")) {
                    if (stand.getMetadata("minion_id").get(0).asString().equals(minion.getUuid().toString())) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) minion.getBase().spawn(player, minion.getLocation(), minion.getLevel());
            else initializeMinion(player, minion.getBase(), minion.getLocation());
        }
    }

    public void initializeMinion(SkyblockPlayer player, MinionBase minion, Location location) {
        if (!this.minions.containsKey(player.getBukkitPlayer().getUniqueId())) {
            this.minions.put(player.getBukkitPlayer().getUniqueId(), new ArrayList<>());
        }

        this.minions.get(player.getBukkitPlayer().getUniqueId()).add(new MinionSerializable(minion, minion.getType(), location, player.getBukkitPlayer().getUniqueId(), minion.getUuid(), minion.getLevel()));
    }

    public void deleteAll() {
        for (UUID uuid : this.minions.keySet()) {
            for (MinionSerializable minion : this.minions.get(uuid)) {
                minion.getBase().getMinion().remove();
                minion.getBase().getText().remove();

                SkyblockPlayer player = SkyblockPlayer.getPlayer(uuid);

                List<MinionSerializable> minions = (List<MinionSerializable>) player.getValue("island.minions");

                if (minions == null) minions = new ArrayList<>();

                boolean found = false;
                int i = 0;

                for (MinionSerializable minionSerializable : minions) {
                    if (minionSerializable.getUuid().equals(minion.getUuid())) {
                        found = true;
                        break;
                    }

                    i++;
                }

                if (!found) minions.add(minion);
                else minions.set(i, minion);

                player.setValue("island.minions", minions);
            }
        }
    }

    public MinionBase getMinion(UUID uuid) {
        for (UUID uuid1 : this.minions.keySet()) {
            for (MinionSerializable minion : this.minions.get(uuid1)) {
                if (minion.getUuid().equals(uuid)) return minion.getBase();
            }
        }

        return null;
    }

    public HashMap<UUID, List<MinionSerializable>> getMinions() {
        return minions;
    }
}
