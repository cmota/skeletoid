package com.mindera.skeletoid.analytics.appenders

import android.content.Context

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class FBAppenderUnitTest {

    private var mContext: Context? = null

    @Before
    fun setUp() {
        mContext = mock<Context>(Context::class.java)
    }

    @Test
    fun testDisableAppender() {
        val appender = FBAppender()
        appender.disableAppender()

        verify(appender, times(1)).setUserProperty("mFirebaseAnalytics", null)
    }
}
