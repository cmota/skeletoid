package com.mindera.skeletoid.generic

import android.support.annotation.VisibleForTesting

import com.mindera.skeletoid.logs.LOG

/**
 * Class to provide debug only methods and utilities that should NOT be used in production
 */
class DebugTools @VisibleForTesting
internal constructor() {

    init {
        throw UnsupportedOperationException()
    }

    companion object {

        fun printAllStackTraces(clazz: Class<*>) {

            LOG.d(clazz.toString(), "DUMPING ALL STACK TRACES")

            val liveThreads = Thread.getAllStackTraces()
            for (thread in liveThreads.keys) {
                LOG.d(clazz.toString(), "Thread " + thread.name)
                val traceElements = liveThreads[thread]
                for (traceElement in traceElements!!) {
                    LOG.d(clazz.toString(), "at $traceElement")
                }
            }
        }
    }
}
