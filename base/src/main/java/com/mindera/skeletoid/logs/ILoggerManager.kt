package com.mindera.skeletoid.logs

import android.content.Context

import com.mindera.skeletoid.logs.appenders.ILogAppender

/**
 * LOG interface
 */
interface ILoggerManager {

    /**
     * Log to all log appenders
     *
     * @param tag      Log tag
     * @param priority Log priority
     * @param text     Log text
     */
    fun log(tag: String, priority: LOG.PRIORITY, vararg text: String)

    /**
     * Log to all log appenders
     *
     * @param tag      Log tag
     * @param priority Log priority
     * @param t        Trowable
     * @param text     Log text
     */
    fun log(tag: String, priority: LOG.PRIORITY, t: Throwable, vararg text: String)

    /**
     * Set method name visible in logs (careful this is a HEAVY operation)
     *
     * @param visibility true if enabled
     */
    fun setMethodNameVisible(visibility: Boolean)

    /**
     * Enable log appenders
     * @param context Context
     * @param logAppenders Log appenders to enable
     * @return Ids of the logs enabled by their order
     */
    fun addAppenders(context: Context, logAppenders: List<ILogAppender>): Set<String>


    /**
     * Disable log appenders
     * @param context Context
     * @param loggerIds Log ids of each of the loggers enabled by the order sent
     */
    fun removeAppenders(context: Context, loggerIds: Set<String>)


    /**
     * Disable all log appenders
     */
    fun removeAllAppenders()

}
