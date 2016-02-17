package kotbot.configuration

class Config {
    
    @Comment("This is the email for the discord account to use. NOTE: if email or password are left null, KotBot will take account credentials through command parameters")
    var email: String? = null
    
    @Comment("This is the password for the discord account to use. NOTE: if email or password are left null, KotBot will take account credentials through command parameters")
    var password: String? = null
}
