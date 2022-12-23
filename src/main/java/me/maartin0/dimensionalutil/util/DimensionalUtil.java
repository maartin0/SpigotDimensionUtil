package me.maartin0.dimensionalutil.util;

import me.maartin0.dimensionalutil.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.function.Consumer;

public class DimensionalUtil {
    final static Consumer<World> blankConsumer = (World world) -> {};
    final static Runnable blankRunnable = () -> {};
    static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, runnable);
    }
    static void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(Main.plugin, runnable);
    }
    public static void createWorld(String name, Consumer<World> success, Runnable failure) {
        if (Bukkit.getWorld(name) != null) {
            failure.run();
            return;
        }
        runAsync(() -> {
            WorldCreator worldCreator = new WorldCreator(name);
            worldCreator.generator(new VoidChunkGenerator());
            worldCreator.environment(World.Environment.NORMAL);
            worldCreator.biomeProvider(new VoidChunkGenerator.PlainsBiomeProvider());
            worldCreator.generateStructures(false);
            worldCreator.hardcore(false);
            runSync(() -> success.accept(worldCreator.createWorld()));
        });
    }
    public static void deleteWorld(World world, Runnable success, Runnable failure) {
        world.getPlayers().forEach(p -> p.kickPlayer("The world you were just in was deleted"));
        Bukkit.unloadWorld(world, false);
        runAsync(() -> {
            try {
                deleteFolder(world.getWorldFolder().toPath());
            } catch (IOException e) {
                runSync(failure);
                return;
            }
            runSync(success);
        });
    }
    public static void copyWorld(World prototype, String name, Consumer<World> success, Runnable failure) {
        if (Bukkit.getWorld(name) != null) {
            failure.run();
            return;
        }
        runAsync(() -> {
            File prototypeFolder = prototype.getWorldFolder();
            File copyFolder = new File(prototypeFolder.getParentFile(), name);
            try {
                if (copyFolder.exists()) {
                    World old = Bukkit.getWorld(name);
                    if (old != null) Bukkit.unloadWorld(old, false);
                    deleteFolder(copyFolder.toPath());
                }
                copyFolder(prototypeFolder.toPath(), copyFolder.toPath());
            } catch (IOException e) {
                runSync(failure);
                return;
            }
            getWorld(name, success, failure);
        });
    }
    public static void getWorld(String name) {
        getWorld(name, blankConsumer, blankRunnable);
    }
    public static void getWorld(String name, Consumer<World> success, Runnable failure) {
        World world = Bukkit.getWorld(name);
        if (world != null) {
            success.accept(world);
            return;
        }
        runAsync(() -> {
            File[] possibleWorlds = Bukkit.getWorldContainer().listFiles((dir, name1) -> name1.equals(name));
            if (possibleWorlds == null || possibleWorlds.length == 0) {
                runSync(failure);
                return;
            }
            runSync(() -> success.accept(new WorldCreator(name).generator(new VoidChunkGenerator()).createWorld()));
        });
    }
    static void copyFolder(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!file.toString().contains("uid.dat"))
                    Files.copy(file, target.resolve(source.relativize(file)));
                return FileVisitResult.CONTINUE;
            }
        });
    }
    static void deleteFolder(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path file, IOException exc) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    public static void loadAll() {
        File[] worlds = Bukkit.getWorldContainer().listFiles(((dir, name) -> name.startsWith("world_")));
        if (worlds == null || worlds.length == 0) return;
        Arrays.stream(worlds).map(File::getName).forEach(DimensionalUtil::getWorld);
    }
}
