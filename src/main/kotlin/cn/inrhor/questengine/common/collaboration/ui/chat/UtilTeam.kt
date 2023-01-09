package cn.inrhor.questengine.common.collaboration.ui.chat

import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.utlis.toJsonStr
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.module.configuration.Configuration

object UtilTeam {

    fun getStr(yaml: Configuration, node: String, tData: TeamOpen, leaderName: String): String {
        return getStr(yaml, node, tData, leaderName, "%memberName%")
    }

    fun getStr(yaml: Configuration, node: String, tData: TeamOpen, leaderName: String, memberName: String): String {
        var content = ""
        yaml.getStringList(node).forEach {
            if (content.isEmpty()) {
                content = it
                return@forEach
            }
            content = "$content&r\n$it"
        }
        return content.replace("%teamName%", tData.teamName, true)
            .replace("%teamAmount%", tData.members.size.toString(), true)
            .replace("%leaderName%", leaderName, true)
            .replace("%memberName%", memberName, true)
    }

    fun getStr(yaml: Configuration, node: String): String {
        return yaml.getStringList(node).toJsonStr()
    }

    fun leaderName(player: Player, tData: TeamOpen): String {
        var leaderName = player.name
        if (tData.leader != player.uniqueId) {
            val leader = Bukkit.getPlayer(tData.leader)?: return ""
            leaderName = leader.name
        }
        return leaderName
    }

}