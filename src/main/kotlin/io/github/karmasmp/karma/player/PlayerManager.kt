package io.github.karmasmp.karma.player

import io.github.karmasmp.karma.logger
import org.bukkit.entity.Player
import java.util.*

object PlayerManager {
    private val players = mutableSetOf<KarmaPlayer>()

    fun initPlayer(player: Player) {
        val existingPlayer = players.find { it.uuid == player.uniqueId }
        if (existingPlayer != null) {
            logger.info("Player ${player.name} already initialized.")
            return
        }

        // here we could fetch data from some persistent source (like pdc, db or something)
        players.add(KarmaPlayer(player.uniqueId))
    }

    fun death(uuid: UUID) {
        val player = players.find { it.uuid == uuid }
        if (player == null) throw PlayerManagerException("Player $uuid not found on death.")
        if (player.lives == 0) return

        player.lives -= 1
        if (player.lives == 0) {
            player.state = PlayerState.GHOST
        }
    }

    fun Player.getKarmaLives(): Int {
        val karmaPlayer = players.find { it.uuid == this.uniqueId }
        return karmaPlayer!!.lives
    }

    fun Player.setKarmaLives(lives: Int) {
        if (lives > 3 || lives < 0) throw PlayerManagerException("Invalid amount of lives passed")

        val karmaPlayer = players.find { it.uuid == this.uniqueId }
        karmaPlayer!!.lives = lives
    }
}
