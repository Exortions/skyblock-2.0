package com.skyblock.skyblock.features.npc;

import com.skyblock.skyblock.Skyblock;
import net.citizensnpcs.api.event.DespawnReason;
import org.bukkit.Bukkit;

import java.util.HashMap;

public class NPCHandler {

    private HashMap<String, NPC> npcs;

    public NPCHandler() {
        this.npcs = new HashMap<>();
    }

    public void spawnAll() {
        for (NPC npc : npcs.values()) {
            npc.spawn();
        }
    }

    public void killAll() {
        for (NPC npc : npcs.values()) {
            npc.getNpc().destroy();
            npc.getStand().remove();
            npc.getClick().remove();

            npc.getNpc().getOwningRegistry().despawnNPCs(DespawnReason.PLUGIN);
            npc.getNpc().getOwningRegistry().deregisterAll();
        }
    }

    public void registerNPC(String id, NPC npc) {
        Bukkit.getPluginManager().registerEvents(npc, Skyblock.getPlugin(Skyblock.class));

        this.npcs.put(id, npc);
    }

    public NPC getNPC(String id) {
        return this.npcs.get(id);
    }

    public NPC getNPCByName(String name) {
        for (NPC npc : this.npcs.values()) {
            if (npc.getName().equalsIgnoreCase(name)) {
                return npc;
            }
        }
        return null;
    }

    public HashMap<String, NPC> getNPCs() {
        return this.npcs;
    }

    public void removeNPC(String id) {
        this.npcs.remove(id);
    }

}
