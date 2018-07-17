package net.craftminecraft.bukkit.ipwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import com.google.common.collect.Lists;

import java.net.InetAddress;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class PlayerListener implements Listener {

    IPWhitelist plugin;
    ReflectUtils reflect = new ReflectUtils();
    Map<UUID, InetAddress> addresses = new HashMap();

    public PlayerListener(IPWhitelist plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChannelRegistered(PlayerRegisterChannelEvent ev) {
        if (this.plugin.getBungeeIPs().isSetupModeEnabled()
                && ev.getChannel().equals("BungeeCord")) {
            this.plugin.getBungeeIPs().whitelist(addresses.get(ev.getPlayer().getUniqueId()).getHostAddress());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(PlayerPreLoginEvent ev) {
    	for (Player player : Bukkit.getOnlinePlayers()) {
    		if ((player.getName().equalsIgnoreCase(ev.getName())) || (player.getUniqueId().equals(ev.getUniqueId())) || (player.getUniqueId().toString().toLowerCase().replaceAll("-", "").equalsIgnoreCase(ev.getUniqueId().toString().toLowerCase().replaceAll("-", "")))) {
            	ev.setKickMessage("Ya hay un jugador conectado con ese nick!");
    			ev.setResult(PlayerPreLoginEvent.Result.KICK_WHITELIST);
    			return;
    		}
    	}
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent ev) {
        final InetAddress addr = ev.getRealAddress();
        if (!this.plugin.allow(addr)) {
        	ev.setKickMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("playerKickMessage")));
        	ev.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
        	return;
        }
    }
}
