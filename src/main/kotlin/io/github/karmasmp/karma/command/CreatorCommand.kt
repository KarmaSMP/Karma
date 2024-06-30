package io.github.karmasmp.karma.command

import io.github.karmasmp.karma.player.creator.Creator
import io.papermc.paper.command.brigadier.CommandSourceStack

import org.bukkit.entity.Player

import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class CreatorCommand {
    @Command("creatormode")
    fun echo(css: CommandSourceStack) {
        if(css.sender is Player) {
            Creator.toggleCreatorMode(css.sender as Player)
        }
    }
}