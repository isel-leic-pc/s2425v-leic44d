package isel.leic.pc.demos

class Cache<K, V>(private val transform: (K) -> V) {
    // Make it thread-safe without using ConcurrentHashMap
    fun get(key: K): V {
        // This is not a cache, but a pass-through implementation
        return transform(key)
    }
}

fun cacheUsageSample() {
    val cache = Cache(transform = { key: String -> key.length })
    val value = cache.get("Hello")  // Should call transform("Hello") = 5
    val anotherValue = cache.get("Hello") // Should NOT call transform("Hello") again
}