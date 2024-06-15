package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.player.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoin : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PlayerManager.initPlayer(event.player)
    }
}