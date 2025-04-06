import com.amazonaws.services.lambda.runtime.LambdaLogger

class CloudWatchLogger(private val lambdaLogger: LambdaLogger) : Logger {
    override fun info(message: String, vararg args: Any?) {
        lambdaLogger.log("[INFO] $message".format(*args))
    }

    override fun debug(message: String, vararg args: Any?) {
        lambdaLogger.log("[DEBUG] $message".format(*args))
    }

    override fun warn(message: String, vararg args: Any?) {
        lambdaLogger.log("[WARN] $message".format(*args))
    }

    override fun error(message: String, vararg args: Any?) {
        lambdaLogger.log("[ERROR] $message".format(*args))
    }
}
