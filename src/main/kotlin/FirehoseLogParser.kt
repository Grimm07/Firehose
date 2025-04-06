package com.example
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent
import com.amazonaws.services.lambda.runtime.events.KinesisFirehoseEvent.Record

class FirehoseLogParser : RequestHandler<KinesisFirehoseEvent, KinesisFirehoseEvent> {
    override fun handleRequest(event: KinesisFirehoseEvent, context: Context): KinesisFirehoseEvent {
        val records = event.records.map { record ->
            // Decode the base64-encoded record data
            val decodedData = String(record.data.array())

            // Parse the JSON log (you may use a library like Jackson or kotlinx.serialization)
            val logJson = parseJson(decodedData)

            // Optionally, you can enrich or transform the log before sending to OpenSearch
            val transformedLog = transformLogForOpenSearch(logJson)

            // Return the record, re-encoded in base64
            val transformedData = base64Encode(transformedLog)

            // Return the transformed record
            Record.builder()
                .data(transformedData)
                .build()
        }

        // Return the transformed records
        return KinesisFirehoseEvent(records)
    }

    private fun parseJson(logData: String): Map<String, Any> {
        // Use a JSON library like Jackson or kotlinx.serialization to parse the JSON log
        // Example with kotlinx.serialization:
        return Json.decodeFromString(Map::class, logData)
    }

    private fun transformLogForOpenSearch(logJson: Map<String, Any>): String {
        // Transform or enrich your log as needed for OpenSearch (e.g., adding a timestamp field)
        logJson["timestamp"] = logJson["timestamp"] ?: System.currentTimeMillis().toString()

        // You could add custom transformations or ensure fields are indexed properly.
        // For example, you could flatten nested objects, change field names, etc.

        return Json.encodeToString(logJson)
    }

    private fun base64Encode(data: String): ByteArray {
        return java.util.Base64.getEncoder().encode(data.toByteArray())
    }
}
