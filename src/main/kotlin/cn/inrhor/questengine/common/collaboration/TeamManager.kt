package cn.inrhor.questengine.common.collaboration

import cn.inrhor.questengine.api.collaboration.TeamData
import cn.inrhor.questengine.api.collaboration.TeamOpen
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.database.data.PlayerData
import org.bukkit.entity.Player
import java.util.*

object TeamManager {

    /*
     *  根据队伍名称对应队伍
     */
    val teamsMap = mutableMapOf<String, TeamOpen>()

    fun hasTeam(pUUID: UUID): Boolean {
        return hasTeam(pUUID.getPlayerData())
    }

    fun hasTeam(pData: PlayerData): Boolean = pData.teamData != null

    fun getTeamData(teamName: String): TeamOpen? = teamsMap[teamName]

    fun getTeamData(pUUID: UUID): TeamOpen? {
        val pData = pUUID.getPlayerData()
        return pData.teamData
    }

    fun getTeamData(player: Player): TeamOpen? {
        return getTeamData(player.uniqueId)
    }

    fun isLeader(pUUID: UUID, teamName: String): Boolean {
        val teamData = getTeamData(teamName)?: return false
        return isLeader(pUUID, teamData)
    }

    fun isLeader(pUUID: UUID, teamData: TeamOpen): Boolean {
        return teamData.leader == pUUID
    }

    fun getMemberAmount(teamName: String): Int {
        val teamData = getTeamData(teamName)?: return 0
        return teamData.getAmount()
    }

    fun getMemberAmount(teamData: TeamOpen): Int {
        return teamData.getAmount()
    }

    fun createTeam(teamName: String, leader: UUID) {
        val pData = leader.getPlayerData()
        if (hasTeam(pData)) return
        if (teamsMap.containsKey(teamName)) return
        val teamData = TeamData(teamName, leader)
        pData.teamData = teamData
        teamsMap[teamName] = teamData
    }

    fun addMember(mUUID: UUID, teamData: TeamOpen) {
        if (teamData.members.contains(mUUID)) return
        teamData.members.add(mUUID)
        removeAsk(mUUID, teamData)
    }

    fun removeMember(mUUID: UUID, teamData: TeamOpen) {
        if (!teamData.members.contains(mUUID)) return
        teamData.members.remove(mUUID)
        val mData = mUUID.getPlayerData()
        mData.teamData = null
    }

    fun removeAsk(aUUID: UUID, teamData: TeamOpen) {
        teamData.asks.remove(aUUID)
    }

    fun addAsk(aUUID: UUID, teamData: TeamOpen) {
        teamData.asks.add(aUUID)
    }

}