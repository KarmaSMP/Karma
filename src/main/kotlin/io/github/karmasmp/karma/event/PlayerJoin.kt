package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.player.PlayerManager
import io.github.karmasmp.karma.player.admin.Admin
import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode
import io.github.karmasmp.karma.player.nametag.PlayerNametag
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PlayerManager.initPlayer(event.player)
        PlayerNametag.buildNametag(event.player)
        if(event.player.hasPermission("karma.group.admin")) {
            Admin.addAdmin(event.player)
        }
        for(player in Bukkit.getOnlinePlayers()) {
            if(player.isAdmin() && !player.isInStaffMode()) {
                Admin.hidePlayer(player)
            }
        }
    }
}