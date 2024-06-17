package io.github.karmasmp.karma.messenger

import io.github.karmasmp.karma.logger
import io.github.karmasmp.karma.util.Noxesium
import io.github.karmasmp.karma.util.NoxesiumChannel

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class NoxesiumMessenger : PluginMessageListener {
    override fun onPluginMessageReceived(channel : String, player : Player, message : ByteArray) {
        logger.info("Channel: $channel | Player: ${player.name} | Message: ${parseIntByteArray(message)}")
        if(channel == NoxesiumChannel.NOXESIUM_V1_CLIENT_INFORMATION_CHANNEL.channel || channel == NoxesiumChannel.NOXESIUM_V2_CLIENT_INFORMATION_CHANNEL.channel) {
            Noxesium.addNoxesiumUser(player, parseIntByteArray(message))
        }
    }

    private fun parseIntByteArray(bytes : ByteArray) : Int {
        // Could quite possibly be destroyed if Daniel changes how information is send from Noxesium.
        return bytes[1].toInt()
    }
}