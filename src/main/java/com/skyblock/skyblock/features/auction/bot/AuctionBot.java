package com.skyblock.skyblock.features.auction.bot;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.enums.Reforge;
import com.skyblock.skyblock.features.auction.Auction;
import com.skyblock.skyblock.features.auction.AuctionBid;
import com.skyblock.skyblock.features.auction.AuctionCategory;
import com.skyblock.skyblock.features.auction.AuctionHouse;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBase;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTReflectionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class AuctionBot {
    private final boolean useRealName;
    private final String nameType;

    public AuctionBot() {
        this.useRealName = Skyblock.getPlugin().getConfig().getBoolean("auction_bot.use_real_name.enabled");
        this.nameType = Skyblock.getPlugin().getConfig().getString("auction_bot.use_real_name.type");
    }

    public List<Auction> getAuctionsAPI(int page) {
        long start = System.currentTimeMillis();
        List<Auction> auctions = new ArrayList<>();

        try {
            URL url = new URL("https://api.hypixel.net/skyblock/auctions?page=" + page);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();

            if (status != 200) {
                Skyblock.getPlugin().sendMessage("&cFailed to initialize Auction Bot: &8Status Code " + status);
                return new ArrayList<>();
            }

            JSONParser parser = new JSONParser();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            Object obj = parser.parse(in);

            JSONObject jsonObject = (JSONObject) obj;
            JSONArray auctionsJson = (JSONArray) jsonObject.get("auctions");
            HashMap<Integer, List<Object>> queueForThreads = new HashMap<>();

            int THREAD_AMOUNT = 20;

            int queueIndex = 0;

            for (int i = 0; i < THREAD_AMOUNT; i++) {
                queueForThreads.put(i, new ArrayList<>());
            }

            for (Object auc : auctionsJson) {
                List<Object> queue = queueForThreads.get(queueIndex);
                queue.add(auc);
                queueForThreads.put(queueIndex, queue);

                if (queueIndex == THREAD_AMOUNT - 1) {
                    queueIndex = 0;
                } else {
                    queueIndex++;
                }
            }

            Skyblock skyblock = Skyblock.getPlugin();
            skyblock.sendMessage("Queued " + queueForThreads.size() + " threads for the Auction Bot, containing " + auctionsJson.size() + " auctions in page " + page + " of the Hypixel API.");

            final int[] totalProcessed = {0};

            final int[] complete = {0};

            final class AuctionBotThread extends BukkitRunnable {

                private final List<Object> auctions;
                private final int index;
                private int processed = 0;

                public AuctionBotThread(int index) {
                    this.auctions = queueForThreads.get(index);
                    this.index = index;
                }

                @Override
                public void run() {
                    for (Object auc : auctions) {
                        JSONObject auction = (JSONObject) auc;

                        String name = (String) auction.get("item_name");
                        String itemID = name.toUpperCase().replaceAll(" ", "_") + ".json";
                        ItemStack neu = Skyblock.getPlugin().getItemHandler().getItem(itemID);

                        if (neu != null) {
                            UUID id = getUUID(auction.get("uuid"));
                            UUID sellerId = getUUID(auction.get("auctioneer"));
                            String seller = uuidToName(sellerId);

                            NBTContainer nbt = new NBTContainer(NBTReflectionUtil.readNBT(new ByteArrayInputStream(Base64.getDecoder().decode(auction.get("item_bytes").toString()))));
                            NBTCompound extraAttributes = nbt.getCompoundList("i")
                                    .get(0).getCompound("tag")
                                    .getCompound("ExtraAttributes");

                            ItemBase base = new ItemBase(neu);

                            if (extraAttributes.hasKey("enchantments")) {
                                NBTCompound enchantments = extraAttributes.getCompound("enchantments");
                                for (String key : enchantments.getKeys()) {
                                    base.setEnchantment(key, enchantments.getInteger(key));
                                }
                            }

                            if (extraAttributes.hasKey("modifier")) {
                                String reforgeString = extraAttributes.getString("modifier").toUpperCase();
                                Reforge reforge = Reforge.valueOf(reforgeString);

                                if (Skyblock.getPlugin().getReforgeHandler().getReforge(reforge) == null)
                                    reforge = Reforge.NONE;

                                if (reforgeString.equalsIgnoreCase("ancient")) reforge = Reforge.FIERCE;
                                if (reforgeString.equalsIgnoreCase("fabled")) reforge = Reforge.SPICY;

                                base.setReforge(reforge);
                            }

                            neu = base.createStack();

                            long startTime = (long) auction.get("start");
                            long endTime = (long) auction.get("end");
                            long start = (long) auction.get("starting_bid");
                            long highest = (long) auction.get("highest_bid_amount");
                            boolean bin = (boolean) auction.get("bin");

                            Auction auctionObject = Skyblock.getPlugin().getAuctionHouse().createFakeAuction(neu, Util.blankPlayer(seller), (highest == 0 ? start : highest), (endTime - startTime) / 50, bin, id);

                            if (auctionObject == null) continue;

                            for (AuctionCategory cat : AuctionCategory.values()) {
                                if (cat.getCanPut().test(auctionObject.getItem())) {
                                    AuctionHouse.CATEGORY_CACHE.get(cat).add(auctionObject);
                                    break;
                                }
                            }

                            AuctionHouse.CATEGORY_CACHE.get(AuctionCategory.ALL).add(auctionObject);

                            JSONArray bids = (JSONArray) auction.get("bids");

                            int i = 0;
                            for (Object b : bids) {
                                if (i >= 2) break;

                                JSONObject bid = (JSONObject) b;

                                UUID bidderId = getUUID(bid.get("bidder"));
                                String bidder = uuidToName(bidderId);
                                long amount = (long) bid.get("amount");
                                long timeStamp = (long) bid.get("timestamp");

                                auctionObject.getBidHistory().add(new AuctionBid(Util.blankPlayer(bidder), auctionObject, (int) amount, timeStamp));
                                i++;
                            }
                        }

                        processed++;
                        totalProcessed[0]++;

                        Skyblock.getPlugin().getAuctionHouse().setBotFinished(false);

                        if (processed == auctions.size()) {
                            skyblock.sendMessage("Finished processing thread " + index + " for the Auction Bot. [Total: " + totalProcessed[0] + 1 + "]");
                            complete[0]++;
                            Skyblock.getPlugin().getAuctionHouse().setBotFinished(true);
                        }

                        if (complete[0] == THREAD_AMOUNT) {
                            skyblock.sendMessage("Finished processing all threads for the Auction Bot page &8" + page + "&f [" + Util.getTimeDifferenceAndColor(start, System.currentTimeMillis()) + "&f]");
                            Skyblock.getPlugin().getAuctionHouse().setBotFinished(true);
                        }
                    }
                }
            }

            for (int i = 0; i < THREAD_AMOUNT; i++) {
                new AuctionBotThread(i).runTaskAsynchronously(Skyblock.getPlugin());
            }
        } catch (IOException ignored) {
        } catch (ParseException ex) {
            Skyblock.getPlugin().sendMessage("&cFailed to initialize Auction Bot: &8" + ex.getMessage());
            return new ArrayList<>();
        }

        return auctions;
    }

    private String uuidToName(UUID u) {
        if (!useRealName || nameType.equalsIgnoreCase("uuid")) return u.toString().substring(0, Util.random(5, 8));

        if (nameType.equalsIgnoreCase("mojang")) {
            String uuid = u.toString();

            try {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int status = conn.getResponseCode();

                if (status != 200) {
                    Skyblock.getPlugin().sendMessage("&cFailed to get username for uuid &8" + uuid);
                    return null;
                }

                JSONParser parser = new JSONParser();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                Object obj = parser.parse(in);

                JSONObject jsonObject = (JSONObject) obj;

                return (String) jsonObject.get("name");
            } catch (Exception ex) {
                return "Unknown";
            }
        } else if (nameType.equals("random")) {
            String firstNamesString = "Bree,Valerie,Lucille,Meadow,Cheryl,Todd,Alesha,Noelle,Brenda,Jaleel,Truman,Jade,Rowan,Menachem,Joanne,Gamer,Kasey,Bret,Kamron,Imari,Ellis,Phillip,Ingrid,Frank,Leroy,Vicente,Octavio,Kyree,Camren,Ariel,Nathalie,Alli,Brycen,Dion,Kristina,Gabriel,Micheal,Jodi,Brynne,Gretchen,Jalin,Rocky,Jairo,Dorothy,Abel,Latavia,Mercedez,Miya,Devante,Megan,Norma,Tara,Armani,Tristian,Taniya,Francisco,Makaylah,Alexandre,Bridget,Cassie,Stone,Tori,Cale,Kaia,Malik,Desean,Aisha,Willis,Ayesha,Tucker,Gladys,Raul,Chastity,Benton,Lisette,Roy,Jonas,Talia,Brenton,Chaya,Heriberto,Kalista,Kacey,Stephen,Karina,Ashlee,Trevin,Genesis,Caelan,Tate,Jeremiah,Wayne,Bennett,Maeve,Shayne,Garrison,Brenna,Alanis,Halley,Lizet,Immanuel";
            String lastNamesString = "Agee,Richard,Webb,Rose,Hinojosa,Ott,Healy,Merrick,Driver,Sorensen,Killian,Tracy,Handley,Ruiz,Fierro,Layton,Albert,Akin,Dove,Peacock,Meade,Game,Carrington,Hermann,Gilliam,Jimenez,Rincon,Regan,Rizzo,Shuler,Lawler,Wiseman,Lomeli,Stowe,Harmon,Gamble,Wofford,Pryor,Barber,McIntosh,Martz,Hobson,Lund,Barbosa,Homan,Ware,Chacon,Fish,Lopes,Elias,Marquis,Peyton,Stanfield,Hackney,Gable,Duarte,Dowdy,Ashford,Gustafson,Ellsworth,Shifflett,Kozlowski,Hyatt,Lennon,Botello,Breedlove,Perales,Reinhardt,Borders,Cleary,Forrester,Wolff,Dyson,Motley,Andrus,Worden,Frias,Needham,Watters,Madrid,Mackey,Marrero,Holley,Randolph,Barragan,Adler,Laws,Black,Quinlan,Moffett,Gale,Demers,Dietrich,Weems,Swenson,Knapp,McClure,Worley,Hanes,Monk,Hedges";

            List<String> firstNames = Arrays.stream(firstNamesString.split(",")).map(String::toLowerCase).collect(Collectors.toList());
            List<String> lastNames = Arrays.stream(lastNamesString.split(",")).map(String::toLowerCase).collect(Collectors.toList());
            List<String> years = Arrays.asList("1970,1971,1972,1973,1974,1975,1976,1977,1978,1979,1980,1981,1982,1983,1984,1985,1986,1987,1988,1989,1990,1991,1992,1993,1994,1995,1996,1997,1998,1999,2000,2001,2002,2003,2004,2005,2006,2007,2008,2009,2010,2011,2012,2013,2014,2015,2016,2017,2018,2019,2020,2021,2022".split(","));

            boolean usesUnderscore = Util.random(0, 1) == 0;
            boolean usesNumber = Util.random(0, 1) == 0;

            return firstNames.get(Util.random(0, firstNames.size() - 1)) + (usesUnderscore ? "_" : "") + lastNames.get(Util.random(0, lastNames.size() - 1)) + (usesNumber ? (usesUnderscore ? "_" : "") + years.get(Util.random(0, years.size() - 1)) : "");
        }

        return "Unknown";
    }

    private UUID getUUID(Object o) {
        return getUUID((String) o);
    }

    private UUID getUUID(String s) {
        return UUID.fromString(s.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }
}
