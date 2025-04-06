import java.util.Base64

class LogAppender(private val lambdaLogger: LambdaLogger) : Logger {
    private val cloudWatchLogger = CloudWatchLogger(lambdaLogger)

    override fun info(message: String, vararg args: Any?) {
        val logEvent = LogEvent(
            timestamp = System.currentTimeMillis().toString(),
            level = "INFO",
            message = message.format(*args),
            userId = MDC.get("userId"),
            requestId = MDC.get("requestId"),
            additionalData = emptyMap() // Add any extra data as needed
        )
        val formattedLog = formatLogEvent(logEvent)
        cloudWatchLogger.info(formattedLog)
        sendLogToFirehose(formattedLog) // If sending logs to Firehose or OpenSearch
    }

    override fun debug(message: String, vararg args: Any?) {
        val logEvent = LogEvent(
            timestamp = System.currentTimeMillis().toString(),
            level = "DEBUG",
            message = message.format(*args),
            userId = MDC.get("userId"),
            requestId = MDC.get("requestId"),
            additionalData = emptyMap()
        )
        val formattedLog = formatLogEvent(logEvent)
        cloudWatchLogger.debug(formattedLog)
        sendLogToFirehose(formattedLog)
    }

    override fun warn(message: String, vararg args: Any?) {
        val logEvent = LogEvent(
            timestamp = System.currentTimeMillis().toString(),
            level = "WARN",
            message = message.format(*args),
            userId = MDC.get("userId"),
            requestId = MDC.get("requestId"),
            additionalData = emptyMap()
        )
        val formattedLog = formatLogEvent(logEvent)
        cloudWatchLogger.warn(formattedLog)
        sendLogToFirehose(formattedLog)
    }

    override fun error(message: String, vararg args: Any?) {
        val logEvent = LogEvent(
            timestamp = System.currentTimeMillis().toString(),
            level = "ERROR",
            message = message.format(*args),
            userId = MDC.get("userId"),
            requestId = MDC.get("requestId"),
            additionalData = emptyMap()
        )
        val formattedLog = formatLogEvent(logEvent)
        cloudWatchLogger.error(formattedLog)
        sendLogToFirehose(formattedLog)
    }

    private fun sendLogToFirehose(log: String) {
        // Here you would implement the logic to send the log to OpenSearch via Firehose
        // This could involve base64 encoding and using the AWS SDK for Kinesis Firehose
        val base64EncodedLog = Base64.getEncoder().encodeToString(log.toByteArray())
        // Send base64EncodedLog to Firehose...
    }
}
