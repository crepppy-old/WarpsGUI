package me.crepppy.warpsgui;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SetWarpListener implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if(e.getMessage().toLowerCase().startsWith("/setwarp")) {
            if(WarpsGUI.playerWarps.containsKey(e.getPlayer()))
                for(int i = WarpsGUI.playerWarps.get(e.getPlayer()); i <= 10; i++) {
                    if(e.getPlayer().hasPermission("essentials.warpset." + i))
                        break;
                    if(i == 10) {
                        e.getPlayer().sendMessage(ChatColor.RED + "You already have the maximum amount of warps set.");
                        e.setCancelled(true);
                        return;
                    }
                }
            if(e.getMessage().contains(" "))
                WarpsGUI.warpOwner.put(e.getMessage().split(" ")[1], e.getPlayer().getUniqueId());
            if(WarpsGUI.playerWarps.keySet().contains(e.getPlayer())) {
                WarpsGUI.playerWarps.replace(e.getPlayer(), WarpsGUI.playerWarps.get(e.getPlayer()) + 1);
            } else {
                WarpsGUI.playerWarps.put(e.getPlayer(), 1);
            }
            
        }
        if(e.getMessage().toLowerCase().startsWith("/delwarp ")) {
            String s = e.getMessage().split(" ")[1];
            WarpsGUI.warpOwner.remove(s);
            WarpsGUI.uniqueVisits.remove(s);
            if(WarpsGUI.playerWarps.containsKey(s))
                WarpsGUI.playerWarps.replace(e.getPlayer(), WarpsGUI.playerWarps.get(e.getPlayer()) - 1);
            WarpsGUI.warps.remove(s);
        }
    }
}
