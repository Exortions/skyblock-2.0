package com.skyblock.skyblock.utilities.sign;

import com.skyblock.skyblock.Skyblock;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignManager {

    private final PluginManager pluginManager;
    private final Map<UUID, SignGui> guis;
    private final Skyblock skyblock;

    public SignManager(Skyblock skyblock) {
        this.pluginManager = Bukkit.getPluginManager();
        this.guis = new HashMap<>();
        this.skyblock = skyblock;

        initialize();
    }

    public void initialize() {
        this.pluginManager.registerEvents(new SignListener(), this.skyblock);
    }

    private class SignListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            final Player player = event.getPlayer();

            ChannelDuplexHandler handler = new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                    if (packet instanceof PacketPlayInUpdateSign) {
                        PacketPlayInUpdateSign signPacket = (PacketPlayInUpdateSign) packet;

                        if (guis.containsKey(player.getUniqueId())) {
                            SignGui gui = guis.get(player.getUniqueId());

                            BlockPosition blockPosition = SignReflection.getValue(signPacket, "a");
                            IChatBaseComponent[] lines = SignReflection.getValue(signPacket, "b");
                            String[] lines2 = new String[4];

                            int i = 0;

                            for (IChatBaseComponent icbc : lines) {
                                lines2[i] = icbc.getText();

                                i++;
                            }

                            gui.getCompleteHandler().onSignComplete(new SignCompleteEvent(player, blockPosition, lines2));
                            guis.remove(player.getUniqueId());
                        }
                    }

                    super.channelRead(channelHandlerContext, packet);
                }
            };

            final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();

            try {
                pipeline.addBefore("packet_handler", "sb-sign-manager", handler);
            } catch (Exception ignored) {} // player already has a handler
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            final Channel channel = ((CraftPlayer) event.getPlayer()).getHandle().playerConnection.networkManager.channel;

            channel.eventLoop().submit(() -> channel.pipeline().remove(event.getPlayer().getName()));
            guis.remove(event.getPlayer().getUniqueId());
        }
    }

    public void add(UUID uuid, SignGui gui) {
        this.guis.put(uuid, gui);
    }

}
