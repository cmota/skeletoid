package com.mindera.skeletoid.analytics

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import com.mindera.skeletoid.analytics.appenders.IAnalyticsAppender

class Analytics {

    @VisibleForTesting
    internal constructor() {
        throw UnsupportedOperationException()
    }

    companion object {

        //    private static final String TAG = "Analytics";

        @Volatile
        private var instance: IAnalyticsManager? = null

        /**
         * Check if the analytics service is initialized
         *
         * @return true if initialized
         */
        @JvmStatic
        val isInitialized: Boolean
            get() = instance != null

        @JvmStatic
        fun init() {
            getInstance()
        }

        /**
         * Init the analytics engine. This method MUST be called before using Analytics
         *
         * @param context            Context app
         * @param analyticsAppenders The analytics appenders to be started
         */
        @JvmStatic
        fun init(context: Context, analyticsAppenders: List<IAnalyticsAppender>): Set<String> {
            return getInstance().addAppenders(context, analyticsAppenders)
        }

        /**
         * Deinit the analytics engine.
         * This method MUST be called if the Analytics engine is not needed any longer on the app
         */
        @JvmStatic
        fun deinit(context: Context) {
            instance?.removeAllAppenders()
            instance = null
        }

        private fun getInstance(): IAnalyticsManager {
            var result = instance
            if (result == null) {
                synchronized(Analytics::class.java) {
                    result = instance
                    if (result == null) {
                        instance = AnalyticsManager()
                    }
                }
            }
            return instance!!

        }

        /**
         * Enable analytics appenders
         *
         * @param context            Context
         * @param analyticsAppenders Analytics appenders to enable
         * @return Ids of the analytics appenders enabled by their order
         */
        @JvmStatic
        fun addAppenders(context: Context, analyticsAppenders: List<IAnalyticsAppender>): Set<String> {
            return instance?.addAppenders(context, analyticsAppenders) ?: emptySet()
        }

        /**
         * Disable analytics appenders
         *
         * @param context      Context
         * @param analyticsIds Analytics ids of each of the analytics appenders disabled by the order sent
         */
        @JvmStatic
        fun removeAppenders(context: Context, analyticsIds: Set<String>) {
            instance?.removeAppenders(context, analyticsIds)
        }

        /**
         * Remove all analytics appenders
         */
        @JvmStatic
        fun removeAllAppenders() {
            instance?.removeAllAppenders()
        }

        /**
         * Sets the user ID
         *
         * @param userID ID of the user
         */
        @JvmStatic
        fun setUserID(userID: String) {
            instance?.setUserID(userID)
        }

        /**
         * Sets a custom property of the user
         *
         * @param name  Property name
         * @param value Property value
         */
        @JvmStatic
        fun setUserProperty(name: String, value: String) {
            instance?.setUserProperty(name, value)
        }

        /**
         * Track Event method - Analytics generic method to send an event with a payload
         *
         * @param eventName        Event name
         * @param analyticsPayload Generic analytics payload
         */
        @JvmStatic
        fun trackEvent(eventName: String, analyticsPayload: Map<String, Any>) {
            instance?.trackEvent(eventName, analyticsPayload)
        }

        /**
         * Track Event method - Analytics generic method to send an event with a payload
         *
         * @param eventName        Event name
         * @param analyticsPayload Generic analytics payload
         */
        @JvmStatic
        fun trackEvent(eventName: String, analyticsPayload: Bundle) {
            instance?.trackEvent(eventName, analyticsPayload)
        }

        /**
         * Track Page Hits - Analytics generic method to track page hits
         *
         * @param activity            Activity that represent
         * @param screenName          Name of screen
         * @param screenClassOverride Screen name class override
         */
        @JvmStatic
        fun trackPageHit(activity: Activity, screenName: String, screenClassOverride: String) {
            instance?.trackPageHit(activity, screenName, screenClassOverride)
        }
    }
}
