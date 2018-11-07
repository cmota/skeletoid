package com.mindera.skeletoid.logs

import android.content.Context
import com.mindera.skeletoid.generic.AndroidUtils
import com.mindera.skeletoid.logs.appenders.ILogAppender
import com.mindera.skeletoid.logs.utils.LogAppenderUtils.getLogString
import com.mindera.skeletoid.logs.utils.LogAppenderUtils.getObjectHash
import com.mindera.skeletoid.logs.utils.LogAppenderUtils.getTag
import com.mindera.skeletoid.threads.utils.ThreadUtils.getCurrentThreadName
import java.util.*

/**
 * LOG main class. It contains all the logic and feeds the appenders
 */
class LoggerManager : ILoggerManager {

    companion object {

        private val LOG_TAG = "LoggerManager"
        /**
         * Log format
         */
        val LOG_FORMAT_4ARGS = "%s %s %s | %s"
        /**
         * Log format
         */
        private val LOG_FORMAT_3ARGS = "%s %s | %s"
    }

    /**
     * Application TAG for logs
     */
    private val PACKAGE_NAME: String
    /**
     * Define if the method name invoking the log should be printed or not (via exception stack)
     */
    private var mAddMethodName = false

    /**
     * Define if the method name invoking the log should be printed or not (via exception stack)
     */
    private val mAddPackageName = false

    /**
     * List of appenders (it can be improved to an ArrayMap if we want to add the support lib as dependency
     */
    private val mLogAppenders = HashMap<String, ILogAppender>()

    /**
     * The logger itself
     */

    constructor(context: Context) {
        PACKAGE_NAME = AndroidUtils.getApplicationPackage(context)
    }

    /**
     * This is to be used on ONLY ON UNIT TESTS.
     */
    constructor(packageName: String) {
        PACKAGE_NAME = packageName
    }

    /**
     * Enables or disables logging to console/logcat.
     */
    override fun addAppenders(context: Context, logAppenders: List<ILogAppender>): Set<String> {
        if (logAppenders.isEmpty()) {
            return HashSet()
        }

        val appenderIds = HashSet<String>()

        for (logAppender in logAppenders) {

            val loggerId = logAppender.loggerId

            if (mLogAppenders.containsKey(loggerId)) {
                log(LOG_TAG, LOG.PRIORITY.ERROR, "Replacing Log Appender with ID: $loggerId")
                val oldLogAppender = mLogAppenders.remove(loggerId)
                oldLogAppender!!.disableAppender()
            }

            logAppender.enableAppender(context)
            appenderIds.add(loggerId)
            mLogAppenders[loggerId] = logAppender
        }
        return appenderIds
    }


    /**
     * Enables or disables logging to console/logcat.
     */
    override fun removeAppenders(context: Context, loggerIds: Set<String>) {
        if (loggerIds == null || mLogAppenders.isEmpty()) {
            return
        }

        for (logId in loggerIds) {
            val logAppender = mLogAppenders.remove(logId)
            logAppender?.disableAppender()
        }
    }

    override fun removeAllAppenders() {
        val appendersKeys = ArrayList(mLogAppenders.keys)
        for (analyticsId in appendersKeys) {
            val analyticsAppender = mLogAppenders.remove(analyticsId)
            analyticsAppender?.disableAppender()
        }
    }

    override fun setMethodNameVisible(visibility: Boolean) {
        mAddMethodName = visibility
    }

    private fun pushLogToAppenders(type: LOG.PRIORITY, t: Throwable?, log: String) {
        for ((_, value) in mLogAppenders) {
            value.log(type, t, log)
        }
    }

    override fun log(tag: String, priority: LOG.PRIORITY, vararg text: String) {
        if (mLogAppenders.isEmpty()) {
            //nothing will be logged so no point in continuing
            return
        }

        val log = String.format(LOG_FORMAT_4ARGS, tag, getObjectHash(tag), getCurrentThreadName(), getLogString(*text))

        pushLogToAppenders(priority, null, log)
    }

    override fun log(tag: String, priority: LOG.PRIORITY, t: Throwable, vararg text: String) {
        if (mLogAppenders.isEmpty()) {
            //nothing will be logged so no point in continuing
            return
        }

        val logString = getLogString(*text)

        val log = String.format(LOG_FORMAT_4ARGS, tag, getObjectHash(tag), getCurrentThreadName(), logString)

        pushLogToAppenders(priority, t, log)
    }

    fun log(clazz: Class<*>?, type: LOG.PRIORITY?, text: String?) {
        if (mLogAppenders.isEmpty()) {
            //nothing will be logged so no point in continuing
            return
        }

        val logString = getLogString(text)

        if (clazz == null || type == null || text == null) {
            LOG.e(LOG_TAG, "Something is wrong, logger caught null -> $logString")
            return
        }

        val log = String.format(LOG_FORMAT_3ARGS, getTag(clazz, mAddPackageName, PACKAGE_NAME, mAddMethodName), getCurrentThreadName(), logString)

        pushLogToAppenders(type, null, log)
    }

    fun log(clazz: Class<*>?, type: LOG.PRIORITY?, text: String?, t: Throwable?) {
        if (mLogAppenders.isEmpty()) {
            //nothing will be logged so no point in continuing
            return
        }

        val logString = getLogString(text)

        if (clazz == null || type == null || text == null || t == null) {
            LOG.e(LOG_TAG, "Something is wrong, logger caught null -> $logString")
            return
        }

        val log = String.format(LOG_FORMAT_3ARGS, getTag(clazz, mAddPackageName, PACKAGE_NAME, mAddMethodName), getCurrentThreadName(), logString)
        pushLogToAppenders(type, t, log)
    }
}
