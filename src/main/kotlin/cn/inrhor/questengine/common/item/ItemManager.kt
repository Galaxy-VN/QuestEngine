package cn.inrhor.questengine.common.item

import cn.inrhor.questengine.utlis.file.FileUtil
import cn.inrhor.questengine.utlis.UtilString
import taboolib.common.platform.function.console
import taboolib.module.configuration.Configuration
import taboolib.module.lang.sendLang
import java.io.File

object ItemManager {
    /**
     * 成功注册的物品
     */
    private var itemFileMap = mutableMapOf<String, ItemFile>()

    /**
     * 注册物品
     */
    fun register(itemID: String, itemFile: ItemFile) {
        if (exist(itemID)) {
            console().sendLang("ITEM.EXIST_ITEM_ID", UtilString.pluginTag, itemID)
            return
        }
        itemFileMap[itemID] = itemFile
    }

    /**
     * 加载并注册物品文件
     */
    fun loadItem() {
        val itemFolder = FileUtil.getFile("space/item/", "ITEM-NO_FILES", true, "example")
        FileUtil.getFileList(itemFolder).forEach{
            checkRegItem(it)
        }
    }

    /**
     * 检查和注册物品
     */
    private fun checkRegItem(file: File) {
        val yaml = Configuration.loadFromFile(file)
        if (yaml.getKeys(false).isEmpty()) {
            console().sendLang("ITEM.EMPTY_CONTENT", UtilString.pluginTag, file.name)
            return
        }
        yaml.getKeys(false).forEach {
            ItemFile(it, yaml)
        }
    }

    /**
     * 物品ID 是否存在
     */
    fun exist(itemID: String) = itemFileMap.contains(itemID)

    /**
     * 获取物品
     */
    fun get(itemID: String) = (itemFileMap[itemID]?: error("unknown item")).itemStack

    fun clearMap() = itemFileMap.clear()
}