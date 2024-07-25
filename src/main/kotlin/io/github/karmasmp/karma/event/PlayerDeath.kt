package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.player.PlayerManager
import io.github.karmasmp.karma.player.nametag.PlayerNametag

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeath : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        event.deathMessage()?.let { PlayerManager.death(event.player, it) }
        PlayerNametag.cancelNametagTask(event.player.uniqueId)
        event.isCancelled = true
    }
}