package com.skyblock.skyblock.utilities;

import com.skyblock.skyblock.Skyblock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Data
public class AnvilGui {

    private final HashMap<AnvilSlot, ItemStack> items = new HashMap<>();
    private AnvilClickEventHandler handler;
    private Inventory inventory;
    private Listener listener;
    private Player player;
    private String title;

    public AnvilGui(Player player, final AnvilClickEventHandler handler) {
        this.handler = handler;
        this.player = player;
        this.title = null;

        this.listener = new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (!(event.getWhoClicked() instanceof Player)) return;

                Player clicker = (Player) event.getWhoClicked();

                if (!event.getInventory().equals(inventory)) return;

                event.setCancelled(true);

                ItemStack item = event.getCurrentItem();
                int slot = event.getRawSlot();
                String name = "";

                if (item != null && item.hasItemMeta()) name = item.getItemMeta().getDisplayName();

                AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);

                handler.onAnvilClick(clickEvent);

                if (clickEvent.willClose()) clicker.closeInventory();

                if (clickEvent.willDestroy()) AnvilGui.this.destory();
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (!(event.getPlayer() instanceof Player)) return;

                Inventory inventory = event.getInventory();

                if (inventory.equals(AnvilGui.this.inventory)) {
                    inventory.clear();
                    AnvilGui.this.destory();
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(player)) AnvilGui.this.destory();
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, Skyblock.getPlugin(Skyblock.class));
    }

    private static class AnvilContainer extends ContainerAnvil {
        public AnvilContainer(EntityHuman entity) {
            super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
        }

        @Override
        public boolean a(EntityHuman entityhuman) {
            return true;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum AnvilSlot {
        INPUT_LEFT(0),
        INPUT_RIGHT(0),
        OUTPUT(2);

        private final int slot;

        public static AnvilSlot bySlot(int slot) {
            for (AnvilSlot value : values()) {
                if (value.getSlot() == slot) return value;
            }

            return null;
        }
    }

    public static class AnvilClickEvent {
        private final AnvilSlot slot;

        private final String name;

        private boolean destroy;
        private boolean close;

        public AnvilClickEvent(AnvilSlot slot, String name) {
            this.slot = slot;
            this.name = name;

            this.destroy = true;
            this.close = true;
        }

        public String getName() {
            return name;
        }

        public boolean willDestroy() {
            return destroy;
        }

        public void setDestroy(boolean destroy) {
            this.destroy = destroy;
        }

        public boolean willClose() {
            return close;
        }

        public void setClose(boolean close) {
            this.close = close;
        }

    }

    public interface AnvilClickEventHandler {
        void onAnvilClick(AnvilClickEvent event);
    }

    public AnvilGui setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
        return this;
    }

    public AnvilGui open() {
        EntityPlayer player = ((CraftPlayer) this.player).getHandle();

        AnvilContainer container = new AnvilContainer(player);

        inventory = container.getBukkitView().getTopInventory();

        for (AnvilSlot slot : items.keySet()) inventory.setItem(slot.getSlot(), items.get(slot));

        int c = player.nextContainerCounter();

        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", IChatBaseComponent.ChatSerializer.a(title != null ? title : "Input"), 0));
        player.playerConnection.sendPacket(new PacketPlayOutWindowData());

        player.activeContainer = container;
        player.activeContainer.windowId = c;
        player.activeContainer.addSlotListener(player);

        return this;
    }

    public AnvilGui setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        return this;
    }

    public void destory() {
        this.player = null;
        this.handler = null;
        this.items.clear();

        HandlerList.unregisterAll(listener);

        listener = null;
    }

}
