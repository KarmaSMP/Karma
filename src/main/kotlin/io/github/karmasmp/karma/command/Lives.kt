package io.github.karmasmp.karma.command

import io.github.karmasmp.karma.player.PlayerManager.getKarmaLives
import io.github.karmasmp.karma.player.PlayerManager.setKarmaLives
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player
import org.incendo.cloud.annotation.specifier.Range
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class Lives {
    @Command("lives get [player]")
    @Permission("karma.cmd.lives.get")
    fun get(css: CommandSourceStack, player: Player?) {
        if (player == null) {
            css.sender.sendMessage("You have ${(css.sender as Player).getKarmaLives()} lives remaining.")
        } else {
            css.sender.sendMessage("The player ${player.name} has ${player.getKarmaLives()} lives remaining.")
        }
    }

    @Command("lives set <player> <amount>")
    @Permission("karma.cmd.lives.get")
    fun set(css: CommandSourceStack, player: Player, @Range(min = "0", max = "3") amount: Int) {
        player.setKarmaLives(amount)
        css.sender.sendMessage("Set ${player.name} to $amount lives.")
    }
}