package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.events.ScreenEvent
import net.ccbluex.liquidbounce.event.repeatable
import net.ccbluex.liquidbounce.event.sequenceHandler
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.utils.client.MovePacketType
import net.ccbluex.liquidbounce.utils.client.Timer
import net.ccbluex.liquidbounce.utils.entity.moving
import net.ccbluex.liquidbounce.utils.kotlin.Priority
import net.minecraft.client.gui.screen.DeathScreen
import net.minecraft.entity.effect.StatusEffects

/**
 * AutoResistance module
 *
 * Automatically give the player resistance effect.
 */
object ModuleAutoResistance : Module("AutoResistance", Category.PLAYER) {

    private val health by int("Health", 16, 0..20)
    private val speed by int("Speed", 20, 1..35)
    private val timer by float("Timer", 0.5f, 0.1f..10f)

    private val notInTheAir by boolean("NotInTheAir", true)
    private val notDuringMove by boolean("NotDuringMove", false)
    private val notDuringRegeneration by boolean("NotDuringRegeneration", false)
    private val doNotCauseHunger by boolean("DoNotCauseHunger", false)

    private val packetType by enumChoice("PacketType", MovePacketType.FULL)

    val repeatable = repeatable {
        if (player.abilities.creativeMode || player.isDead || player.health > health) {
            return@repeatable
        }

        if (notInTheAir && !player.isOnGround) {
            return@repeatable
        }

        if (notDuringMove && player.moving) {
            return@repeatable
        }

        if (notDuringRegeneration && player.hasStatusEffect(StatusEffects.RESISTANCE)) {
            return@repeatable
        }

        if (doNotCauseHunger && player.hungerManager.foodLevel < 20) {
            return@repeatable
        }

        Timer.requestTimerSpeed(timer, Priority.IMPORTANT_FOR_USAGE_1, this@ModuleAutoResistance)

        repeat(speed) {
            network.sendPacket(packetType.generatePacket())
        }
    }
}
