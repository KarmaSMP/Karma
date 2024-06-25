package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

@Suppress("unused")
class BlockEvent : Listener {
    @EventHandler
    private fun onBlockBreak(e : BlockBreakEvent) {
        if(e.player.isAdmin() && !e.player.isInStaffMode()) {
            e.isCancelled = true
        }
    }

    @EventHandler
    private fun onBlockPlace(e : BlockPlaceEvent) {
        if(e.player.isAdmin() && !e.player.isInStaffMode()) {
            e.isCancelled = true
        }
    }
}