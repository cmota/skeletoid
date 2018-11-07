package com.mindera.skeletoid.logs.appenders

import android.content.Context
import android.util.Log
import com.mindera.skeletoid.logs.LOG
import com.mindera.skeletoid.logs.utils.LogAppenderUtils.getLogString
import java.util.*

/**
 * Log appender for Logcat
 */
class LogcatAppender(private val TAG: String?) : ILogAppender {

    /**
     * The maximum number of chars to log in a single line.
     */
    var maxLineLength = 4000
    /**
     * To check if logcat should show the complete lines or just the first 4000 chars.
     */
    var isSplitLinesAboveMaxLength = true
    /**
     * LogAppender ID
     */
    override val loggerId = "LogcatAppender"
    /**
     * Minimum log level for this appender
     */
    override var minLogLevel: LOG.PRIORITY = LOG.PRIORITY.VERBOSE

    init {
        if (TAG == null) {
            throw IllegalArgumentException("TAG cannot be null")
        }
    }

    override fun enableAppender(context: Context) {
        //Nothing needed here
    }

    override fun disableAppender() {
        //Nothing needed here
    }

    override fun log(type: LOG.PRIORITY, t: Throwable?, vararg log: String) {
        if (type.ordinal < minLogLevel.ordinal) {
            return
        }
        val logString = getLogString(*log)
        val logs = formatLog(logString)

        for (logText in logs) {

            when (type) {
                LOG.PRIORITY.VERBOSE -> Log.v(TAG, logText, t)
                LOG.PRIORITY.WARN -> Log.w(TAG, logText, t)
                LOG.PRIORITY.ERROR -> Log.e(TAG, logText, t)
                LOG.PRIORITY.DEBUG -> Log.d(TAG, logText, t)
                LOG.PRIORITY.INFO -> Log.i(TAG, logText, t)
                LOG.PRIORITY.FATAL -> Log.wtf(TAG, logText, t)
                else -> Log.d(TAG, logText, t)
            }
        }
    }

    /**
     * Formats the log to respect the value defined in MAX_LINE_LENGTH
     *
     * @param text The log text
     * @return A list of lines to log
     */
    fun formatLog(text: String?): List<String> {
        val result = ArrayList<String>()

        if (text == null || text.isEmpty()) {
            return result
        }

        if (text.length > maxLineLength) {
            if (isSplitLinesAboveMaxLength) {

                val chunkCount = Math.ceil((1f * text.length / maxLineLength).toDouble()).toInt()
                for (i in 1..chunkCount) {
                    val max = maxLineLength * i
                    if (max < text.length) {
                        result.add("[Chunk $i of $chunkCount] ${text.substring(maxLineLength * (i - 1), max)}")
                    } else {
                        result.add("[Chunk $i of $chunkCount] ${text.substring(maxLineLength * (i - 1))}")

                    }
                }
            }

        } else {
            result.add(text)
        }
        return result
    }


}