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
    enum class Prefix(val prefixName: String, val value: String) {
        DEV_PREFIX("dev", "\uD001"),
        ADMIN_PREFIX("admin", "\uD002"),
        STAFF_MODE_PREFIX("staff", "\uD004"),
        CREATOR_MODE_PREFIX("creator", "\uD005"),
        NO_PREFIX("", ""),
        WARNING_PREFIX("warning", "⚠");

        companion object {
            fun ofName(str : String): Prefix {
                for(p in entries) {
                    if (p.prefixName == str) return p
                }
                return NO_PREFIX
            }
        }
    }

    /** ActionBarIcon enum for allowing MiniMessage usage of the <actionbar:NAME> tag for actionbar icons. **/
    enum class ActionBarIcon(val iconName: String, val value: String) {
        ADMIN("admin", "\uE004"),
        ADMIN_STAFF_MODE("staffmode", "\uE005"),
        ONE_LIFE("one", "\uE001"),
        TWO_LIVES("two", "\uE002"),
        THREE_LIVES("three", "\uE003"),
        GHOST("ghost", "\uE000"),
        NULL("", "");

        companion object {
            fun ofName(str : String): ActionBarIcon {
                for(ab in ActionBarIcon.entries) {
                    if (ab.iconName == str) return ab
                }
                return NULL
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
                .resolver(actionBar())
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

    /** Builds an action bar tag. **/
    private fun actionBar() : TagResolver {
        return TagResolver.resolver("actionbar") { args, _ ->
            val iconName = args.popOr("Name not supplied.")
            Tag.inserting(
                Component.text(ActionBarIcon.ofName(iconName.toString()).value)
            )
        }
    }
}