package io.github.karmasmp.karma

import io.papermc.paper.command.brigadier.CommandSourceStack
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
}