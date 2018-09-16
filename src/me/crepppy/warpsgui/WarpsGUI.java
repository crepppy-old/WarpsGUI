package me.crepppy.warpsgui;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WarpsGUI extends JavaPlugin {
    static HashMap<OfflinePlayer, Integer> playerWarps = new HashMap<>();
    static HashMap<String, Integer> warps = new HashMap<>();
    static HashMap<String, List<UUID>> uniqueVisits = new HashMap<>();
    static HashMap<String, UUID> warpOwner = new HashMap<>();
    private static WarpsGUI instance;
    private Essentials essentials;
    private FileConfiguration playerData;
    private File playerDataFile;
    
    static WarpsGUI getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveDefaultConfig();
        saveConfig();
        playerDataFile = new File(getDataFolder(), "playerData.yml");
        if(!playerDataFile.exists()) {
            try {
                if(!playerDataFile.getParentFile().exists())
                    playerDataFile.getParentFile().mkdirs();
                playerDataFile.createNewFile();
            } catch(IOException e) {
                Bukkit.getServer().getLogger().severe("playerData.yml was unable to be created!");
            }
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        savePlayerData();
        Bukkit.getPluginCommand("warps").setExecutor(new WarpCommand());
        Bukkit.getPluginManager().registerEvents(new WarpListener(), this);
        Bukkit.getPluginManager().registerEvents(new InvListener(), this);
        Bukkit.getPluginManager().registerEvents(new SetWarpListener(), this);
        for(String s : getConfig().getStringList("warps")) {
            warps.put(s.split(":")[0], Integer.parseInt(s.split(":")[1]));
        }
        for(String s : getConfig().getStringList("playerWarps")) {
            playerWarps.put(Bukkit.getOfflinePlayer(UUID.fromString(s.split(":")[0])), Integer.parseInt(s.split(":")[1]));
        }
        for(String s : playerData.getKeys(false)) {
            List<String> stringList = playerData.getStringList(s + ".visitors");
            List<UUID> uuid = new ArrayList<>();
            warpOwner.put(s, UUID.fromString(playerData.getString(s + ".owner")));
            for(String string : stringList)
                uuid.add(UUID.fromString(string));
            uniqueVisits.put(s, uuid);
        }
        getLogger().info(warpOwner.toString());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, this::makeEssentials, 20 * 6);
    }
    
    @Override
    public void onDisable() {
        reloadConfig();
        List<String> stringList = new ArrayList<>();
        if(!warps.isEmpty())
            warps.forEach((k, v) -> stringList.add(k + ":" + v));
        getConfig().set("warps", stringList);
        List<String> playerWarpsList = new ArrayList<>();
        if(!playerWarps.isEmpty())
            playerWarps.forEach((k, v) -> playerWarpsList.add(k.getUniqueId() + ":" + v));
        getConfig().set("playerWarps", playerWarpsList);
        for(String s : warpOwner.keySet()) {
            playerData.set(s + ".owner", warpOwner.get(s).toString());
        }
        for (String s : uniqueVisits.keySet()) {
            List<String> uuid = new ArrayList<>();
                for(UUID u : uniqueVisits.get(s))
                    uuid.add(u.toString());
            playerData.set(s + ".visitors", uuid);
        }
        saveConfig();
        savePlayerData();
    }
    
    private void savePlayerData() {
        try {
            playerData.save(playerDataFile);
        } catch(IOException e) {
            Bukkit.getServer().getLogger().severe("Unable to save playerData.yml!");
            e.printStackTrace();
        }
    }
    
    boolean makeEssentials() {
        Plugin p = Bukkit.getPluginManager().getPlugin("Essentials");
        if(p.isEnabled() && (p instanceof Essentials))
            essentials = (Essentials) p;
        return essentials != null;
    }
    
    Essentials getEssentials() {
        return essentials;
    }
}
