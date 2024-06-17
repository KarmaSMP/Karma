package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.chat.ChatUtils
import io.github.karmasmp.karma.player.PlayerManager.getKarmaLives
import io.github.karmasmp.karma.util.Noxesium

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener : Listener, ChatRenderer {

    @EventHandler
    fun onChat(event : AsyncChatEvent) {
        event.renderer(this)
    }

    override fun render(source: Player, sourceDisplayName: Component, message: Component, viewer: Audience): Component {
        val lifeCount = source.getKarmaLives()

        val lives = Component.text("[")
            .append(ChatUtils.livesAsComponent(lifeCount))
            .append(Component.text("] "))

        val playerHead = Noxesium.buildSkullComponent(source.uniqueId, false, 0, 0, 1.0f).append(Component.space())

        return playerHead
            .append(lives)
            .append(sourceDisplayName.color(TextColor.color(255, 174, 97)))
            .append(Component.text(": "))
            .append(message)
    }
}
