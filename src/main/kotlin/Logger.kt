

import kotlinx.serialization.encodeToString
import mu.KotlinLogging
interface Logger {
    fun log(eventType: EventType, message: String, vararg args: Any?)
}
// Extend KotlinLogging with EventType
class ExtendedLogger(private val logger: mu.KLogger) {

    fun log(eventType: EventType, message: String, vararg args: Any?) {
        val formattedMessage = "[${eventType.name}] $message"
        when (eventType) {
            EventType.INFO -> logger.info { formattedMessage.format(*args) }
            EventType.DEBUG -> logger.debug { formattedMessage.format(*args) }
            EventType.WARN -> logger.warn { formattedMessage.format(*args) }
            EventType.ERROR -> logger.error { formattedMessage.format(*args) }
            EventType.TRACE -> logger.trace { formattedMessage.format(*args) }
        }
    }

    // Optionally, add structured logging (e.g., JSON)
    fun logStructured(eventType: EventType, message: String, vararg args: Any?) {
        val structuredMessage = LogEntry(
            timestamp = System.currentTimeMillis().toString(),
            eventType = eventType,
            message = message,
            userId = "user123", // Example context
            requestId = "req-abc123" // Example context
        )
        val jsonLog = kotlinx.serialization.json.Json.encodeToString(structuredMessage)

        when (eventType) {
            EventType.INFO -> logger.info { jsonLog }
            EventType.DEBUG -> logger.debug { jsonLog }
            EventType.WARN -> logger.warn { jsonLog }
            EventType.ERROR -> logger.error { jsonLog }
            EventType.TRACE -> logger.trace { jsonLog }
        }
    }
}
