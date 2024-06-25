package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTargetEvent

@Suppress("unused")
class PathfindEvent : Listener {
    @EventHandler
    private fun onPathfind(e : EntityTargetEvent) {
        if(e.target != null) {
            if(e.target is Player) {
                val targetedPlayer = e.target as Player
                if(targetedPlayer.isAdmin() && !targetedPlayer.isInStaffMode()) {
                    e.target = null
                    e.isCancelled = true
                }
            }
        }
    }
}