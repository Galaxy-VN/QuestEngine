package cn.inrhor.questengine.script.kether.expand

import cn.inrhor.questengine.common.database.data.DataStorage.getPlayerData
import cn.inrhor.questengine.common.nav.NavData
import cn.inrhor.questengine.script.kether.frameVoid
import cn.inrhor.questengine.script.kether.player
import cn.inrhor.questengine.script.kether.selectNavID
import org.bukkit.Location
import taboolib.common.platform.ProxyParticle
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionNavigation {

    class Create(val location: ParsedAction<*>): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            return frame.newFrame(location).run<Location>().thenAccept {
                frame.player().uniqueId.getPlayerData().navData[frame.selectNavID()] = NavData(it)
            }
        }
    }

    class Nav(val state: NavData.State, val effect: ProxyParticle = ProxyParticle.VILLAGER_HAPPY): ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val sender = frame.player()
            val id = frame.selectNavID()
            val data = sender.uniqueId.getPlayerData()
            val nav = data.navData
            if (nav.containsKey(id)) {
                val n = data.navData[id]!!
                when (state) {
                    NavData.State.START -> n.start(sender, effect)
                    NavData.State.STOP -> n.stop()
                    else -> {
                        n.stop()
                        nav.remove(id)
                    }
                }
            }
            return frameVoid()
        }
    }

    companion object {
        @KetherParser(["nav"], shared = true)
        fun parser() = scriptParser {
            it.switch {
                case("select") {
                    val action = it.next(ArgTypes.ACTION)
                    actionNow {
                        newFrame(action).run<Any>().thenAccept { a ->
                            variables().set("@QenNavID", a.toString())
                        }
                    }
                }
                case("create") {
                    it.mark()
                    it.expect("target")
                    Create(it.next(ArgTypes.ACTION))
                }
                case("set") {
                    when (val state = NavData.State.valueOf(it.nextToken().uppercase())) {
                        NavData.State.START -> {
                            val effect = try {
                                it.mark()
                                it.expect("display")
                                ProxyParticle.valueOf(it.nextToken().uppercase())
                            }catch (ex: Exception) {
                                it.reset()
                                ProxyParticle.VILLAGER_HAPPY
                            }
                            Nav(state, effect)
                        }
                        else -> {
                            Nav(state)
                        }
                    }
                }
                case("stopAll") {
                    actionNow {
                        val data = player().uniqueId.getPlayerData()
                        data.navData.forEach { (_, v) ->
                            v.stop()
                        }
                    }
                }
            }
        }
    }

}