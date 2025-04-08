package isel.leic.pc.demos.jmm

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private object UNINITIALIZED_VALUE
class LazyImpl<out T>(initializer: () -> T) : Lazy<T> {

    private var _initializer: (() -> T)? = initializer
    @Volatile private var _value: Any? = UNINITIALIZED_VALUE
    private val guard = ReentrantLock()

    override val value: T
        get() {
            if (_value === UNINITIALIZED_VALUE) {
                guard.withLock {
                    if (_value === UNINITIALIZED_VALUE) {
                        _value = _initializer!!()
                        _initializer = null
                    }
                }
            }

            @Suppress("UNCHECKED_CAST")
            return _value as T
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE
}

fun <T> myLazy(initializer: () -> T): Lazy<T> = LazyImpl(initializer)

fun main() {

    val someValue = myLazy {
        println("Computing value...")
        null
    }
    println("Using someValue = ${someValue.value}")
}