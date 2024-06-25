package io.github.karmasmp.karma.command

import io.github.karmasmp.karma.player.PlayerManager.getKarmaPlayer
import io.github.karmasmp.karma.player.PlayerManager.setKarmaLives
import io.github.karmasmp.karma.player.visuals.PlayerVisuals
import io.github.karmasmp.karma.util.Sounds

import io.papermc.paper.command.brigadier.CommandSourceStack

import org.bukkit.entity.Player

import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Flag
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class DevCommand {
    @Command("loopsound <sound>")
    @Permission("karma.cmd.test")
    fun soundTest(css: CommandSourceStack, sound: String, @Flag("isDescending") isDescending : Boolean) {
        if(css.sender is Player) {
            val player = css.sender as Player
            Sounds.playProgressSoundLoop(player, sound, isDescending)
        }
    }

    @Command("ghostanim")
    @Permission("karma.cmd.test")
    fun ghostAnimTest(css: CommandSourceStack) {
        if(css.sender is Player) {
            val player = css.sender as Player
            player.setKarmaLives(0)
            PlayerVisuals.death(player.getKarmaPlayer())
        }
    }
}