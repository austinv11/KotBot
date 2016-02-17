package kotbot.wrapper

import kotbot.KotBot

public data class Discord4JWrapper(val email: String, val password: String) {
    
    val fileWatchThread: Thread
    var isReloading: Boolean = false
    
    init {
        fileWatchThread = Thread(ModuleChangeWatcher())
        fileWatchThread.start()
    }
    
    fun reload() {
        isReloading = true
        KotBot.LOGGER.info("Module change detected! Preparing for restart.")
    }
}
