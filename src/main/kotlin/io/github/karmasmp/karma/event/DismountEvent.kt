package io.github.karmasmp.karma.event

import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDismountEvent

@Suppress("unused")
class DismountEvent : Listener {
    @EventHandler
    private fun onDismount(event: EntityDismountEvent) {
        if(event.entity is Player && event.dismounted is AreaEffectCloud) {
            event.isCancelled = true
        }
    }
}