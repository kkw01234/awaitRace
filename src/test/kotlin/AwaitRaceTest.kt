import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class AwaitTest {

    @Test
    fun testAwaitRace() = runBlocking {
        val test1 = async { delay(20); "test1" }
        val test2 = async { delay(10); "test2" }

        val result = awaitRace(test1, test2) {
            assertEquals("test1", it)
        }
        assertEquals("test2", result)
    }

    @Test
    fun testAwaitRaceNotCallback() = runBlocking {
        val d1 = async { delay(10); "d1" }
        val d2 = async { delay(20); "d2" }

        val result = awaitRace(d1, d2)

        assertEquals("d1", result)
    }

    @Test
    fun testAwaitRaceByCollection() = runBlocking {
        val d1 = async { delay(20); "d1" }
        val d2 = async { delay(10); "d2" }

        val result = listOf(d1, d2).awaitRace { assertEquals("d1", it) }

        assertEquals("d2", result)
    }

    @Test
    fun testAwaitRaceByCollectionAndNotCallback() = runBlocking {
        val d1 = async { delay(20); "d1" }
        val d2 = async { delay(10); "d2" }

        val result = listOf(d1, d2).awaitRace()

        assertEquals("d2", result)
    }
}
