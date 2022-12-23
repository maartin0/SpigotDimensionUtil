package me.maartin0.dimensionalutil;

import me.maartin0.dimensionalutil.commands.DimensionCommand;
import me.maartin0.dimensionalutil.util.DimensionalUtil;
import me.maartin0.dimensionalutil.util.Logger;
import me.maartin0.dimensionalutil.util.VoidChunkGenerator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Main extends JavaPlugin {
    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;
        registerCommands();
        DimensionalUtil.loadAll();
        Logger.logInfo("Enabled!");
    }

    void registerCommands() {
        new DimensionCommand().register("dimension");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        return new VoidChunkGenerator();
    }
}
