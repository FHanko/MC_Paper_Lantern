package io.github.fhanko.lantern

import com.jeff_media.customblockdata.CustomBlockData
import com.jeff_media.customblockdata.events.CustomBlockDataRemoveEvent
import io.github.fhanko.kplugin.KPlugin
import io.github.fhanko.kplugin.util.Scheduler
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class Lantern : JavaPlugin(), Listener {
    override fun onEnable() {
        KPlugin.initialize(this)
        Bukkit.getPluginManager().registerEvents(this, this)
        getCommand("lantern")?.setExecutor(LanternCommand())

        ConfigurationSerialization.registerClass(LanternBlock::class.java)
        saveDefaultConfig()

        val lanterns = config.get("lanterns") as List<MutableMap<String, Any>>
        lanterns.forEach { LanternBlock(it) }

        val metrics = Metrics(this, 20354)
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        val lightPower = LanternBlock.lightData.getBlock(e.block) ?: return
        Scheduler.nextTick({ _ -> LanternBlock.placeLight(e.block.location, lightPower) })
    }

    @EventHandler
    fun onDestroy(e: CustomBlockDataRemoveEvent) {
        val lightPower = LanternBlock.lightData.getBlock(e.block) ?: return
        Scheduler.nextTick({ _ -> LanternBlock.lightData.setBlock(e.block.world.getBlockAt(e.block.location), lightPower) })
    }
}
