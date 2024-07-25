package io.github.karmasmp.karma.event

import io.github.karmasmp.karma.player.admin.Admin.isAdmin
import io.github.karmasmp.karma.player.admin.Admin.isInStaffMode

import org.bukkit.entity.AreaEffectCloud
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
            if(damaged.vehicle != null) {
                if(damaged.vehicle is AreaEffectCloud) {
                    e.isCancelled = true
                }
            }
            if(damaged.isAdmin() && !damaged.isInStaffMode()) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    private fun onDamageByEntity(e : EntityDamageByEntityEvent) {
        if(e.entity is Player) {
            val damaged = e.entity as Player
            if(damaged.vehicle != null) {
                if(damaged.vehicle is AreaEffectCloud) {
                    e.isCancelled = true
                }
            }
            if(damaged.isAdmin() && !damaged.isInStaffMode()) {
                e.isCancelled = true
            }
        }
        if(e.damager is Player) {
            val damager = e.damager as Player
            if(damager.vehicle != null) {
                if(damager.vehicle is AreaEffectCloud) {
                    e.isCancelled = true
                }
            }
            if(damager.isAdmin() && !damager.isInStaffMode()) {
                e.isCancelled = true
            }
        }
    }
}