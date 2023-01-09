package cn.inrhor.questengine.common.dialog.theme.hologram.core

import cn.inrhor.questengine.api.dialog.DialogModule
import cn.inrhor.questengine.api.dialog.theme.DialogTheme
import cn.inrhor.questengine.api.dialog.ReplyModule
import cn.inrhor.questengine.api.hologram.HoloIDManager
import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.dialog.theme.hologram.HologramData
import cn.inrhor.questengine.common.dialog.theme.hologram.OriginLocation
import cn.inrhor.questengine.common.dialog.theme.hologram.content.AnimationItem
import cn.inrhor.questengine.common.dialog.theme.hologram.content.AnimationText
import cn.inrhor.questengine.common.dialog.theme.hologram.parserOrigin
import cn.inrhor.questengine.utlis.spaceSplit
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * 全息对话
 */
class DialogHologram(
    override val dialogModule: DialogModule,
    override val npcLoc: Location,
    override val viewers: MutableSet<Player>): DialogTheme() {

    val origin = OriginLocation(npcLoc)
    val replyHoloList = mutableListOf<ReplyHologram>()
    val holoData = HologramData()

    var end = false

    override fun play() {
        parserContent()
        viewers.forEach {
            val pData = it.getPlayerData()
            pData.dialogData.addDialog(dialogModule.dialogID, this)
        }
    }

    override fun end() {
        viewers.forEach {
            val pData = it.getPlayerData()
            pData.flagsDialog.clear()
        }
        end = true
        holoData.remove(viewers)
    }

    override fun addViewer(viewer: Player) {
        viewers.add(viewer)
    }

    override fun deleteViewer(viewer: Player) {
        viewers.remove(viewer)
    }

    /**
     * 解析对话内容
     */
    private fun parserContent() {
        dialogModule.dialog.forEach {
            val u = it.lowercase()
            if (u.startsWith("text")) {
                textViewers(it)
            }else if (u.startsWith("item ")) {
                itemViewers(it)
            }else if (u.startsWith("replyall ") || u.startsWith("reply ")) {
                val replyList = if (u.startsWith("replyall")) dialogModule.reply else
                    getReply((it.spaceSplit(2)))
                val reply = ReplyHologram(
                    this, replyList,
                    it.spaceSplit(1).toLong())
                reply.play()
                replyHoloList.add(reply)
            }else {
                parserOrigin(origin, it)
            }
        }
    }

    private fun getReply(replyID: String): MutableList<ReplyModule> {
        dialogModule.reply.forEach {
            if (it.replyID == replyID) return mutableListOf(it)
        }
        return mutableListOf()
    }


    /**
     * 向 viewer 发送文本
     */
    private fun textViewers(text: String) {
        val type = HoloIDManager.Type.TEXT
        val holoID = HoloIDManager.generate(
            dialogModule.dialogID, holoData.size(), type)
        holoData.create(holoID, viewers, origin, type)
        val animation = AnimationText(text)
        animation.sendViewers(holoID, this)
    }

    /**
     * 向 viewers 发送物品
     */
    private fun itemViewers(content: String) {
        val dialogID = dialogModule.dialogID
        val index = holoData.size()
        val type = HoloIDManager.Type.ITEM
        val itemHoloID = HoloIDManager.generate(
            dialogID, index, type)
        val stackHoloID = HoloIDManager.generate(
            dialogID, index+1, HoloIDManager.Type.ITEMSTACK)
        holoData.create(itemHoloID, viewers, origin, type)
        val animation = AnimationItem(content, holoData)
        animation.sendViewers(this, origin, itemHoloID, stackHoloID)
    }

}