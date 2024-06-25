package io.github.karmasmp.karma.command

import io.github.karmasmp.karma.chat.ChatUtils

import io.papermc.paper.command.brigadier.CommandSourceStack

import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class ChatCommand {
    @Command("ac <text>")
    @Permission("karma.cmd.chat.admin")
    fun adminChat(css : CommandSourceStack, text : Array<String>) {
        ChatUtils.broadcastAdmin("<dark_red>[ADMIN] ${css.sender.name}<white>: ${text.joinToString(" ")}", false)
    }

    @Command("dc <text>")
    @Permission("karma.cmd.chat.dev")
    fun devChat(css : CommandSourceStack, text : Array<String>) {
        ChatUtils.broadcastDev("<gold>[DEV] ${css.sender.name}<white>: ${text.joinToString(" ")}", false)
    }
}