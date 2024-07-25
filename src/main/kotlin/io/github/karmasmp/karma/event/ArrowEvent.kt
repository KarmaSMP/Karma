package io.github.karmasmp.karma.event

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

@Suppress("unused")
class ArrowEvent : Listener {
    @EventHandler
    private fun onArrowHit(event: ProjectileHitEvent) {
        if(event.hitEntity != null) {
            if(event.hitEntity is Player && event.entity.shooter is Player) {
                val playerHit = event.hitEntity as Player
                val shooter = event.entity.shooter as Player
                if(playerHit.lastDamage > 0.0) {
                    shooter.stopSound("entity.arrow.hit_player")
                    shooter.playSound(shooter.location, "entity.arrow.hit_player", 1.0f,
                        if(playerHit.health.toInt() >= 15) 1.25f
                        else if(playerHit.health.toInt() in 10..14) 1.0f
                        else if(playerHit.health.toInt() in 5..9) 0.75f
                        else 0.5f
                    )
                }
            }
        }
    }
}