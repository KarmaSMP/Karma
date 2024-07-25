package io.github.karmasmp.karma.player.admin

import io.github.karmasmp.karma.plugin
import io.github.karmasmp.karma.util.Pathfinder

import org.bukkit.Bukkit
import org.bukkit.entity.Player

import java.util.UUID

import kotlin.collections.ArrayList

object Admin {
    private val admins = ArrayList<UUID>()
    private val staffMode = ArrayList<UUID>()

    fun addAdmin(player: Player) {
        if(!admins.contains(player.uniqueId)) {
            admins.add(player.uniqueId)
            hidePlayer(player)
        }
    }

    fun removeAdmin(player: Player) {
        if(admins.contains(player.uniqueId)) admins.remove(player.uniqueId)
    }

    fun joinStaffMode(player: Player) {
        if(!staffMode.contains(player.uniqueId)) {
            staffMode.add(player.uniqueId)
            showPlayer(player)
        }
    }

    fun leaveStaffMode(player: Player) {
        if(staffMode.contains(player.uniqueId)) {
            staffMode.remove(player.uniqueId)
            hidePlayer(player)
            Pathfinder.clearNearbyTargets(player)
        }
    }

    fun showPlayer(admin: Player) {
        val nonAdmins = Bukkit.getOnlinePlayers().filter { nonAdmin -> !admins.contains(nonAdmin.uniqueId) }
        for(player in nonAdmins) {
            player.showPlayer(plugin, admin)
        }
    }

    fun hidePlayer(admin: Player) {
        val nonAdmins = Bukkit.getOnlinePlayers().filter { nonAdmin -> !admins.contains(nonAdmin.uniqueId) }
        for(player in nonAdmins) {
            player.hidePlayer(plugin, admin)
        }
    }

    fun getAdmins() : ArrayList<UUID> {
        return admins
    }

    fun getStaffMode() : ArrayList<UUID> {
        return staffMode
    }

    fun Player.isAdmin(): Boolean {
        return admins.contains(this.uniqueId)
    }

    fun Player.isInStaffMode(): Boolean {
        return staffMode.contains(this.uniqueId)
    }
}