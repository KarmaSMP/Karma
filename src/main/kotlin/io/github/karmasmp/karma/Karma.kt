package io.github.karmasmp.karma

import io.github.karmasmp.karma.event.*
import io.github.karmasmp.karma.messenger.BrandMessenger
import io.github.karmasmp.karma.messenger.NoxesiumMessenger
import io.github.karmasmp.karma.player.nametag.PlayerNametag
import io.github.karmasmp.karma.util.NoxesiumChannel

import io.papermc.paper.command.brigadier.CommandSourceStack

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.description.CommandDescription
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.processors.cache.SimpleCache
import org.incendo.cloud.processors.confirmation.ConfirmationConfiguration
import org.incendo.cloud.processors.confirmation.ConfirmationManager
import org.incendo.cloud.processors.confirmation.annotation.ConfirmationBuilderModifier

import java.time.Duration

@Suppress("unused", "unstableApiUsage")
class Karma : JavaPlugin() {
    private lateinit var commandManager: PaperCommandManager<CommandSourceStack>
    private lateinit var annotationParser: AnnotationParser<CommandSourceStack>

    override fun onEnable() {
        logger.info("What is up gamers?!")
        registerCommands()
        registerEvents()
        registerPluginMessengers()
        PlayerNametag.setup()
    }

    override fun onDisable() {
        PlayerNametag.destroy()
        logger.info("We are no longer gaming.")
    }

    private fun registerCommands() {
        logger.info("Registering commands.")
        commandManager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
            .buildOnEnable(this)

        annotationParser = AnnotationParser(commandManager, CommandSourceStack::class.java)
        annotationParser.parseContainers()

        setupCommandConfirmation()
    }

    private fun setupCommandConfirmation() {
        logger.info("Setting up command confirmation.")
        val confirmationConfig = ConfirmationConfiguration.builder<CommandSourceStack>()
            .cache(SimpleCache.of())
            .noPendingCommandNotifier { css ->
                css.sender.sendMessage(
                    Component.text(
                        "You do not have any pending commands.",
                        NamedTextColor.RED
                    )
                ) }
            .confirmationRequiredNotifier { css, ctx ->
                css.sender.sendMessage(
                    Component.text("Confirm command ", NamedTextColor.RED).append(
                        Component.text("'/${ctx.commandContext()}' ", NamedTextColor.GREEN)
                    ).append(Component.text("by running ", NamedTextColor.RED)).append(
                        Component.text("'/confirm' ", NamedTextColor.YELLOW)
                    ).append(Component.text("to execute.", NamedTextColor.RED))
                ) }
            .expiration(Duration.ofSeconds(30))
            .build()

        val confirmationManager = ConfirmationManager.confirmationManager(confirmationConfig)
        commandManager.registerCommandPostProcessor(confirmationManager.createPostprocessor())

        commandManager.command(
            commandManager.commandBuilder("confirm")
                .handler(confirmationManager.createExecutionHandler())
                .commandDescription(CommandDescription.commandDescription("Confirm a pending command."))
                .permission("karma.cmd.confirm")
        )
        ConfirmationBuilderModifier.install(annotationParser)
    }

    private fun registerEvents() {
        logger.info("Registering events.")
        registerEvent(PlayerDeath())
        registerEvent(PlayerJoin())
        registerEvent(PlayerLeave())
        registerEvent(ChatListener())
        registerEvent(BlockEvent())
        registerEvent(DamageEvent())
        registerEvent(InteractEvent())
        registerEvent(ItemEvent())
        registerEvent(PathfindEvent())
        registerEvent(RespawnEvent())
    }

    private fun registerEvent(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    private fun registerPluginMessengers() {
        logger.info("Registering plugin messengers.")
        messenger.registerIncomingPluginChannel(this, "minecraft:brand", BrandMessenger())
        messenger.registerIncomingPluginChannel(this, NoxesiumChannel.NOXESIUM_V1_CLIENT_INFORMATION_CHANNEL.channel, NoxesiumMessenger())
        messenger.registerIncomingPluginChannel(this, NoxesiumChannel.NOXESIUM_V2_CLIENT_INFORMATION_CHANNEL.channel, NoxesiumMessenger())
    }
}

val plugin = Bukkit.getPluginManager().getPlugin("Karma")!!
val logger = plugin.logger
val messenger = Bukkit.getMessenger()