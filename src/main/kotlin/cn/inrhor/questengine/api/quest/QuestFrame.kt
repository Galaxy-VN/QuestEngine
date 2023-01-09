package cn.inrhor.questengine.api.quest

import cn.inrhor.questengine.common.database.data.quest.TargetData
import cn.inrhor.questengine.api.manager.DataManager.teamData
import cn.inrhor.questengine.common.quest.enum.ModeType
import cn.inrhor.questengine.script.kether.runEvalSet
import cn.inrhor.questengine.utlis.time.noTimeout
import org.bukkit.entity.Player
import java.util.*

data class QuestFrame(
    var id: String = "unknownID", var name: String = "", var note: List<String> = listOf(),
    val accept: AcceptAddon = AcceptAddon(),
    val time: TimeAddon = TimeAddon(),
    val mode: ModeAddon = ModeAddon(),
    val target: MutableList<TargetFrame> = mutableListOf(),
    val control: MutableList<ControlFrame> = mutableListOf(),
    var data: List<String> = listOf(),
    @Transient var path: String = "") {

    /**
     * @return 返回新的任务数据
     */
    fun newTargetsData(): MutableList<TargetData> {
        val list = mutableListOf<TargetData>()
        target.forEach {
            list.add(TargetData(id, it))
        }
        return list
    }

    /**
     * @return 任务脚本是否在允许的时间
     */
    fun allowTime(): Boolean {
        val endDate = time.endDate?: return true
        return Date().noTimeout(time.timeDate, endDate)
    }

    /**
     * 运行脚本
     * 在协同模式中，应当只由队长执行
     */
    fun runEval(player: Player, queueType: QueueType) {
        if (mode.type == ModeType.COLLABORATION && player.teamData()?.isLeader(player) == false) {
            return
        }
        control.forEach {
            if (it.type == queueType) {
                runEvalSet(it.select.objective(player), it.script) { s ->
                    s.rootFrame().variables()["@QenQuestID"] = id
                }
            }
        }
    }

    fun delTarget(targetID: String) {
        val i = target.iterator()
        while (i.hasNext()) {
            val n = i.next()
            if (n.id== targetID) {
                i.remove()
                break
            }
        }
    }

    fun existTargetID(targetID: String): Boolean {
        target.forEach { if (it.id == targetID) return true }
        return false
    }

}