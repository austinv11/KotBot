package kotbot.configuration

import com.google.gson.Gson
import java.io.File
import java.util.*
import kotlin.reflect.jvm.javaType
import kotlin.reflect.memberProperties

/**
 * This provides extension functions for Gson allowing for fields to be (un)serialized with comments, useful for configs.
 */

inline fun <reified T> Gson.fromJsonWithComments(file: File): T? {
    val originalContents = file.readLines()
    val cleanedContents = originalContents.filter { line -> return@filter !line.trim().startsWith("#") }
    var joiner = StringJoiner("\n")
    cleanedContents.forEach { line -> joiner.add(line) }
    return this.fromJson(joiner.toString(), Class.forName(T::class.qualifiedName) as Class<T>)
} 
    
fun Gson.toJsonWithComments(thing: Any): String {
    val json: String = this.toJson(thing)
    val joiner: StringJoiner = StringJoiner("\n")
    json.lines().forEach { line -> 
        if (line.startsWith("  ") && !line.startsWith("    ") && line.contains("\"")) { //Dammit gson, use tabs, not spaces
            val comment: String? = getCommentForField(line.split('"')[1], thing)
            if (comment != null)
                joiner.add("  # "+comment)
        }
        joiner.add(line)
    }
    return joiner.toString()
}

private fun getCommentForField(field: String, toSearch: Any): String? {
    try {
        val property = toSearch.javaClass.kotlin.memberProperties.find { property -> property.name == field }
        val annotation = property?.annotations?.find { annotation -> annotation is Comment }
        if (annotation != null)
            return "${(annotation as Comment).comment} (${property?.returnType?.javaType?.typeName?.split(".")?.last()})"
    } catch(e: NoSuchFieldException) {}
    
    return null
}
