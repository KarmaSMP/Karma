package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.chat.GlobalRenderer

import io.papermc.paper.event.player.AsyncChatEvent

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener : Listener {
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        event.renderer(GlobalRenderer)
    }
}