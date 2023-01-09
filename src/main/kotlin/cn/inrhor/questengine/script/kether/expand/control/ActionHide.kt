package cn.inrhor.questengine.script.kether.expand.control

import cn.inrhor.questengine.QuestEngine
import cn.inrhor.questengine.script.kether.frameVoid
import cn.inrhor.questengine.script.kether.player
import org.bukkit.Bukkit
import taboolib.module.kether.*
import taboolib.module.nms.MinecraftVersion
import java.util.concurrent.CompletableFuture

class ActionHide(val hide: Boolean): ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val player = frame.player()
        Bukkit.getOnlinePlayers().forEach {
            if (hide) {
                if (MinecraftVersion.majorLegacy >= 11300) {
                    it.hidePlayer(QuestEngine.plugin, player)
                } else {
                    it.hidePlayer(player)
                }
            }else {
                if (MinecraftVersion.majorLegacy >= 11300) {
                    it.showPlayer(QuestEngine.plugin, player)
                } else {
                    it.showPlayer(player)
                }
            }
        }
        return frameVoid()
    }

    /**
     * hide player set (true/false)
     */
    companion object {
        @KetherParser(["hide"], shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("player") {
                    it.mark()
                    it.expect("set")
                    val set = it.nextToken()=="true"
                    ActionHide(set)
                }
            }
        }
    }
}