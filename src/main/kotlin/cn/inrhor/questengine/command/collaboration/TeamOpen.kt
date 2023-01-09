package cn.inrhor.questengine.command.collaboration

import cn.inrhor.questengine.common.collaboration.TeamManager
import cn.inrhor.questengine.common.collaboration.ui.chat.HasTeam
import cn.inrhor.questengine.common.collaboration.ui.chat.NoTeam
import org.bukkit.entity.Player
import taboolib.common.platform.command.*

object TeamOpen {

    val open = subCommand {
        execute<Player> { sender, _, _ ->
            val pUUID = sender.uniqueId
            if (TeamManager.hasTeam(pUUID)) {
                HasTeam.openInfo(sender)
                return@execute
            }
            NoTeam.openHome(sender)
        }
    }
}