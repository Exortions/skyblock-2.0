package com.skyblock.skyblock.features.auction.display;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionHouse;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DisplayHandler {

    private final AuctionHouse house;

    private final List<Display> displays = new ArrayList<>();

    public DisplayHandler(Skyblock plugin, AuctionHouse house, Display... displays) {
        this.house = house;

        this.displays.addAll(Arrays.asList(displays));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!house.isBotFinished()) return;

                updateDisplays();
            }
        }.runTaskTimer(plugin, 0, 20);
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
