package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.player.admin.Admin

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

@Suppress("unused")
class DamageEvent : Listener {
    @EventHandler
    private fun onDamage(e : EntityDamageEvent) {
        if(e.entity is Player) {
            val damaged = e.entity as Player
            e.isCancelled = Admin.getAdmins().contains(damaged.uniqueId) && !Admin.getStaffMode().contains(damaged.uniqueId)
        }
    }

    @EventHandler
    private fun onDamageByEntity(e : EntityDamageByEntityEvent) {
        if(e.entity is Player) {
            val damaged = e.entity as Player
            e.isCancelled = Admin.getAdmins().contains(damaged.uniqueId) && !Admin.getStaffMode().contains(damaged.uniqueId)
        }
        if(e.damager is Player) {
            val damager = e.damager as Player
            e.isCancelled = Admin.getAdmins().contains(damager.uniqueId) && !Admin.getStaffMode().contains(damager.uniqueId)
        }
    }
}