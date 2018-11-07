package com.mindera.skeletoid.network

import android.content.Context
import android.net.ConnectivityManager

/**
 * Class to validate if we are connected to a network and if we have internet access.
 */
object Connectivity {
//    private static final String LOG_TAG = "Connectivity";

    /**
     * Validates if we are connected to a network.
     * REMINDER: Being connected to a network DOES NOT imply internet access!
     *
     * You'll need to add <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission> to the manifest
     *
     * @param context The context
     * @return true if we are connected, false otherwise.
     */
    @JvmStatic
    fun isConnected(context: Context?): Boolean {
        if (context == null) {
            throw IllegalArgumentException("Context must not be null")
        }
        val cm = context.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val ni = cm.activeNetworkInfo
        return ni != null
    }

    /**
     * Check if we are connected to a WIFI network
     *
     * You'll need to add <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission> to the manifest
     *
     * @param context The context
     * @return true if we are connected, false otherwise.
     */
    @JvmStatic
    fun isConnectedToWIFI(context: Context?): Boolean {
        if (context == null) {
            throw IllegalArgumentException("Context must not be null")
        }
        val cm = context.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val ni = cm.activeNetworkInfo
        return ni != null && ni.type == ConnectivityManager.TYPE_WIFI
    }

}
