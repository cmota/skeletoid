package com.mindera.skeletoid.analytics.appenders;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.util.Map;

/**
 * Interface for Analytics appenders
 */
public interface IAnalyticsAppender {

    /**
     * Enable analytics
     *
     * @param context Application context
     */
    void enableAppender(Context context);

    /**
     * Disable analytics.
     */
    void disableAppender();

    /**
     * Track app event
     *
     * @param eventName        Event name
     * @param analyticsPayload Generic analytics payload
     */
    void trackEvent(String eventName, Map<String, Object> analyticsPayload);

    /**
     * Track app event
     *
     * @param eventName        Event name
     * @param analyticsPayload Generic analytics payload
     */
    void trackEvent(String eventName, Bundle analyticsPayload);

    /**
     * Track app page hit
     *
     * @param activity            Activity that represent
     * @param screenName          Screen name
     * @param screenClassOverride Screen class override name
     */
    void trackPageHit(Activity activity, String screenName, String screenClassOverride);

    /**
     * Get Analytics id (it should be unique within AnalyticsAppenders)
     */
    String getAnalyticsId();

    /**
     * Sets the user ID
     *
     * @param userID ID of the user
     */
    void setUserID(String userID);

    /**
     * Sets a custom property of the user
     *
     * @param name  Property name
     * @param value Property value
     */
    void setUserProperty(String name, String value);
}
