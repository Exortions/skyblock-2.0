package com.skyblock.skyblock.commands.game;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.enums.SkyblockStat;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.*;
import org.bukkit.Note.Tone;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RequiresPlayer
@Usage(usage = "/sb harp")
@Description(description = "Opens the Harp Gui")
public class HarpCommand implements Command, Listener {
    private enum Difficulty {
        EASY,
        HARD,
        EXPERT,
        VIRTUOSO;
    }

    protected String formatScore(int score) {
        String bestColor = "" + ChatColor.RED;
        if (score == -1) return  "" + ChatColor.DARK_GRAY + "N/A";
        if (score > 0) bestColor = "" + ChatColor.YELLOW;
        if (score == 100) bestColor = "" + ChatColor.GREEN + ChatColor.BOLD;
        return bestColor + score + "%" + (score == 100 ? "!" : "");
    }

    protected Object[] getNoteColor(int note) {
        short damage = 0;
        ChatColor color = null;
        switch (note) {
            case 0:
                damage = 6; //pink
                color = ChatColor.LIGHT_PURPLE;
                break;
            case 1:
                damage = 4; // yellow
                color = ChatColor.YELLOW;
                break;
            case 2:
                damage = 5; // lime
                color = ChatColor.GREEN;
                break;
            case 3:
                damage = 13; //green
                color = ChatColor.DARK_GREEN;
                break;
            case 4:
                damage = 10; // purple
                color = ChatColor.DARK_PURPLE;
                break;
            case 5:
                damage = 11; //blue
                color = ChatColor.BLUE;
                break;
            case 6:
                damage = 3; //light blue
                color = ChatColor.AQUA;
                break;
        }
        return new Object[] {damage, color};
    }

    List<Song> songs = new ArrayList<>();
    private class Song {
        public final String name;
        public final String path;
        public final int delay;
        public final String unlockedBy;
        public final ArrayList<Integer> notes;
        private String difficultyString;

        public final int rewardIntelligence;
        public final String rewardItem;

        protected Song(String name, String path, HarpCommand.Difficulty difficulty, int delay, String unlockedBy, String rewardItem, ArrayList<Integer> song) {
            this.name = name;
            this.path = path;
            this.delay = delay;
            this.unlockedBy = unlockedBy;
            this.notes = song;
             switch (difficulty) {
                case EASY:
                    difficultyString = "Easy Song";
                    rewardIntelligence = 1;
                    break;
                case HARD:
                    difficultyString = "Hard Song";
                    rewardIntelligence = 2;
                    break;
                case EXPERT:
                    difficultyString = "Expert Song";
                    rewardIntelligence = 3;
                    break;
                case VIRTUOSO:
                    difficultyString = "Virtuoso Song";
                    rewardIntelligence = 4;
                    break;
                default:
                    difficultyString = null;
                    rewardIntelligence = 0;
                    break;
            }
            this.rewardItem = rewardItem;
       }

        public ItemStack getSelectorItem(SkyblockPlayer player) {
            int unlockerBest;
            if (unlockedBy == null) unlockerBest = 100;
            else unlockerBest = (int) player.getValue("harp." + unlockedBy + ".best");
            boolean unlocked = unlockerBest >= 90;
            int thisBest = player.getIntValue("harp." + path + ".best");

            List<String> lore = new ArrayList<>();
            lore.addAll(Arrays.asList(ChatColor.DARK_GRAY + difficultyString));

            if (unlocked) {
                lore.addAll(Arrays.asList(ChatColor.GRAY + "Best: " + formatScore(thisBest)));
            }
            
            lore.addAll(Arrays.asList(
                ChatColor.GRAY + "Rewards:",
                ChatColor.AQUA + "+" + rewardIntelligence + " Intelligence"
            ));

            if (thisBest != 100 && unlocked) {
                lore.add(ChatColor.LIGHT_PURPLE + "Get to 100% to earn reward!");
            }

            ItemBuilder item = new ItemBuilder(unlocked ? Material.BOOK : Material.NOTE_BLOCK);
            if (unlockerBest != -1 && !unlocked) {
                item.setDisplayName(ChatColor.RED + name);
                lore.add(ChatColor.RED + "Score 90% on previous!");
            }
            else item.setDisplayName(unlocked ? ChatColor.RESET +name : ChatColor.RED + "Mystery Song");
            
            item.setLore(lore);
            return item.toItemStack();
        }
    }

    public HarpCommand() {
        try {
            JSONParser parser = new JSONParser();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(new File(Skyblock.getPlugin().getDataFolder() + File.separator + "harpSongs.json").toPath()), StandardCharsets.UTF_8));
            Object obj = parser.parse(bufferedReader);

            JSONArray songList = (JSONArray) ((JSONObject) obj).get("songs");
            for (Object songObject : songList) {
                JSONObject rawSong = (JSONObject) songObject;
                
                String name = (String) rawSong.get("name");
                String unlockedBy = (String) rawSong.get("unlockedBy");
                String path = (String) rawSong.get("path");
                Difficulty difficulty = Difficulty.valueOf((String) rawSong.get("difficulty"));
                int delay = ((Long) rawSong.get("delay")).intValue();
                JSONArray jsonNotes = (JSONArray) rawSong.get("notes");
                
                ArrayList<Integer> notes = new ArrayList<>();
                for (int i = 0; i < 6; ++i) {
                    notes.add(-1);
                }
                for (Object rawNote : jsonNotes) {
                    if (rawNote == null) notes.add(-1);
                    else notes.add(((Long) rawNote).intValue());
                }
                JSONObject rewards = (JSONObject) rawSong.get("rewards");
                String item = (String) rewards.get("item");

                this.songs.add(new Song(name, path, difficulty, delay, unlockedBy, item, notes));
            }
        } catch (ParseException | IOException ex) {
            ex.printStackTrace();
        }
        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin(Skyblock.class));
    }

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        SkyblockPlayer sbp = SkyblockPlayer.getPlayer(player);

        Inventory out = Bukkit.createInventory(null, 45, "Melody");
        Util.fillEmpty(out);

        for (int i = 0; i < songs.size(); ++i) {
            Double y = Math.floor(i / 7);
            out.setItem(10 + y.intValue() * 2 + i, songs.get(i).getSelectorItem(sbp));
        }

        player.openInventory(out);
    }

    public void harp(SkyblockPlayer player, Song song) {
        int notes = 0;
        for (int note : song.notes) {
            if (note != -1) ++notes;
        }
        player.setExtraData("harpNotes", notes);
        player.setExtraData("harpClicks", 0);
        player.setExtraData("harpCancel", null);

        Player bPlayer = player.getBukkitPlayer();
        bPlayer.openInventory(Bukkit.createInventory(null, 54, "Harp - " + song.name));

        new BukkitRunnable() {
            int tick = song.delay;
            int time = 0;
            @Override
            public void run() {
                if (tick == song.delay) {
                    tick = 0;
                    if (time >= song.notes.size()) {
                        cancel();
                        bPlayer.closeInventory();
                        int score = Double.valueOf(((Double.valueOf((int) player.getExtraData("harpClicks")) / (int) player.getExtraData("harpNotes")) * 100)).intValue();
                        bPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "[Harp] " + ChatColor.GREEN + "Song Completed!");
                        bPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "[Harp] "
                                            + ChatColor.WHITE + "You Scored "
                                            + formatScore(score)
                                            + ChatColor.WHITE + " on "
                                            + ChatColor.GREEN +  song.name);
                        if (score > player.getIntValue("harp." + song.path + ".best")) {
                            player.setValue("harp." + song.path + ".best", score);
                            if (score == 100) {
                                player.addStat(SkyblockStat.MANA, song.rewardIntelligence);
                                bPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "[Harp] "
                                                    + ChatColor.WHITE + "Earned " + ChatColor.AQUA
                                                    + "+" + song.rewardIntelligence + " Intelligence"
                                                    + ChatColor.WHITE + " from mastering a new Song!");
                                ItemStack rewardItemStack = Skyblock.getPlugin().getItemHandler().getItem(song.rewardItem);
                                if (rewardItemStack != null)
                                    bPlayer.getInventory().addItem(rewardItemStack);
                            }
                        }
                        return;
                    }
                    else if (player.hasExtraData("harpCancel")) {
                        cancel();
                        bPlayer.closeInventory();
                        player.setExtraData("harpCancel", null);
                        bPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "[Harp] " + ChatColor.RED + "Song cancelled!");
                        bPlayer.playSound(bPlayer.getLocation(), Sound.CHICKEN_IDLE, 1, 1);
                        return;
                    }
                    paintHarp(bPlayer, song, time++);
                }
                ++tick;
            }
        }.runTaskTimer(Skyblock.getPlugin(Skyblock.class), 0, 1); 
    }
    
    public void paintHarp(Player player, Song song, int time) {
        Inventory out = Bukkit.createInventory(null, 54, "Harp - " + song.name);
        Util.fillEmpty(out);

        for (int noteList = time; noteList < song.notes.size(); ++noteList) { // T[C, D, E, F, G, A, B]
            int y = 0;
            for (int relativeNoteTime = 4; relativeNoteTime > -2; --relativeNoteTime) {
                for (int note = 0; note < 7; ++note) { // C D E F G A B
                    boolean hasNote = song.notes.size() > time + relativeNoteTime
                        && time + relativeNoteTime >= 0
                        && song.notes.get(time + relativeNoteTime).toString().equals(Integer.toString(note));
                    ItemStack icon;
                    if (relativeNoteTime != 0)
                        icon = new ItemStack(hasNote ? Material.WOOL : Material.STAINED_GLASS_PANE);
                    else
                        icon = new ItemStack(Material.STAINED_CLAY); //TODO: set name

                    ItemMeta meta = icon.getItemMeta();
                    if (!hasNote)
                        meta.setDisplayName((ChatColor) getNoteColor(note)[1] + "|" + ChatColor.GRAY + " Click!");
                    else
                        meta.setDisplayName((ChatColor) getNoteColor(note)[1] + "| Click!");

                    icon.setItemMeta(meta);

                    if (!(relativeNoteTime == 0 && hasNote))
                        icon.setDurability((short) getNoteColor(note)[0]);

                    out.setItem(1 + note + y * 9, icon);
                }
                ++y;
            }
        }
        player.getOpenInventory().getTopInventory().setContents(out.getContents());
        // player.openInventory(out);
    }

    @EventHandler
    public void onHarpClick(InventoryClickEvent event) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getWhoClicked());

        if (event.getInventory().getTitle().startsWith("Harp -")) {
            event.setCancelled(true);
            ItemStack i = event.getCurrentItem();
            String n = i.getItemMeta().getDisplayName();
            if (i != null && i.getDurability() == 0) {
                Note note = null;
                if (n.contains(ChatColor.LIGHT_PURPLE + "")) note = Note.natural(0, Tone.C);
                else if (n.contains(ChatColor.YELLOW + "")) note = Note.natural(0, Tone.D);
                else if (n.contains(ChatColor.GREEN + "")) note = Note.natural(0, Tone.E);
                else if (n.contains(ChatColor.DARK_GREEN + "")) note = Note.natural(0, Tone.F);
                else if (n.contains(ChatColor.DARK_PURPLE + "")) note = Note.natural(1, Tone.G);
                else if (n.contains(ChatColor.BLUE + "")) note = Note.natural(1, Tone.A);
                else if (n.contains(ChatColor.AQUA + "")) note = Note.natural(1, Tone.B);
                else return;

                ItemMeta meta = i.getItemMeta();
                meta.setDisplayName(ChatColor.GRAY + "| Click!");
                i.setItemMeta(meta);
                
                player.setExtraData("harpClicks", (int) player.getExtraData("harpClicks") + 1);
                player.getBukkitPlayer().playNote(player.getBukkitPlayer().getLocation(), Instrument.PIANO, note);
            }
            else {
                int harpClicks = (int) player.getExtraData("harpClicks");
                if (harpClicks > 0) player.setExtraData("harpClicks", harpClicks - 1);
            }
        }
        else if (event.getInventory().getTitle().equals("Melody")) {
            event.setCancelled(true);
                String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
                for (Song s : songs) {
                if (itemName.contains(s.name) && !itemName.contains(ChatColor.RED + "")) {
                    harp(player, s);
                }
            }
        }
    }

    @EventHandler
    public void onHarpClose(InventoryCloseEvent event) {
        if (event.getInventory().getTitle().startsWith("Harp -")) {
            SkyblockPlayer.getPlayer((Player) event.getPlayer()).setExtraData("harpCancel", true);
        }
    }
}
