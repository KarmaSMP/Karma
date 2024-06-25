package io.github.karmasmp.karma.command

import io.github.karmasmp.karma.chat.ChatUtils
import io.github.karmasmp.karma.player.admin.Admin
import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode

import io.papermc.paper.command.brigadier.CommandSourceStack

import net.kyori.adventure.text.Component

import org.bukkit.entity.Player

import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.Permission
import org.incendo.cloud.annotations.processing.CommandContainer
import org.incendo.cloud.processors.confirmation.annotation.Confirmation

@Suppress("unused", "unstableApiUsage")
@CommandContainer
class AdminCommand {
    @Command("staffmode")
    @Permission("karma.cmd.staffmode")
    @Confirmation
    fun staffMode(css: CommandSourceStack) {
        if(css.sender is Player) {
            val sender = css.sender as Player
            if(sender.isAdmin()) {
                if(!sender.isInStaffMode()) {
                    Admin.joinStaffMode(sender)
                    ChatUtils.broadcastAdmin("<notifcolour>${sender.name} <white>toggled <green>into <gold>Staff Mode<white>.", false)
                } else {
                    Admin.leaveStaffMode(sender)
                    ChatUtils.broadcastAdmin("<notifcolour>${sender.name} <white>toggled <red>out <white>of <gold>Staff Mode<white>.", false)
                }
            }
        }
    }

    @Command("admin list")
    @Permission("karma.cmd.list.admin")
    fun listAdmins(css: CommandSourceStack) {
        css.sender.sendMessage(Component.text("${Admin.getAdmins()}"))
    }

    @Command("admin list staffmode")
    @Permission("karma.cmd.list.admin")
    fun listStaffMode(css: CommandSourceStack) {
        css.sender.sendMessage(Component.text("${Admin.getStaffMode()}"))
    }
}