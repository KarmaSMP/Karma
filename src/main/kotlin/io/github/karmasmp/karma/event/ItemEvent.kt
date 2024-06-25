package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent

@Suppress("unused")
class ItemEvent : Listener {
    @EventHandler
    private fun onItemPickup(e : PlayerAttemptPickupItemEvent) {
        if(e.player.isAdmin() && !e.player.isInStaffMode()) {
            e.isCancelled = true
        }
    }

    @EventHandler
    private fun onItemDrop(e : PlayerDropItemEvent) {
        if(e.player.isAdmin() && !e.player.isInStaffMode()) {
            e.isCancelled = true
        }
    }
}