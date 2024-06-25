package io.github.karmasmp.karma.event

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
            Admin.removeAdmin(event.player)
        }
        if(Admin.getStaffMode().contains(event.player.uniqueId)) {
            Admin.leaveStaffMode(event.player)
        }
    }
}