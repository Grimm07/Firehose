package com.example

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent.Record
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

data class LogEntry(
    val timestamp: String,
    val eventType: String,
    val message: String,
    val userId: String?
)

@Serializable
data class LambdaOutputRecord(val data: String)

class Handler : RequestHandler<KinesisFirehoseEvent, List<LambdaOutputRecord>> {
    override fun handleRequest(event: KinesisFirehoseEvent, context: Context): List<LambdaOutputRecord> {
        return event.records.map { record ->
            val payload = String(record.data.array()) // Base64-encoded log
            val logEntry = processLog(payload)
            val jsonLog = Json.encodeToString(logEntry)  // Convert LogEntry to JSON
            LambdaOutputRecord(data = jsonLog)
        }
    }

    private fun processLog(payload: String): LogEntry {
        // Example of transforming log into structured data
        return LogEntry(
            timestamp = System.currentTimeMillis().toString(),
            eventType = "INFO",
            message = payload,
            userId = "user123" // This could be extracted from the payload
        )
    }
}
