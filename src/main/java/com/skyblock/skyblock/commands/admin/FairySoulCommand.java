package com.skyblock.skyblock.commands.admin;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.utilities.command.Command;
import com.skyblock.skyblock.utilities.command.annotations.Description;
import com.skyblock.skyblock.utilities.command.annotations.Permission;
import com.skyblock.skyblock.utilities.command.annotations.RequiresPlayer;
import com.skyblock.skyblock.utilities.command.annotations.Usage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiresPlayer
@Permission(permission = "skyblock.admin")
@Usage(usage = "/sb fairysoul set/add/remove/claim amount")
@Description(description = "Modify player fairy souls")
public class FairySoulCommand implements Command {

    @Override
    public void execute(Player player, String[] args, Skyblock plugin) {
        if (args.length < 2)  { sendUsage(player); return; }

        SkyblockPlayer sb = SkyblockPlayer.getPlayer(player);
        List<Location> souls = new ArrayList<>();
        int amount = Integer.parseInt(args[1]);

        switch (args[0]) {
            case "set":
                for (int i = 0; i < amount; i++) souls.add(new Location(Skyblock.getSkyblockWorld(), 0, 100, 0));
                sb.setValue("fairySouls.found", souls);
                break;
            case "add":
                souls = (List<Location>) sb.getValue("fairySouls.found");
                for (int i = 0; i < amount; i++) souls.add(new Location(Skyblock.getSkyblockWorld(), 0, 100, 0));
                sb.setValue("fairySouls.found", souls);
                break;
            case "remove":
                souls = (List<Location>) sb.getValue("fairySouls.found");
                for (int i = 0; i < amount; i++) souls.remove(souls.size() - 1);
                sb.setValue("fairySouls.found", souls);
                break;
            case "claim":
                for (int i = 0; i < amount; i++) Skyblock.getPlugin().getFairySoulHandler().claim(player);
                break;
        }

        player.sendMessage(ChatColor.GRAY + "Successfully modified fairy souls.");
    }
}
