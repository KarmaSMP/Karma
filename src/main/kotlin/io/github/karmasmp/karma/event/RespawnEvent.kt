package io.github.karmasmp.karma.event

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent

import io.github.karmasmp.karma.player.nametag.PlayerNametag

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class RespawnEvent : Listener {
    @EventHandler
    private fun onRespawn(event: PlayerPostRespawnEvent) {
        PlayerNametag.buildNametag(event.player)
    }
}