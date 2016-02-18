package kotbot.wrapper

import kotbot.KotBot
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.IListener
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent
import sx.blah.discord.handle.impl.events.ReadyEvent

public class Discord4JWrapper(val email: String, val password: String) {
    
    val fileWatchThread: Thread
    var isReloading: Boolean = false
    var isCloseRequested: Boolean = false
    var currentClient: IDiscordClient
    var nextClient: IDiscordClient? = null
    
    init {
        try {
            currentClient = createClient()
        } catch(e: Exception) {
            KotBot.LOGGER.error("Error initializing client.", e)
            throw RuntimeException("Unable to launch client")
        }

        fileWatchThread = Thread(ModuleChangeWatcher())
        fileWatchThread.start()
    }
    
    fun reload() {
        KotBot.LOGGER.info("Preparing for restart...")
        isReloading = true
        
        try {
            nextClient = createClient()
            currentClient.logout()
        } catch (e: Exception) {
            KotBot.LOGGER.error("Error initializing new client, aborting reload.", e)
            isReloading = false
            nextClient = null
        }
    }
    
    fun close() {
        KotBot.LOGGER.info("Shutting down...")
        isCloseRequested = true
        currentClient.logout()
        nextClient?.logout()
        fileWatchThread.interrupt()
    }
    
    private fun createClient(): IDiscordClient {
        val client = ClientBuilder().withLogin(email, password).login()
        client.dispatcher.registerListener(IListener<DiscordDisconnectedEvent> {event -> 
            if (isReloading) {
                if (client === currentClient) {
                    KotBot.LOGGER.info("Initial client shut down, completing reload...")
                    currentClient = nextClient!!
                } else {
                    KotBot.LOGGER.warn("Error initializing new client, aborting reload.")
                    isReloading = false
                }
            } else {
                if (!isCloseRequested) {
                    KotBot.LOGGER.warn("Bot unexpectedly shut down for reason ${event.reason}! Attempting reload...")
                    reload()
                } else {
                    KotBot.LOGGER.info("Successfully shut down client.")
                }
            }
        })
        client.dispatcher.registerListener(IListener<ReadyEvent> {event -> 
            if (isReloading) {
                if (client === nextClient) {
                    KotBot.LOGGER.info("Secondary client completed loading.")
                    nextClient = null
                    isReloading = false
                    KotBot.INSTANCE.onReady(event)
                } else {
                    KotBot.LOGGER.error("Umm, what just happened? Tell the author ASAP!")
                }
            } else {
                KotBot.LOGGER.info("Completed initial client initialization, logged in as ${client.ourUser.name}")
                KotBot.INSTANCE.onReady(event)
            }
        })
        return client
    }
}
