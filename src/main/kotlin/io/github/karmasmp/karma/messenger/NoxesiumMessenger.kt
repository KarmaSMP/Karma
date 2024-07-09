package io.github.karmasmp.karma.messenger

import io.github.karmasmp.karma.chat.ChatUtils
import io.github.karmasmp.karma.logger
import io.github.karmasmp.karma.util.Noxesium
import io.github.karmasmp.karma.util.NoxesiumChannel

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class NoxesiumMessenger : PluginMessageListener {
    override fun onPluginMessageReceived(channel : String, player : Player, message : ByteArray) {
        logger.info("Channel: $channel | Player: ${player.name} | Message: ${parseIntByteArray(player, message)}")
        if(channel == NoxesiumChannel.NOXESIUM_V1_CLIENT_INFORMATION_CHANNEL.channel || channel == NoxesiumChannel.NOXESIUM_V2_CLIENT_INFORMATION_CHANNEL.channel) {
            Noxesium.addNoxesiumUser(player, parseIntByteArray(player, message))
        }
    }

    private fun parseIntByteArray(player: Player, bytes : ByteArray) : Int {
        // Could quite possibly be destroyed if Daniel changes how information is sent from Noxesium.
        try {
            logger.info("[NOX] ByteArray passed and read for player ${player.name}.")
            return bytes[1].toInt()
        } catch(e: Exception) {
            ChatUtils.broadcastDev("<red>Failed to read ByteArray for player <yellow>${player.name}<red>, disabling Noxesium features for this user.", false)
            logger.info("[NOX] Failed to read ByteArray for player ${player.name}, disabling Noxesium features for this user.")
            return 0
        }
    }
}