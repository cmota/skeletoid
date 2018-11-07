package com.mindera.skeletoid.routing

import android.app.Activity

/**
 * Closes the activity passed in the constructor
 */
class CloseActivityCommand(private val mActivity: Activity) : IRouteCommand {

    override fun navigate() {
        mActivity.finish()
    }
}
