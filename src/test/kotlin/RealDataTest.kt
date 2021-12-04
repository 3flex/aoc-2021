import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class RealDataTest {
    @Test fun `day 1 part 1`() { assertEquals(1616, day1part1(getInput(1))) }
    @Test fun `day 1 part 2`() { assertEquals(1645, day1part2(getInput(1))) }
    @Test fun `day 2 part 1`() { assertEquals(2039256, day2part1(getInput(2))) }
    @Test fun `day 2 part 2`() { assertEquals(1856459736, day2part2(getInput(2))) }
    @Test fun `day 3 part 1`() { assertEquals(3633500, day3part1(getInput(3))) }
    @Test fun `day 3 part 2`() { assertEquals(4550283, day3part2(getInput(3))) }
    @Test fun `day 4 part 1`() { assertEquals(50008, day4part1(getInput(4))) }
    @Test fun `day 4 part 2`() { assertEquals(17408, day4part2(getInput(4))) }
    @Test fun `day 5 part 1`() { assertEquals(0, day5part1(getInput(5))) }
    @Test fun `day 5 part 2`() { assertEquals(0, day5part2(getInput(5))) }
}
