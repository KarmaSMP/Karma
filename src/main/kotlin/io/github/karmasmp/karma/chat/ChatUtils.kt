package io.github.karmasmp.karma.chat

import io.github.karmasmp.karma.chat.Formatting.allTags
import io.github.karmasmp.karma.util.Sounds

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ChatUtils {
    private val HEART = Component.text("❤")
    private val GREEN_HEART = HEART.color(NamedTextColor.GREEN)
    private val YELLOW_HEART = HEART.color(NamedTextColor.YELLOW)
    private val RED_HEART = HEART.color(NamedTextColor.RED)
    private val GRAY_HEART = HEART.color(NamedTextColor.DARK_GRAY)

    fun livesAsComponent(liveCount: Int): Component {
        return if (liveCount >= 3) {
            GREEN_HEART
                .append(GREEN_HEART)
                .append(GREEN_HEART)
        } else if (liveCount == 2) {
            GRAY_HEART
                .append(YELLOW_HEART)
                .append(YELLOW_HEART)
        } else if (liveCount == 1) {
            GRAY_HEART
                .append(GRAY_HEART)
                .append(RED_HEART)
        } else {
            GRAY_HEART
                .append(GRAY_HEART)
                .append(GRAY_HEART)
        }
    }

    /** Sends a message to the admin channel which includes all online admins. **/
    fun broadcastAdmin(rawMessage : String, isSilent: Boolean) {
        val admin = Audience.audience(Bukkit.getOnlinePlayers())
            .filterAudience { (it as Player).hasPermission("karma.group.admin") }
        admin.sendMessage(
            allTags.deserialize("<prefix:admin>").append(allTags.deserialize(rawMessage))
        )
        if(!isSilent) {
            admin.playSound(Sounds.ADMIN_MESSAGE)
        }
    }

    /** Sends a message to the dev channel which includes all online devs. **/
    fun broadcastDev(rawMessage : String, isSilent: Boolean) {
        val dev = Audience.audience(Bukkit.getOnlinePlayers())
            .filterAudience { (it as Player).hasPermission("karma.group.dev") }
        dev.sendMessage(
            allTags.deserialize("<prefix:dev>").append(allTags.deserialize(rawMessage))
        )
        if(!isSilent) {
            dev.playSound(Sounds.ADMIN_MESSAGE)
        }
    }
}