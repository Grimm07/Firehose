import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class Slf4jLogger(val logger: org.slf4j.Logger) : Logger {
    override fun log(eventType: EventType, message: String, vararg args: Any?) {
        val formattedMessage = "[${eventType.name}] $message"
        when (eventType) {
            EventType.INFO -> logger.info(formattedMessage, *args)
            EventType.DEBUG -> logger.debug(formattedMessage, *args)
            EventType.WARN -> logger.warn(formattedMessage, *args)
            EventType.ERROR -> logger.error(formattedMessage, *args)
            EventType.TRACE -> logger.trace(formattedMessage, *args)
        }
    }
}

@Serializable
data class LogEntry(
    val timestamp: String,
    val eventType: EventType,
    val message: String,
    val userId: String? = null,
    val requestId: String? = null
)

fun Slf4jLogger.logStructured(eventType: EventType, message: String, userId: String? = null, requestId: String? = null) {
    val logEntry = LogEntry(
        timestamp = System.currentTimeMillis().toString(),
        eventType = eventType,
        message = message,
        userId = userId,
        requestId = requestId
    )
    val jsonLog = Json.encodeToString(logEntry)
    when (eventType) {
        EventType.INFO -> logger.info(jsonLog)
        EventType.DEBUG -> logger.debug(jsonLog)
        EventType.WARN -> logger.warn(jsonLog)
        EventType.ERROR -> logger.error(jsonLog)
        EventType.TRACE -> logger.trace(jsonLog)
    }
}
