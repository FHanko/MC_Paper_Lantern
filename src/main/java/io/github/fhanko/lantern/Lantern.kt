package io.github.fhanko.lantern

import com.jeff_media.customblockdata.CustomBlockData
import com.jeff_media.customblockdata.events.CustomBlockDataRemoveEvent
import io.github.fhanko.kplugin.KPlugin
import io.github.fhanko.kplugin.blocks.BlockBase
import io.github.fhanko.kplugin.util.Schedulable
import io.github.fhanko.kplugin.util.copyPdc
import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class Lantern : JavaPlugin(), Listener {
    override fun onEnable() {
        KPlugin.initialize(this)
        getCommand("lantern")?.setExecutor(LanternCommand())
        Bukkit.getPluginManager().registerEvents(this, this)

        ConfigurationSerialization.registerClass(LanternBlock::class.java)
        saveDefaultConfig()

        val lanterns = config.get("lanterns") as List<MutableMap<String, Any>>
        lanterns.forEach { LanternBlock(it) }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        val lightPower = BlockBase.readBlock(e.block, LIGHT_KEY, PersistentDataType.INTEGER) ?: return
        Schedulable.nextTick({_ -> Util.placeLight(e.block.location, lightPower) })
    }

    @EventHandler
    fun onDestroy(e: CustomBlockDataRemoveEvent) {
        val data = CustomBlockData(e.block, KPlugin.instance)
        if (!data.has(LIGHT_KEY)) return
        Schedulable.nextTick({ _ -> copyPdc(data, CustomBlockData(e.block.world.getBlockAt(e.block.location), KPlugin.instance)) })
    }
}
