package io.github.karmasmp.karma

import io.github.karmasmp.karma.event.ChatListener
import io.github.karmasmp.karma.event.PlayerDeath
import io.github.karmasmp.karma.event.PlayerJoin
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager


@Suppress("unused", "unstableApiUsage")
class Karma : JavaPlugin() {
    private lateinit var commandManager: PaperCommandManager<CommandSourceStack>

    override fun onEnable() {
        logger.info("What is up gamers")
        registerCommands()
        registerEvents()
    }

    override fun onDisable() {
        logger.info("We are no longer gaming")
    }

    private fun registerCommands() {
        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
            .buildOnEnable(this)

        val annotationParser = AnnotationParser(commandManager, CommandSourceStack::class.java)
        annotationParser.parseContainers()
    }

    private fun registerEvents() {
        registerEvent(PlayerDeath())
        registerEvent(PlayerJoin())
        registerEvent(ChatListener())
    }

    private fun registerEvent(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }
}

val logger = Bukkit.getPluginManager().getPlugin("Karma")!!.logger