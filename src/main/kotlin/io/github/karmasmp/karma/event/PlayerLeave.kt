package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.util.Noxesium

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeave : Listener {
    @EventHandler
    private fun onLeave(event : PlayerQuitEvent) {
        Noxesium.removeNoxesiumUser(event.player)
    }
}