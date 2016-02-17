@file:JvmName("StaticLoggerBinder")

package org.slf4j.impl

import kotbot.logging.LoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

/**
 * This is a dummy StaticLoggerBinder to get slf4j to recognize my custom LoggerAdapter
 */
public class StaticLoggerBinder : LoggerFactoryBinder {
    
    companion object {
        const val REQUESTED_API_VERSION: String = "1.6.99" //Required field
        
        @JvmStatic
        fun getSingleton() : StaticLoggerBinder = StaticLoggerBinder() //Required function
    }
    
    override fun getLoggerFactory() = LoggerFactory()

    override fun getLoggerFactoryClassStr() = LoggerFactory::class.java.name;
}
