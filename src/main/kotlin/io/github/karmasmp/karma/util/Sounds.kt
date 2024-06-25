package io.github.karmasmp.karma.util

import io.github.karmasmp.karma.plugin
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object Sounds {
    val SERVER_ANNOUNCEMENT = Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1.0f, 1.0f)
    val RESTART_ANNOUNCEMENT = Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1.0f, 1.0f)
    val ADMIN_MESSAGE = Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 1.0f, 2.0f)
    val ACTION_FAIL = Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.MASTER, 1.0f, 0.0f)

    fun playProgressSoundLoop(player: Player, sound: String, isDescending: Boolean) {
        object : BukkitRunnable() {
            var timer = 0
            var pitch = if(isDescending) 1.5F else 0.5F
            override fun run() {
                if(timer % 4 == 0) {
                    if(isDescending) pitch -= 0.1F else pitch += 0.1F
                }
                player.playSound(player.location, sound, 0.75f, pitch)
                if(isDescending) {
                    if(pitch <= 0.5F) {
                        this.cancel()
                    }
                } else {
                    if(pitch >= 1.5F) {
                        this.cancel()
                    }
                }
                timer++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }
}