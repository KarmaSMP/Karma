package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.chat.Formatting
import io.github.karmasmp.karma.player.PlayerManager
import io.github.karmasmp.karma.player.PlayerManager.getKarmaLives
import io.github.karmasmp.karma.player.admin.Admin
import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode
import io.github.karmasmp.karma.player.nametag.PlayerNametag
import io.github.karmasmp.karma.player.visuals.PlayerVisuals

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PlayerManager.initPlayer(event.player)
        PlayerNametag.buildNametag(event.player)
        PlayerVisuals.actionBar(event.player)

        if(event.player.hasPermission("karma.group.admin")) {
            event.joinMessage(Formatting.allTags.deserialize(""))
            Admin.addAdmin(event.player)
        } else {
            event.joinMessage(Formatting.allTags.deserialize(
                "${if(event.player.getKarmaLives() >= 3) "<green>"
                            else if(event.player.getKarmaLives() == 2) "<yellow>"
                            else if(event.player.getKarmaLives() == 1) "<red>"
                            else "<dark_gray>"}${event.player.name}<reset> joined the game.")
            )
        }

        for(player in Bukkit.getOnlinePlayers()) {
            if(player.isAdmin() && !player.isInStaffMode()) {
                Admin.hidePlayer(player)
            }
        }
    }
}