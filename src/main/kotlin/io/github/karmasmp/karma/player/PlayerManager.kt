package io.github.karmasmp.karma.player

import io.github.karmasmp.karma.logger
import org.bukkit.entity.Player

object PlayerManager {
    private val karmaPlayers = mutableSetOf<KarmaPlayer>()

    fun initPlayer(player: Player) {
        val existingPlayer = karmaPlayers.find { it.uuid == player.uniqueId }
        if (existingPlayer != null) {
            logger.info("Player ${player.name} already initialized.")
            return
        }

        karmaPlayers.add(KarmaPlayer(player.uniqueId))
    }

    fun death(player: Player) {
        val uuid = player.uniqueId
        val karmaPlayer = karmaPlayers.find { it.uuid == uuid }
        if (karmaPlayer == null) throw PlayerManagerException("Player $uuid not found on death.")
        if (karmaPlayer.lives == 0) return

        karmaPlayer.setLives(karmaPlayer.lives - 1)
        if (karmaPlayer.lives == 0) {
            karmaPlayer.setState(PlayerState.GHOST)
        }
    }

    fun Player.getKarmaLives(): Int {
        val karmaPlayer = karmaPlayers.find { it.uuid == this.uniqueId }
        return karmaPlayer!!.lives
    }

    fun Player.setKarmaLives(lives: Int) {
        if (lives > 3 || lives < 0) throw PlayerManagerException("Invalid amount of lives passed")

        val karmaPlayer = karmaPlayers.find { it.uuid == this.uniqueId }
        karmaPlayer!!.setLives(lives)
    }
}
