package me.crepppy.warpsgui;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpListener implements Listener {
    @EventHandler
    public void onPlayerDoWarp(PlayerCommandPreprocessEvent e) {
        if(e.getMessage().equalsIgnoreCase("/warp")) {
            e.setCancelled(true);
            e.getPlayer().performCommand("warps");
        }
        if(e.getMessage().toLowerCase().startsWith("/warp ")) {
            if(WarpsGUI.getInstance().getEssentials() == null)
                if(WarpsGUI.getInstance().makeEssentials()) {
                    Bukkit.getServer().getLogger().severe("Error: Essentials not found!");
                    if(!WarpsGUI.getInstance().getEssentials().getWarps().getList().contains(e.getMessage().split(" ")[1]))
                        return;
                    if(WarpsGUI.uniqueVisits.containsKey(e.getMessage().split(" ")[1]) && WarpsGUI.uniqueVisits.get(e.getMessage().split(" ")[1]).contains(e.getPlayer().getUniqueId()))
                        return;
                    if(WarpsGUI.warps.keySet().contains(e.getMessage().split(" ")[1])) {
                        WarpsGUI.warps.replace(e.getMessage().split(" ")[1], WarpsGUI.warps.get(e.getMessage().split(" ")[1]) + 1);
                        List<UUID> newList = WarpsGUI.uniqueVisits.get(e.getMessage().split(" ")[1]);
                        if(newList == null) {
                            newList = new ArrayList<UUID>() {{
                                add(e.getPlayer().getUniqueId());
                            }};
                            WarpsGUI.uniqueVisits.put(e.getMessage().split(" ")[1], newList);
                        } else {
                            newList.add(e.getPlayer().getUniqueId());
                            WarpsGUI.uniqueVisits.replace(e.getMessage().split(" ")[1], newList);
                        }
                        
                    } else {
                        WarpsGUI.warps.put(e.getMessage().split(" ")[1], 1);
                        WarpsGUI.uniqueVisits.put(e.getMessage().split(" ")[1], new ArrayList<UUID>() {{
                            add(e.getPlayer().getUniqueId());
                        }});
                    }
                }
            if(e.getMessage().toLowerCase().startsWith("/warps")) {
                if(Bukkit.getPluginCommand(e.getMessage().substring(1)).getPlugin().getName().equalsIgnoreCase("essentials")) {
                    e.setCancelled(true);
                    e.getPlayer().performCommand("warpsgui:warps");
                }
            }
        }
    }
}
