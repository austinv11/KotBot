package kotbot.configuration

class Config {
    
    @Comment("This is the email for the discord account to use. NOTE: if email or password are left null, KotBot will take account credentials through command parameters")
    var email: String? = null
    
    @Comment("This is the password for the discord account to use. NOTE: if email or password are left null, KotBot will take account credentials through command parameters")
    var password: String? = null
    
    @Comment("This is the prefix required to activate a command")
    var commandPrefix: String = "~"
    
    @Comment("This is the unique user id for the owner of this bot")
    var ownerID: String? = null
    
    @Comment("This is the array of channel ids which you want this bot to ignore")
    var blackListedChannels: Array<String> = emptyArray()
}
