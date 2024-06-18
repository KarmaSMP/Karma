package io.github.karmasmp.karma.player

import io.github.karmasmp.karma.plugin
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

private val LIVES_KEY = NamespacedKey(plugin, "player.lives")
private val STATE_KEY = NamespacedKey(plugin, "player.state")

private const val DEFAULT_LIVES = 3
private val DEFAULT_STATE = PlayerState.ALIVE

class KarmaPlayer(val uuid: UUID) {

    var lives: Int
        private set
    var state: PlayerState
        private set

    init {
        val player = Bukkit.getPlayer(uuid)!!
        lives = player.persistentDataContainer.getOrDefault(LIVES_KEY, PersistentDataType.INTEGER, DEFAULT_LIVES)

        val stateStr = player.persistentDataContainer.getOrDefault(STATE_KEY, PersistentDataType.STRING, DEFAULT_STATE.toString())
        state = PlayerState.valueOf(stateStr)
    }

    fun setLives(count: Int) {
        val player = Bukkit.getPlayer(uuid)!!
        player.persistentDataContainer.set(LIVES_KEY, PersistentDataType.INTEGER, count)
        lives = count
    }

    fun setState(newState: PlayerState) {
        val player = Bukkit.getPlayer(uuid)!!
        player.persistentDataContainer.set(STATE_KEY, PersistentDataType.STRING, newState.toString())
        state = newState
    }
}
