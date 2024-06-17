package io.github.karmasmp.karma.chat

import io.github.karmasmp.karma.util.Noxesium

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

object Formatting {
    /** Prefix enum for allowing MiniMessage usage of the <prefix:NAME> tag in messages. **/
    private enum class Prefix(val prefixName: String, val value: String) {
        DEV_PREFIX("dev", ""),
        ADMIN_PREFIX("admin", ""),
        NO_PREFIX("", ""),
        WARNING_PREFIX("warning", "⚠ ");

        companion object {
            fun ofName(str : String): Prefix {
                for(p in entries) {
                    if (p.prefixName == str) return p
                }
                return NO_PREFIX
            }
        }
    }

    private val KARMA_COLOUR = TagResolver.resolver("karmacolour", Tag.styling(TextColor.color(255, 174, 97)))
    private val NOTIFICATION_COLOUR = TagResolver.resolver("notifcolour", Tag.styling(TextColor.color(219, 0, 96)))

    val allTags = MiniMessage.builder()
        .tags(
            TagResolver.builder()
                .resolver(StandardTags.defaults())
                .resolver(Noxesium.skullResolver())
                .resolver(KARMA_COLOUR)
                .resolver(NOTIFICATION_COLOUR)
                .resolver(prefix())
                .build()
        )
        .build()

    val restrictedTags = MiniMessage.builder()
        .tags(
            TagResolver.builder()
                .resolver(StandardTags.color())
                .resolver(StandardTags.decorations())
                .resolver(StandardTags.rainbow())
                .resolver(StandardTags.reset())
                .resolver(Noxesium.skullResolver())
                .resolver(KARMA_COLOUR)
                .resolver(NOTIFICATION_COLOUR)
                .build()
        )
        .build()

    /** Builds a prefix tag. **/
    private fun prefix() : TagResolver {
        return TagResolver.resolver("prefix") { args, _ ->
            val prefixName = args.popOr("Name not supplied.")
            Tag.inserting(
                Component.text(Prefix.ofName(prefixName.toString()).value)
            )
        }
    }
}