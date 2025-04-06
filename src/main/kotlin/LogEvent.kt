import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class LogEvent(
    val timestamp: String,
    val level: String,
    val message: String,
    val userId: String?,
    val requestId: String?,
    val additionalData: Map<String, String>?
)

fun formatLogEvent(logEvent: LogEvent): String {
    return Json.encodeToString(logEvent)
}
