package io.github.fhanko.lantern

import io.github.fhanko.kplugin.items.ItemBase
import io.github.fhanko.kplugin.util.mm
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LanternCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (args?.size !in 0..2) return false
        if (args?.size == 0) {
            ItemBase.itemList.values.forEach {if (it !is LanternBlock) return@forEach
                val message = mm.deserialize("<blue>${it.id}<reset>: ${mm.serialize(it.name)}").
                    clickEvent(ClickEvent.runCommand("/lantern ${it.id}"))
                sender.sendMessage(message)
            }
            return true
        }
        if (args?.getOrNull(0)?.toIntOrNull() == null) return false
        val id = args[0].toInt()
        val amount = args.getOrNull(1)?.toIntOrNull()
        ItemBase.give(sender, id, amount ?: 1)
        return true
    }
}