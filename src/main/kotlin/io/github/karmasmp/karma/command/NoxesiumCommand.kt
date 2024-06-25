package io.github.karmasmp.karma.command

import io.github.karmasmp.karma.util.Noxesium

import io.papermc.paper.command.brigadier.CommandSourceStack

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

import org.bukkit.entity.Player

import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class NoxesiumCommand {
    @Command("noxesium head <player>")
    @Permission("karma.cmd.noxesium")
    fun noxesiumHead(css : CommandSourceStack, player : Player) {
        if(css.sender is Player) {
            if(Noxesium.isNoxesiumUser(css.sender as Player)) {
                css.sender.sendMessage(Component.text("${player.name}'s head: ", NamedTextColor.YELLOW).append(Noxesium.buildSkullComponent(player.uniqueId, false, 0, 0, 1.0f)))
            } else {
                css.sender.sendMessage(Component.text("You are not running Noxesium, you numpty!", NamedTextColor.RED))
            }
        }
    }

    @Command("noxesium protocol <player>")
    @Permission("karma.cmd.noxesium")
    fun noxesiumProtocol(css : CommandSourceStack, player : Player) {
        val protocol = Noxesium.getNoxesiumUsers()[player.uniqueId]
        if(protocol != null) {
            css.sender.sendMessage(Component.text("${player.name}'s Noxesium protocol is ", NamedTextColor.YELLOW).append(Component.text("$protocol", NamedTextColor.GOLD)).append(Component.text(".", NamedTextColor.YELLOW)))
        } else {
            css.sender.sendMessage(Component.text("${player.name} is not registered as a Noxesium user.", NamedTextColor.YELLOW))
        }
    }
}