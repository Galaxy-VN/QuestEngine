package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.api.manager.DataManager.teamData
import org.bukkit.Bukkit
import org.bukkit.entity.Player

enum class QueueType {
    ACCEPT, FINISH, FAIL, QUIT, RESET, TRACK
}

enum class SelectObject {
    SELF {
        override fun objective(player: Player): Set<Player> {
            return setOf(player)
        }
         },
    TEAM {
        override fun objective(player: Player): Set<Player> {
            return player.teamData()?.playerMembers()?: setOf()
        }
         },
    ALL {
        override fun objective(player: Player): Set<Player> {
            return Bukkit.getOnlinePlayers().toSet()
        }
    };

    abstract fun objective(player: Player): Set<Player>
}