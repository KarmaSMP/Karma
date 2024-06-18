package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.player.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent


class PlayerDeath : Listener {
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        PlayerManager.death(event.player)
    }
}