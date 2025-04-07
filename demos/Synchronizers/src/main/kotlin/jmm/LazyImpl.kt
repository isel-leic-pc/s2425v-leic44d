package isel.leic.pc.demos.jmm

class LazyImpl<out T>(private val initializer: () -> T) : Lazy<T> {
    override val value: T
        get() = TODO("Not yet implemented")

    override fun isInitialized(): Boolean {
        TODO("Not yet implemented")
    }
}

fun <T> myLazy(initializer: () -> T): Lazy<T> = LazyImpl(initializer)
