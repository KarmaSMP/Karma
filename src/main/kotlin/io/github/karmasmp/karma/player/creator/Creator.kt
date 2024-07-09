package io.github.karmasmp.karma.player.creator

import io.github.karmasmp.karma.player.visuals.PlayerVisuals

import org.bukkit.entity.Player

import java.util.*

object Creator {
    private val creators = ArrayList<UUID>()

    fun toggleCreatorMode(player: Player) {
        val creator = creators.find { it == player.uniqueId }
        if(creator == null) {
            creators.add(player.uniqueId)
            PlayerVisuals.toggleCreator(player)
        } else {
            creators.remove(creator)
            PlayerVisuals.toggleCreator(player)
        }
    }

    fun getCreators(): ArrayList<UUID> {
        return creators
    }

    fun Player.isLive(): Boolean {
        return creators.contains(this.uniqueId)
    }
}