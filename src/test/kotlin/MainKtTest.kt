import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.reflect.full.functions
import kotlin.test.assertEquals

internal class MainKtTest {
    @DisplayName("Sample tests")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """DAY, PART, SAMPLE, REAL
1, 1, 7
1, 2, 5
2, 1, 150
2, 2, 900
3, 1, 198
3, 2, 230
4, 1, 4512
4, 2, 1924
5, 1, 
5, 2, 
""")
    @ParameterizedTest(name = "{0}, {1}")
    fun sampleTests(day: Int, part: Int, sample: String?) {
        fun getSample(day: Int) = javaClass.classLoader.getResource("day$day.txt").readText()

        val actual =
            Main::class.functions.find { it.name == "day${day}part${part}" }!!.call(Main, getSample(day)).toString()

        assertEquals(sample, actual)
    }

    @DisplayName("Real tests")
    @CsvSource(useHeadersInDisplayName = true, textBlock = """DAY, PART, SAMPLE, REAL
1, 1, 1616
1, 2, 1645
2, 1, 2039256
2, 2, 1856459736
3, 1, 3633500
3, 2, 4550283
4, 1, 50008
4, 2, 17408
5, 1, 
5, 2, 
""")
    @ParameterizedTest(name = "{0}, {1}")
    fun realTests(day: Int, part: Int, real: String?) {
        val actual =
            Main::class.functions.find { it.name == "day${day}part${part}" }!!.call(Main, getInput(day)).toString()

        assertEquals(real, actual)
    }
}
