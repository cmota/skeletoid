package com.mindera.skeletoid.logs.appenders

import android.content.Context
import android.util.Log
import com.mindera.skeletoid.generic.AndroidUtils
import com.mindera.skeletoid.logs.LOG
import com.mindera.skeletoid.logs.utils.LogAppenderUtils.getLogString
import com.mindera.skeletoid.threads.threadpools.ThreadPoolUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.SimpleFormatter

/**
 * Log appender for file
 */
class LogFileAppender(private val TAG: String?, fileName: String?, writeToExternalStorage: Boolean = false)
    : ILogAppender {

    companion object {
        private val LOG_TAG = "LogFileAppender"

        private val dateFormatter = SimpleDateFormat("MM-dd HH:mm:ss.SSS")
    }

    /**
     * Log ID
     */
    override val loggerId = "LogFileAppender"
    /**
     * Size of the log file in MBytes
     */
    var logFileSize = 5
    /**
     * Number of files to log (rolling appending)
     */
    var numberOfLogFiles = 1
    /**
     * Whether or not logging to file is possible (don't change value! This is controlled
     * automatically)
     */
    @Volatile
    private var canWriteToFile = false
    /**
     * Whether or not logging to file is possible (don't change value! This is controlled
     * automatically)
     */
    private var writeToExternal = false
    /**
     * FileHandler logger: To write to file *
     */
    private var fileHandler: FileHandler? = null
    /**
     * Thread pool to write files to disk. It will only span 1 thread
     */
    private var fileLoggingTP: java.util.concurrent.ThreadPoolExecutor? = null

    //Default name, it will be replaced to packageName.log on constructor
    //It presents no problem, because this will be used only after the logger is instantiated.
    private var LOG_FILE_NAME = "debug.log"
    /**
     * Minimum log level for this appender
     */
    override var minLogLevel: LOG.PRIORITY = LOG.PRIORITY.VERBOSE

    val isThreadPoolRunning: Boolean
        get() = (fileLoggingTP != null
                && fileLoggingTP?.isTerminating == false
                && fileLoggingTP?.isTerminated == false
                && fileLoggingTP?.isShutdown == false)

    init {
        if (TAG == null) {
            throw IllegalArgumentException("TAG cannot be null")
        }

        if (fileName == null) {
            throw IllegalArgumentException("FileName cannot be null")
        }

        if (!isFilenameValid(fileName)) {
            throw IllegalArgumentException("Invalid fileName")
        }

        writeToExternal = writeToExternalStorage

        LOG_FILE_NAME = "$fileName.log"
    }

    /**
     * Check if fileName is valid
     *
     * @param fileName fileName
     * @return true if it is, false if not
     */
    fun isFilenameValid(fileName: String): Boolean {
        return fileName.matches("\\w+".toRegex())
    }

    /**
     * Converts LOG level into FileHandler level
     *
     * @param type LOG type
     * @return FileHandler level
     */
    private fun getFileHandlerLevel(type: LOG.PRIORITY): Level {

        val level: Level

        when (type) {
            LOG.PRIORITY.VERBOSE -> level = Level.ALL
            LOG.PRIORITY.WARN -> level = Level.WARNING
            LOG.PRIORITY.ERROR, LOG.PRIORITY.FATAL -> level = Level.SEVERE
            LOG.PRIORITY.INFO -> level = Level.INFO
            LOG.PRIORITY.DEBUG -> level = Level.ALL
            else -> level = Level.ALL
        }

        return level
    }

    override fun enableAppender(context: Context) {
        val MBYTE_IN_BYTES = 1024 * 1024

        fileLoggingTP = ThreadPoolUtils.getFixedThreadPool("LogToFileTP", 1)

        fileLoggingTP?.let {

            it.submit {
                try {
                    fileHandler = FileHandler(getFileLogPath(context),
                            logFileSize * MBYTE_IN_BYTES, numberOfLogFiles, true)
                    fileHandler?.let { fileHandler ->
                        fileHandler?.formatter = SimpleFormatter()
                        fileHandler?.formatter = object : Formatter() {
                            override fun format(logRecord: LogRecord): String {
                                return logRecord.message + "\n"
                            }
                        }

                        canWriteToFile = true

                    }


                } catch (e: Throwable) {
                    canWriteToFile = false
                    LOG.e(LOG_TAG, e, "Logging to file startup error")
                }
            }
        }

    }

    override fun disableAppender() {

        if (fileHandler != null) {
            fileHandler!!.close()
            fileHandler = null
        }

        canWriteToFile = false

        if (fileLoggingTP != null) {
            ThreadPoolUtils.shutdown(fileLoggingTP)

            try {
                // wait until task terminate
                if (fileLoggingTP?.awaitTermination(500, TimeUnit.MILLISECONDS) == false) {
                    ThreadPoolUtils.shutdownNow(fileLoggingTP)
                }
            } catch (e: InterruptedException) {
                ThreadPoolUtils.shutdownNow(fileLoggingTP)
            }

        }
    }

    override fun log(type: LOG.PRIORITY, t: Throwable?, vararg log: String) {
        if (type.ordinal < minLogLevel.ordinal) {
            return
        }

        if (!canWriteToFile) {
            Log.e(LOG_TAG, "Cannot write to file")
            return
        }

        if (!isThreadPoolRunning) {
            canWriteToFile = false
            LOG.e(LOG_TAG, "Error on submitToFileLoggingQueue: fileLoggingTP is not available")
        }

        fileLoggingTP?.submit {
            fileHandler?.let { fileHandler ->
                val level = getFileHandlerLevel(type)

                try {
                    val logText = formatLog(type, *log)

                    val logRecord = LogRecord(level, logText)
                    logRecord.thrown = t

                    fileHandler.publish(logRecord)

                } catch (e: Exception) {
                    Log.e(TAG, "Something is wrong", e)
                }

            }
        }
    }

    /**
     * Formats the log
     *
     * @param type Type of log
     * @param t    Throwable (can be null)
     * @param logs Log
     */
    protected fun formatLog(type: LOG.PRIORITY, vararg logs: String): String {
        val builder = StringBuilder()
        builder.append(dateFormatter.format(Date()))
        builder.append(": ")
        builder.append(type.name[0])
        builder.append("/")
        builder.append(TAG)
        builder.append("(").append(Thread.currentThread().id)
        builder.append(")")
        builder.append(": ")
        builder.append(getLogString(*logs))

        return builder.toString()

    }

    private fun getFileLogPath(context: Context): String {
        val path: String = if (writeToExternal) {
            AndroidUtils.getExternalPublicDirectory(context, File.separator + LOG_FILE_NAME)
        } else {
            AndroidUtils.getFileDirPath(context, File.separator + LOG_FILE_NAME)
        }
        return path
    }

    fun canWriteToFile(): Boolean {
        return canWriteToFile && fileHandler != null
    }
}
