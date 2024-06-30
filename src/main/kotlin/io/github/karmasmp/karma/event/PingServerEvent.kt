package io.github.karmasmp.karma.event

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import com.destroystokyo.paper.event.server.PaperServerListPingEvent.ListedPlayerInfo

import io.github.karmasmp.karma.player.PlayerManager.getPlayer
import io.github.karmasmp.karma.player.admin.Admin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PingServerEvent : Listener {
    @EventHandler
    private fun onServerPing(e : PaperServerListPingEvent) {
        for(admin in Admin.getAdmins()) {
            if(admin.getPlayer().player?.isInStaffMode() == false) {
                if(e.listedPlayers.contains(ListedPlayerInfo("${admin.getPlayer().name}", admin.getPlayer().uniqueId))) {
                    e.listedPlayers.remove(ListedPlayerInfo("${admin.getPlayer().name}", admin.getPlayer().uniqueId))
                    e.numPlayers -= 1
                }
            }
        }
    }
}