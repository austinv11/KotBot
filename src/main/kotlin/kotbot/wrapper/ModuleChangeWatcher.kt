package kotbot.wrapper

import kotbot.KotBot
import java.io.File
import java.nio.file.*
import java.util.*

/**
 * This waits for a change in ./modules and when found waits `ModuleChangeWatcher.TIME_TO_WAIT_FOR_RELOAD` seconds to 
 * reload the bot.
 */
class ModuleChangeWatcher : Runnable {
    
    val watcher: WatchService = FileSystems.getDefault().newWatchService()
    var reloadQueued: Boolean = false
    
    constructor() {
        if (!MODULE_DIR.exists())
            MODULE_DIR.mkdir()
        
        if (!MODULE_DIR.exists() && !MODULE_DIR.isDirectory)
            throw NoSuchFileException("File ${MODULE_DIR.name} must be a directory!")
        
        val dir: Path = MODULE_DIR.toPath()
        dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, 
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.OVERFLOW)
    }
    
    companion object {
        val MODULE_DIR: File = File("./modules")
        const val TIME_TO_WAIT_FOR_RELOAD: Int = 10 //Time in seconds
    }
    
    override fun run() {
        while (true) {
            val key: WatchKey = watcher.take()
            
            for (event: WatchEvent<*> in key.pollEvents()) {
                val kind: WatchEvent.Kind<*> = event.kind()
                val pathEvent: WatchEvent<Path> = event as WatchEvent<Path>
                KotBot.LOGGER.trace("File watcher update: ${kind.name()} in ${pathEvent.context().fileName}")
                if (!reloadQueued && !KotBot.WRAPPER!!.isReloading) {
                    KotBot.LOGGER.info("File change! Reloading bot in $TIME_TO_WAIT_FOR_RELOAD seconds...")
                    reloadQueued = true
                    Timer().schedule(object: TimerTask() {
                        override fun run() {
                            KotBot.WRAPPER!!.reload()
                            reloadQueued = false
                        }
                    }, (TIME_TO_WAIT_FOR_RELOAD * 1000).toLong())
                }
            }
            
            if (!key.reset())
                break
        }
    }
}
