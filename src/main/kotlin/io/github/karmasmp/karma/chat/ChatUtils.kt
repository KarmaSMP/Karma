package io.github.karmasmp.karma.chat

import io.github.karmasmp.karma.chat.Formatting.allTags
import io.github.karmasmp.karma.chat.Formatting.restrictedTags
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode
import io.github.karmasmp.karma.player.PlayerManager.getKarmaLives
import io.github.karmasmp.karma.util.Noxesium
import io.github.karmasmp.karma.util.Sounds

import io.papermc.paper.chat.ChatRenderer

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

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

    /** Sends a message to the specified audience. **/
    fun messageAudience(recipient: Audience, message: String, restricted: Boolean, vararg placeholders: TagResolver) {
        val resolvers = mutableListOf<TagResolver>()
        for(p in placeholders) {
            resolvers.add(p)
        }

        recipient.sendMessage(formatMessage(message, restricted, TagResolver.resolver(resolvers)))
    }

    /** Formats a message, which can produce different results depending on if restricted or not. **/
    fun formatMessage(message: String, restricted: Boolean, vararg placeholders: TagResolver): Component {
        val resolvers = mutableListOf<TagResolver>()
        for(p in placeholders) {
            resolvers.add(p)
        }

        return if (restricted) {
            restrictedTags.deserialize(message, TagResolver.resolver(resolvers))
        } else {
            allTags.deserialize(message, TagResolver.resolver(resolvers))
        }
    }

    /** Sends a message to the admin channel which includes all online admins. **/
    fun broadcastAdmin(rawMessage: String, isSilent: Boolean) {
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
    fun broadcastDev(rawMessage: String, isSilent: Boolean) {
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

object GlobalRenderer : ChatRenderer {
    override fun render(source: Player, sourceDisplayName: Component, message: Component, viewer: Audience): Component {
        val playerHead = Noxesium.buildSkullComponent(source.uniqueId, false, 0, 0, 1.0f)
        if(source.hasPermission("karma.group.admin")) {
            return if(source.isInStaffMode()) {
                playerHead
                    .append(Component.text(Formatting.Prefix.ADMIN_PREFIX.value))
                    .append(sourceDisplayName.color(NamedTextColor.DARK_RED))
                    .append(Component.text(" SM", NamedTextColor.GOLD))
                    .append(Component.text(": "))
                    .append(message)
            } else {
                playerHead
                    .append(Component.text(Formatting.Prefix.ADMIN_PREFIX.value))
                    .append(sourceDisplayName.color(NamedTextColor.DARK_RED))
                    .append(Component.text(": "))
                    .append(message)
            }
        } else {
            val lifeCount = source.getKarmaLives()

            val lives = ChatUtils.livesAsComponent(lifeCount).append(Component.text(" "))

            return playerHead
                .append(lives)
                .append(sourceDisplayName)
                .append(Component.text(": "))
                .append(message)
        }
    }
}