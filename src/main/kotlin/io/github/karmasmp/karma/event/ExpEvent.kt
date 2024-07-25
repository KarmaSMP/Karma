package io.github.karmasmp.karma.event

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent

import org.bukkit.entity.AreaEffectCloud

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ExpEvent : Listener {
    @EventHandler
    private fun onPickupXp(event: PlayerPickupExperienceEvent) {
        if(event.player.vehicle != null) {
            if(event.player.vehicle is AreaEffectCloud) {
                event.isCancelled = true
            }
        }
    }
}