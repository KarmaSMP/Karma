package io.github.karmasmp.karma.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class Echo {
    @Command("echo <text>")
    @Permission("karma.cmd.echo")
    fun echo(css: CommandSourceStack, text: Array<String>) {
        css.sender.sendMessage(text.joinToString(" "))
    }
}
