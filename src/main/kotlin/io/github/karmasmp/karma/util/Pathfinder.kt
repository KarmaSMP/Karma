package io.github.karmasmp.karma.util

import org.bukkit.entity.Bee
import org.bukkit.entity.Mob
import org.bukkit.entity.PigZombie
import org.bukkit.entity.Player
import org.bukkit.entity.Warden
import org.bukkit.entity.Wolf

object Pathfinder {
    fun clearNearbyTargets(player : Player) {
        val nearbyEntities = player.getNearbyEntities(20.0, 20.0, 20.0).filterIsInstance<Mob>()
        for(entity in nearbyEntities) {
            if(entity.target == player) {
                if(entity is PigZombie) {
                    entity.isAngry = false
                    entity.anger = -1
                }
                if(entity is Warden) {
                    if(entity.entityAngryAt == player) {
                        entity.setAnger(player, 0)
                        entity.clearAnger(player)
                    }
                }
                if(entity is Bee) {
                    entity.anger = -1
                }
                if(entity is Wolf) {
                    entity.isAngry = false
                }
                entity.isAggressive = false
                entity.target = null
            }
        }
    }
}