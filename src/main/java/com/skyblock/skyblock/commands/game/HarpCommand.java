package com.skyblock.skyblock.commands.game;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import org.bukkit.*;
import org.bukkit.Note.Tone;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RequiresPlayer
@Usage(usage = "/sb harp")
@Description(description = "Opens the Harp Gui")
public class HarpCommand implements Command, Listener {
    List<Song> songs = new ArrayList<>();

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
    
    private class Song {
        public final String name;
        public final String path;
        public final HashMap<Integer, List<Integer>> notes;
        public final Difficulty difficulty;

        protected Song(String name, String path, HarpCommand.Difficulty difficulty, HashMap<Integer, List<Integer>> song) {
            this.name = name;
            this.path = path;
            this.difficulty = difficulty;
            this.notes = song;
            
            //HashMap<Integer, List<Integer>> toNotes = new HashMap<>();
            //for (int i = 0; i < 5; ++i) {
            //    toNotes.put()
            //}                       start delay here
       }

        public ItemStack getSelectorItem(int best, boolean unlocked, boolean visible) {            
            String difficultyString = null;
            int rewardIntelligence = 0;
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
            }
            List<String> lore = new ArrayList<>();

            if (unlocked) {
                lore.addAll(Arrays.asList(
                    ChatColor.DARK_GRAY + difficultyString,
                    ChatColor.GRAY + "Best: " + formatScore(best)
                ));
            }
            
            lore.addAll(Arrays.asList(
                ChatColor.GRAY + "Rewards:",
                ChatColor.AQUA + "+" + rewardIntelligence + " Intelligence"
            ));

            if (best != 100 && unlocked) {
                lore.add(ChatColor.LIGHT_PURPLE + "Get to 100% to earn reward!");
            }

            ItemBuilder item = new ItemBuilder(unlocked ? Material.BOOK : Material.NOTE_BLOCK);
            if (visible && !unlocked) {
                item.setDisplayName(ChatColor.RED + name);
                lore.add(ChatColor.RED + "Score 90% on previous!");
            }
            else item.setDisplayName(unlocked ? ChatColor.RESET +name : ChatColor.RED + "Mystery Song");
            
            item.setLore(lore);
            return item.toItemStack();
        }
    }

    public HarpCommand() {
        HashMap<Integer, List<Integer>> hymnToTheJoy = new HashMap<>();
        hymnToTheJoy.put(0, Arrays.asList(2));
        hymnToTheJoy.put(1, Arrays.asList());
        hymnToTheJoy.put(2, Arrays.asList(2));
        hymnToTheJoy.put(3, Arrays.asList());
        hymnToTheJoy.put(4, Arrays.asList(3));
        hymnToTheJoy.put(5, Arrays.asList());
        hymnToTheJoy.put(6, Arrays.asList(4));
        hymnToTheJoy.put(7, Arrays.asList(0));
        hymnToTheJoy.put(8, Arrays.asList(4));
        hymnToTheJoy.put(9, Arrays.asList());
        hymnToTheJoy.put(10, Arrays.asList(3));
        hymnToTheJoy.put(11, Arrays.asList());
        hymnToTheJoy.put(12, Arrays.asList(2));
        hymnToTheJoy.put(13, Arrays.asList());
        hymnToTheJoy.put(14, Arrays.asList(1));
        hymnToTheJoy.put(15, Arrays.asList());
        hymnToTheJoy.put(16, Arrays.asList(0));
        hymnToTheJoy.put(17, Arrays.asList(0));
        hymnToTheJoy.put(19, Arrays.asList());
        hymnToTheJoy.put(18, Arrays.asList(1));
        hymnToTheJoy.put(20, Arrays.asList());
        hymnToTheJoy.put(21, Arrays.asList(2));
        hymnToTheJoy.put(22, Arrays.asList());
        hymnToTheJoy.put(23, Arrays.asList(2));
        hymnToTheJoy.put(24, Arrays.asList());
        hymnToTheJoy.put(25, Arrays.asList());
        hymnToTheJoy.put(26, Arrays.asList(1));
        hymnToTheJoy.put(27, Arrays.asList(1));
        hymnToTheJoy.put(28, Arrays.asList());

        songs.add(new Song("Hymn to the Joy", "hymn_to_the_joy", Difficulty.EASY, hymnToTheJoy));
        songs.add(new Song("Frère Jacques", "frere_jaques", Difficulty.EASY, null));
        songs.add(new Song("Amazing Grace", "amazing_grace", Difficulty.EASY, null));
        songs.add(new Song("Brahm's lullaby", "brahms_lullaby", Difficulty.HARD, null));
        songs.add(new Song("Happy Birthday To You", "happy_birthday_to_you", Difficulty.HARD, null));
        songs.add(new Song("Greensleeves", "greensleeves", Difficulty.HARD, null));
        songs.add(new Song("Geothermy?", "geothermy", Difficulty.EXPERT, null));
        songs.add(new Song("Minuet", "minuet", Difficulty.EXPERT, null));
        songs.add(new Song("Joy To The World", "joy_to_the_world", Difficulty.EXPERT, null));
        songs.add(new Song("Godly Imagination", "godly_imagination", Difficulty.VIRTUOSO, null));
        songs.add(new Song("La Vie En Rose", "la_vie_en_rose", Difficulty.VIRTUOSO, null));

        Bukkit.getPluginManager().registerEvents(this, Skyblock.getPlugin(Skyblock.class));
    }

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        SkyblockPlayer sbp = SkyblockPlayer.getPlayer(player);

        Inventory out = Bukkit.createInventory(null, 45, "Melody");
        Util.fillEmpty(out);

        for (int i = 0; i < songs.size(); ++i) {
            Double y = Math.floor(i / 7);
            int best = (int) sbp.getValue("harp." + songs.get(i).path + ".best");
            player.sendMessage(best + "");
            boolean unlocked = i == 0 || best >= 90;
            boolean visible = i == 0 || best != -1;
            out.setItem(10 + y.intValue() * 2 + i, songs.get(i).getSelectorItem(best, unlocked, visible));
        }

        player.openInventory(out);
    }

    public void harp(SkyblockPlayer player, Song song) {
        int notes = 0;
        for (List<Integer> values : song.notes.values()) {
            notes += values.size();
        }
        player.setExtraData("harpNotes", notes);
        player.setExtraData("harpClicks", 0);

        Player bPlayer = player.getBukkitPlayer();

        new BukkitRunnable() {
            int tick = 10;
            int time = 0;
            @Override
            public void run() {
                if (tick == 10) {
                    tick = 0;
                    if (!song.notes.containsKey(time)) {
                        cancel();
                        int score = Double.valueOf(((Double.valueOf((int) player.getExtraData("harpClicks")) / (int) player.getExtraData("harpNotes")) * 100)).intValue();
                        bPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "[Harp] " + ChatColor.GREEN + "Song Completed!");
                        bPlayer.sendMessage(ChatColor.LIGHT_PURPLE + "[Harp] " + ChatColor.WHITE + "You Scored " + formatScore(score));
                        player.setValue("harp." + song.path + ".score", score);
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
            for (int relativeNoteTime = 5; relativeNoteTime > -2; --relativeNoteTime) { 
                for (int note = 0; note < 7; ++note) { // C D E F G A B
                    boolean hasNote = song.notes.containsKey(time + relativeNoteTime) && song.notes.get(time + relativeNoteTime).contains(note);
                    ItemStack icon;
                    if (relativeNoteTime != 1)
                        icon = new ItemStack(hasNote ? Material.WOOL : Material.STAINED_GLASS_PANE);
                    else
                        icon = new ItemStack(Material.STAINED_CLAY); //TODO: set name

                    ItemBuilder item = new ItemBuilder(icon);
                    if (!hasNote)
                        item.setDisplayName((ChatColor) getNoteColor(note)[1] + "|" + ChatColor.GRAY + " Click!");
                    else
                        item.setDisplayName((ChatColor) getNoteColor(note)[1] + "| Click!");

                    icon = item.toItemStack();

                    if (!(relativeNoteTime == 1 && hasNote))
                        icon.setDurability((short) getNoteColor(note)[0]);

                    if (y < 6)
                        out.setItem(1 + note + y * 9, icon);
                    else
                        out.setItem(1 + note, icon);
                }
                ++y;
            }
        }
        player.openInventory(out);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        SkyblockPlayer player = SkyblockPlayer.getPlayer((Player) event.getWhoClicked());

        if (event.getInventory().getTitle().startsWith("Harp -")) {
            event.setCancelled(true);
                ItemStack i = event.getCurrentItem();
                String n = i.getItemMeta().getDisplayName();
                if (i != null && i.getDurability() == 0) {
                player.setExtraData("harpClicks", (int) player.getExtraData("harpClicks") + 1);
                Tone note = null;
                if (n.contains(ChatColor.LIGHT_PURPLE + "")) note = Tone.C;
                if (n.contains(ChatColor.YELLOW + "")) note = Tone.D;
                if (n.contains(ChatColor.GREEN + "")) note = Tone.E;
                if (n.contains(ChatColor.DARK_GREEN + "")) note = Tone.F;
                if (n.contains(ChatColor.DARK_PURPLE + "")) note = Tone.G;
                if (n.contains(ChatColor.BLUE + "")) note = Tone.A;
                if (n.contains(ChatColor.AQUA + "")) note = Tone.B;
                player.getBukkitPlayer().playNote(player.getBukkitPlayer().getLocation(), Instrument.PIANO, Note.natural(1, note));
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
}
