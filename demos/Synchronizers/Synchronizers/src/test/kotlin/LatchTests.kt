import isel.leic.pc.demos.Latch
import kotlin.test.Test

class LatchTests {

    @Test
    fun `await blocks threads until gate is open`() {
        val gate = Latch()
        Thread.ofPlatform().start {
            Thread.sleep(1000)
            gate.open()
        }

        gate.await()

    }
}