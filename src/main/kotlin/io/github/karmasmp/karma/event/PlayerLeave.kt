package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.chat.Formatting
import io.github.karmasmp.karma.player.PlayerManager.getKarmaLives
import io.github.karmasmp.karma.player.admin.Admin
import io.github.karmasmp.karma.player.nametag.PlayerNametag
import io.github.karmasmp.karma.util.Noxesium

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeave : Listener {
    @EventHandler
    private fun onLeave(event: PlayerQuitEvent) {
        PlayerNametag.cancelNametagTask(event.player.uniqueId)
        Noxesium.removeNoxesiumUser(event.player)
        if(Admin.getAdmins().contains(event.player.uniqueId)) {
            event.quitMessage(Formatting.allTags.deserialize(""))
            Admin.removeAdmin(event.player)
        } else {
            event.quitMessage(
                Formatting.allTags.deserialize(
                "${if(event.player.getKarmaLives() >= 3) "<green>"
                else if(event.player.getKarmaLives() == 2) "<yellow>"
                else if(event.player.getKarmaLives() == 1) "<red>"
                else "<dark_gray>"}${event.player.name}<reset> left the game.")
            )
        }
        if(Admin.getStaffMode().contains(event.player.uniqueId)) {
            Admin.leaveStaffMode(event.player)
        }
    }
}