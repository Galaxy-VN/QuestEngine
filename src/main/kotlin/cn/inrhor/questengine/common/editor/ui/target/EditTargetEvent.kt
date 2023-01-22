package cn.inrhor.questengine.common.editor.ui.target

import cn.inrhor.questengine.api.quest.QuestFrame
import cn.inrhor.questengine.api.quest.TargetFrame
import cn.inrhor.questengine.api.target.RegisterTarget.getNodeList
import cn.inrhor.questengine.api.target.RegisterTarget.saveTarget
import cn.inrhor.questengine.api.target.TargetNode
import cn.inrhor.questengine.api.target.TargetNodeType
import cn.inrhor.questengine.api.target.TargetStorage
import cn.inrhor.questengine.common.editor.ui.EditHome.addButton
import cn.inrhor.questengine.common.editor.ui.EditHome.pageItem
import cn.inrhor.questengine.common.editor.ui.EditTarget
import cn.inrhor.questengine.common.quest.manager.QuestManager.saveFile
import cn.inrhor.questengine.script.kether.evalStringList
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.asLangText
import taboolib.platform.util.asLangTextList
import taboolib.platform.util.buildItem

object EditTargetEvent {

    fun open(player: Player, questFrame: QuestFrame, targetFrame: TargetFrame) {
        val id = questFrame.id
        val t = targetFrame.id
        player.openMenu<Linked<TargetNode>>(player.asLangText("EDIT_UI_TARGET_EVENT_NODE")) {
            rows(6)
            addButton(player, 8, XMaterial.ARROW, "EDIT_BACK_TARGET_EDIT", id, t) {
                EditTarget.open(player, questFrame, targetFrame)
            }
            addButton(player, 11, XMaterial.NETHER_STAR, "EDIT_TARGET_EVENT_CHANGE", id, t) {
                openSelect(player, questFrame, targetFrame)
            }
            slots(
                listOf(
                    12, 13, 14, 15,
                    20, 21, 22, 23, 24,
                    29, 30, 31, 32, 33
                )
            )
            elements { getNodeList(targetFrame.event) }
            onGenerate { player, element, _, _ ->
                val node = element.node
                val value = targetFrame.nodeMeta(node)
                val lang: List<String> = if (element.nodeType == TargetNodeType.LIST) {
                    val list = mutableListOf<String>()
                    player.asLangTextList("EDIT_EVENT_NODE_${node.uppercase()}").forEach {
                        info("list   $it  check  "+(it == "__List__"))
                        if (it == "__List__") {
                            list.addAll(value)
                        } else {
                            list.add(it)
                        }
                    }
                    list
                }else {
                    player.asLangTextList("EDIT_EVENT_NODE_${node.uppercase()}", value)
                }
                buildItem(element.material) {
                    name = "§f                                        "
                    lore.addAll(lang)
                }
            }
            pageItem(player)
        }
    }

    private fun openSelect(player: Player, questFrame: QuestFrame, targetFrame: TargetFrame) {
        val id = questFrame.id
        val t = targetFrame.id
        player.openMenu<Linked<TargetStorage>>(player.asLangText("EDIT_UI_TARGET_EVENT_LIST")) {
            rows(6)
            slots(
                listOf(
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34
                )
            )
            addButton(player, 8, XMaterial.ARROW, "EDIT_BACK_EVENT_EDIT", id, t) {
                open(player, questFrame, targetFrame)
            }
            elements { saveTarget }
            onGenerate { _, element, _, _ ->
                buildItem(element.material) {
                    name = "§f                                        "
                    lore.addAll(player.evalStringList(player.asLangTextList("EDIT_TARGET_SELECT_EVENT",
                        element.lang(player))) {
                        it.rootFrame().variables().set("@QenQuestID", id)
                        it.rootFrame().variables().set("@QenTargetID", t)
                    })
                }
            }
            onClick { _, element ->
                targetFrame.event = element.name
                targetFrame.node = ""
                questFrame.saveFile()
                open(player, questFrame, targetFrame)
            }
            pageItem(player)
        }
    }

}