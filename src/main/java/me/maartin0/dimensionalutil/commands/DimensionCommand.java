package me.maartin0.dimensionalutil.commands;

import me.maartin0.dimensionalutil.util.Command;
import me.maartin0.dimensionalutil.util.Logger;
import me.maartin0.dimensionalutil.util.DimensionalUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DimensionCommand extends Command {
    final static List<String> defaultWorlds = List.of("world", "world_nether", "world_the_end");
    void teleportPlayerToWorld(Player player, World world) {
        Logger.sendPlayerMessage(player, "Teleporting you to world: '" + world.getName() + "'");
        if (player.getGameMode() == GameMode.CREATIVE) player.setFlying(true);
        player.teleport(world.getSpawnLocation());
    }
    void commandCreateWorld(Player player, String[] args) {
        Logger.sendPlayerMessage(player, "Loading...");
        DimensionalUtil.createWorld(args[1],
            (World world) -> {
                teleportPlayerToWorld(player, world);
            }, () -> {
                Logger.sendPlayerMessage(player, "A world with that name already exists!");
            });
    }
    void commandJoinWorld(Player player, String[] args) {
        DimensionalUtil.getWorld(args[1], (World world) -> {
            teleportPlayerToWorld(player, world);
        }, () -> {
            Logger.sendPlayerMessage(player, "Unable to find a world with that name!");
        });
    }
    void commandDeleteWorld(Player player, String[] args) {
        DimensionalUtil.getWorld(args[1], (World world) -> {
            DimensionalUtil.deleteWorld(world, () -> {
                Logger.sendPlayerMessage(player, "Success");
            }, () -> {
                Logger.sendPlayerGenericErrorMessage(player);
            });
        }, () -> {
            Logger.sendPlayerMessage(player, "Unable to find a world with that name!");
        });
    }
    void commandCopyWorld(Player player, String[] args) {
        if (args.length != 3) {
            Logger.sendPlayerMessage(player, "Invalid usage!");
            return;
        }
        DimensionalUtil.getWorld(args[1], (World world) -> {
            Logger.sendPlayerMessage(player, "Loading...");
            DimensionalUtil.copyWorld(world, args[2], (World copy) -> {
                teleportPlayerToWorld(player, copy);
                Logger.sendPlayerMessage(player, "Woosh!");
            }, () -> {
                Logger.sendPlayerMessage(player, "A world with that name already exists!");
            });
        }, () -> {
            Logger.sendPlayerMessage(player, "Unable to find a world with that name!");
        });
    }
    @Override
    public boolean onCommand(@NotNull Player player, @NotNull String commandName, @NotNull String[] args) {
        if (args[0].length() < 2) return false;
        else if (args[0].equals("create")) commandCreateWorld(player, args);
        else if (args[0].equals("join")) commandJoinWorld(player, args);
        else if (args[0].equals("delete")) commandDeleteWorld(player, args);
        else if (args[0].equals("copy")) commandCopyWorld(player, args);
        else return false;
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String commandName, @NotNull String[] args) {
        if (args.length < 2) return List.of("create", "join", "delete", "copy");
        else if (args.length == 2) {
            switch (args[0]) {
                case "create":
                    return List.of("<name>");
                case "join":
                case "copy":
                    return Bukkit.getWorlds()
                            .stream()
                            .map(World::getName)
                            .toList();
                case "delete":
                    return Bukkit.getWorlds()
                            .stream()
                            .map(World::getName)
                            .filter((String name) -> !(defaultWorlds.contains(name)))
                            .toList();
            }
        } else if (args.length == 3 && args[0].equals("delete")) return List.of("<name>");
        return List.of();
    }
}
