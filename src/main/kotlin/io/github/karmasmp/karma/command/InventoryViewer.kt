package io.github.karmasmp.karma.command

import io.github.karmasmp.karma.chat.ChatUtils
import io.github.karmasmp.karma.logger
import io.github.karmasmp.karma.util.Sounds

import io.papermc.paper.command.brigadier.CommandSourceStack

import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Flag
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class InventoryViewer {
    @Command("invsee <player>")
    @Permission("karma.cmd.invsee")
    fun viewInventory(css: CommandSourceStack, player: Player, @Flag("ec") isEChest : Boolean) {
        if(css.sender is Player) {
            val sender = css.sender as Player
            if(sender == player) {
                ChatUtils.messageAudience(sender, "<red><prefix:warning> You cannot view your own ${if(isEChest) "<light_purple>Ender Chest" else "<yellow>Inventory"}<red>, you numpty.", false)
                sender.playSound(Sounds.ACTION_FAIL)
            } else {
                if(isEChest) {
                    ChatUtils.broadcastAdmin("<notifcolour>${sender.name}<reset> is viewing <notifcolour>${player.name}<reset>'s <light_purple>Ender Chest<reset>.", false)
                    sender.openInventory(player.enderChest)
                } else {
                    ChatUtils.broadcastAdmin("<notifcolour>${sender.name}<reset> is viewing <notifcolour>${player.name}<reset>'s <yellow>Inventory<reset>.", false)
                    sender.openInventory(player.inventory)
                }
            }
        } else {
            logger.warning("Only players are able to utilise the /invsee command.")
        }
    }
}