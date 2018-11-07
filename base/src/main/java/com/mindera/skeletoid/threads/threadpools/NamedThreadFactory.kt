package com.mindera.skeletoid.threads.threadpools

import com.mindera.skeletoid.logs.LOG
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Factory for threads that provides custom naming
 */
class NamedThreadFactory
/**
 * Default constructor
 *
 * @param threadPoolName The name of the ThreadPool
 */
internal constructor(private val mNamePrefix: String, private val mMaxFactoryThreads: Int) : ThreadFactory {

    private val threadGroup: ThreadGroup
    private val threadPoolNumber = AtomicInteger(0)
    val threads: Queue<Thread>

    init {
        val s = System.getSecurityManager()
        threadGroup = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
        threads = ArrayBlockingQueue(mMaxFactoryThreads)
    }

    /**
     * Creates a new named thread
     *
     * @param r Runnable
     * @return Thread
     */
    override fun newThread(r: Runnable): Thread {

        val threadNumber = threadPoolNumber.incrementAndGet()
        val threadName = "$mNamePrefix [#$threadNumber/$mMaxFactoryThreads]"

        val t = Thread(threadGroup, r, threadName, 0)

        if (t.isDaemon) {
            t.isDaemon = false
        }
        if (t.priority != Thread.NORM_PRIORITY) {
            t.priority = Thread.NORM_PRIORITY
        }

        val threadTotal = ThreadPoolUtils.threadCount.incrementAndGet()

        LOG.d(LOG_TAG, "Created one more thread: "
                + threadName
                + " | Total number of threads (currently): "
                + threadTotal)

        threads.add(t)

        return t
    }

    fun clearThreads() {
        threads.clear()
    }

    companion object {

        private val LOG_TAG = "NamedThreadFactory"
    }
}
