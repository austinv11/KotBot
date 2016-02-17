package kotbot.wrapper

import kotbot.KotBot
import java.io.File
import java.nio.file.*

/**
 * This waits for a change in ./modules and when found waits `ModuleChangeWatcher.TIME_TO_WAIT_FOR_RELOAD` seconds to 
 * reload the bot.
 */
class ModuleChangeWatcher : Runnable {
    
    val watcher: WatchService = FileSystems.getDefault().newWatchService()
    var lastFileChangeTime: Long = -1;
    
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
            if (lastFileChangeTime != -1.toLong()) {
                if (System.currentTimeMillis()-(lastFileChangeTime) >= (TIME_TO_WAIT_FOR_RELOAD*1000) 
                        && !KotBot.isReloading) { //Don't want to deal with concurrent reload attempts
                    KotBot.onFileChange()
                    lastFileChangeTime = -1
                }
            }
            
            val key: WatchKey = watcher.take()
            
            for (event: WatchEvent<*> in key.pollEvents()) {
                val kind: WatchEvent.Kind<*> = event.kind()
                val pathEvent: WatchEvent<Path> = event as WatchEvent<Path>
                KotBot.LOGGER.trace("File watcher update: ${kind.name()} in ${pathEvent.context().fileName}")
                lastFileChangeTime = System.currentTimeMillis()
            }
            
            if (!key.reset())
                break
        }
    }
}
