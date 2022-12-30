package com.skyblock.skyblock.features.auction.display;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionHouse;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DisplayHandler implements Listener {

    private final AuctionHouse house;

    private final List<Display> displays = new ArrayList<>();

    public DisplayHandler(Skyblock plugin, AuctionHouse house, Display... displays) {
        this.house = house;

        this.displays.addAll(Arrays.asList(displays));

        Bukkit.getPluginManager().registerEvents(this, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!house.isBotFinished()) return;

                updateDisplays();
            }
        }.runTaskTimer(plugin, 0, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!house.isBotFinished()) return;

                Arrays.stream(displays).forEach(Display::updateItemPosition);
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler
    public void onSignRightClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        if (event.getClickedBlock().getType() != Material.SIGN && event.getClickedBlock().getType() != Material.WALL_SIGN) return;

        Display display = displays.stream().filter(d -> d.isPossibleSignLocation(event.getClickedBlock().getLocation())).findFirst().orElse(null);

        if (display == null) return;
        if (display.getCurrentAuction() == null ) return;

        event.getPlayer().performCommand("ah " + display.getCurrentAuction().getUuid().toString());
    }

    public void updateDisplays() {
        List<Auction> top = getTopPriceAuctions().stream().limit(34).collect(Collectors.toList());

        List<Display> ranked = this.displays.stream().sorted(Comparator.comparingInt(Display::getRank)).collect(Collectors.toList());

        try {
            for (int i = 0; i < top.size(); i++) {
                ranked.get(i).update(top.get(i));
            }
        } catch (IndexOutOfBoundsException ignored) {}
    }

    public List<Auction> getTopPriceAuctions() {
        return this.house.getAuctions().stream().sorted((a1, a2) -> Long.compare(a2.getPrice(), a1.getPrice())).collect(Collectors.toList());
    }

}
