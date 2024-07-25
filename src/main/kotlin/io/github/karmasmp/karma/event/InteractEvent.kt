package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode
import org.bukkit.entity.AreaEffectCloud

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

@Suppress("unused")
class InteractEvent : Listener {
    @EventHandler
    private fun onInteract(e : PlayerInteractEvent) {
        if(e.player.isAdmin() && !e.player.isInStaffMode()) {
            e.isCancelled = true
        }
        if(e.player.vehicle != null) {
            if(e.player.vehicle is AreaEffectCloud) {
                e.isCancelled = true
            }
        }
    }
}