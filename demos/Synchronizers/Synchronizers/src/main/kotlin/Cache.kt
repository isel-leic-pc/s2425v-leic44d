package isel.leic.pc.demos

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private class Holder<K, V>(
    private val transform: (K) -> V
) {
    private var value: V? = null
    private val guard = ReentrantLock()

    fun getValue(key: K): V {
        guard.withLock {
            if (value == null) {
                value = transform(key)
            }
            return value!!
        }
    }
}

class Cache<K, V>(private val transform: (K) -> V) {
    private val cache = mutableMapOf<K, Holder<K, V>>()
    private val guard = ReentrantLock()

    fun get(key: K): V {
        val holder = guard.withLock {
            val result = cache[key]
            if (result != null) {
                result
            }
            else {
                val newHolder = Holder(transform)
                cache[key] = newHolder
                newHolder
            }
        }

        return holder.getValue(key)
    }
}

fun cacheUsageSample() {
    val cache = Cache(transform = { key: String -> key.length })
    val value = cache.get("Hello")  // Should call transform("Hello") = 5
    val anotherValue = cache.get("Hello") // Should NOT call transform("Hello") again
}