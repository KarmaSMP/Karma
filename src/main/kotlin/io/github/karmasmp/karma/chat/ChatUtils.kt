package io.github.karmasmp.karma.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

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
}