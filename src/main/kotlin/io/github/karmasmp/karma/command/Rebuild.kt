package io.github.karmasmp.karma.command

import io.github.karmasmp.karma.PluginRebuilder.rebuild
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class Rebuild {
    @Command("karma rebuild [branch]")
    @Permission("karma.admin.plugin.rebuild")
    fun echo(css: CommandSourceStack, branch: String = "main") {
        css.sender.sendMessage("Rebuilding based on branch: $branch")
        rebuild(branch)
    }
}