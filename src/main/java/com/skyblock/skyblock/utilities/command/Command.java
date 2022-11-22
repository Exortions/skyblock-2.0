package com.skyblock.skyblock.utilities.command;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.utilities.command.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Command {

    default String[] aliases() {
        return getClass().getAnnotation(Alias.class) == null ? new String[0] : getClass().getAnnotation(Alias.class).aliases();
    }

    default boolean requiresPlayer() {
        return getClass().getAnnotation(RequiresPlayer.class) != null;
    }

    default org.bukkit.permissions.Permission permission() {
        Permission annotation = this.getClass().getAnnotation(Permission.class);

        return new org.bukkit.permissions.Permission(annotation == null ? "skyblock.*" : annotation.permission());
    }

    default String description() {
        Description annotation = this.getClass().getAnnotation(Description.class);

        return annotation == null ? "No description provided." : annotation.description();
    }

    default String usage() {
        Usage annotation = this.getClass().getAnnotation(Usage.class);

        return annotation == null ? "No usage provided." : annotation.usage();
    }

    default String name() {
        return this.getClass().getSimpleName().split("Command")[0];
    }

    default void execute(CommandSender sender, String[] args, Skyblock plugin) {}
    default void execute(Player player, String[] args, Skyblock plugin) {}

}
