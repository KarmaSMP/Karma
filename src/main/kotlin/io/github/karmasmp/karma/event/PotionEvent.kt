package io.github.karmasmp.karma.event

import org.bukkit.entity.AreaEffectCloud
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent

@Suppress("unused")
class PotionEvent : Listener {
    @EventHandler
    private fun onPotionEffectChange(event: EntityPotionEffectEvent) {
        if(event.entity is Player) {
            val player = event.entity as Player
            if(player.vehicle != null) {
                if(player.vehicle is AreaEffectCloud) {
                    event.isCancelled = true
                }
            }
        }
    }
}