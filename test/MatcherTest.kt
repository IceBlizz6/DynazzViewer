import dynazzviewer.base.Matcher
import dynazzviewer.base.UniqueKey
import org.junit.Assert
import org.junit.Test

class MatcherTest {
    private val matcher = Matcher()

    @Test
    fun addedTest() {
        val original = listOf<Dummy>()
        val result = matcher.matchWithString(original, listOf("ABC"))
        Assert.assertEquals(1, result.added.count())
        Assert.assertEquals(0, result.removed.count())
        Assert.assertEquals(0, result.matched.count())
    }

    @Test
    fun removedTest() {
        val original = listOf<Dummy>(Dummy("ABC"))
        val result = matcher.matchWithString(original, listOf())
        Assert.assertEquals(0, result.added.count())
        Assert.assertEquals(1, result.removed.count())
        Assert.assertEquals(0, result.matched.count())
    }

    @Test
    fun matchedTest() {
        val original = listOf<Dummy>(Dummy("ABC"))
        val result = matcher.matchWithString(original, listOf("ABC"))
        Assert.assertEquals(0, result.added.count())
        Assert.assertEquals(0, result.removed.count())
        Assert.assertEquals(1, result.matched.count())
    }

    class Dummy(
        override val uniqueKey: String
    ) : UniqueKey
}
