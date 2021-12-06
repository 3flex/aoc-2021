import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.toList
import org.jetbrains.kotlinx.multik.ndarray.operations.toSet
import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    println(Main.day5part1(getInput(5)))
    println(Main.day5part2(getInput(5)))
}

fun getInput(day: Int) = Path("inputs/day$day.txt").readText()

object Main {
    fun day1part1(input: String) =
        input.lines()
            .map(String::toInt)
            .zipWithNext { a, b -> if (a < b) 1 else 0 }
            .sum()

    fun day1part2(input: String) =
        input.lines()
            .map(String::toInt)
            .windowed(3) { it.sum() }
            .zipWithNext { a, b -> if (a < b) 1 else 0 }
            .sum()

    fun day2part1(input: String): Int {
        var depth = 0
        var horizontal = 0

        val regex = """(\w*) (\d)""".toRegex()

        input.lines().forEach {
            val (direction, distance) = regex.find(it)!!.destructured
            when (direction) {
                "forward" -> horizontal += distance.toInt()
                "down" -> depth += distance.toInt()
                "up" -> depth -= distance.toInt()
            }
        }

        return horizontal * depth
    }

    fun day2part2(input: String): Int {
        var depth = 0
        var horizontal = 0
        var aim = 0

        val regex = """(\w*) (\d)""".toRegex()

        input.lines().forEach {
            val (direction, distance) = regex.find(it)!!.destructured
            when (direction) {
                "forward" -> {
                    horizontal += distance.toInt()
                    depth += aim * distance.toInt()
                }
                "down" -> aim += distance.toInt()
                "up" -> aim -= distance.toInt()
            }
        }

        return horizontal * depth
    }

    fun day3part1(input: String): Int {
        val values = IntArray(input.lines().first().length)

        values.indices.forEach { index -> values[index] = input.lines().count { it[index] == '1' } }

        val gammaRateBinary = values.map { if (it < input.lines().size / 2) '0' else '1' }.joinToString("")
        val epsilonRateBinary = gammaRateBinary.map { if (it == '0') '1' else '0' }.joinToString("")

        return gammaRateBinary.toInt(2) * epsilonRateBinary.toInt(2)
    }

    fun day3part2(input: String): Int = scrubberRating(input) * oxyGenRating(input)

    tailrec fun oxyGenRating(input: String, index: Int = 0): Int {
        val x = if (input.lines().sumOf { it[index].digitToInt() } < input.lines().size.toDouble() / 2) '0' else '1'

        val filtered = input.lines().filter { it[index] == x }

        return filtered.singleOrNull()?.toInt(2) ?: oxyGenRating(filtered.joinToString("\n"), index + 1)
    }

    tailrec fun scrubberRating(input: String, index: Int = 0): Int {
        val x = if (input.lines().sumOf { it[index].digitToInt() } < input.lines().size.toDouble() / 2) '0' else '1'

        val filtered = input.lines().filter { it[index] != x }

        return filtered.singleOrNull()?.toInt(2) ?: scrubberRating(filtered.joinToString("\n"), index + 1)
    }

    fun Card.drawnToSolve(draws: List<Int>): Set<Int> {
        val drawn = mutableSetOf<Int>()
        draw@ for (number in draws) {
            drawn.add(number)

            for (row in 0..4) {
                if (drawn.intersect(this[row].toSet()).size == 5) {
                    break@draw
                }
            }

            for (col in 0..4) {
                if (drawn.intersect(this[0..5, col].toSet()).size == 5) {
                    break@draw
                }
            }
        }
        return drawn
    }

    fun day4part1(input: String): Int {
        val draws = input.lines()
            .first()
            .split(",")
            .map(String::toInt)

        val card = input.lines()
            .drop(1)
            .flatMap { it.split("""\s+""".toRegex()) }
            .mapNotNull(String::toIntOrNull)
            .chunked(25) { mk.ndarray(it, 5, 5) }
            .map { it to it.drawnToSolve(draws) }
            .minByOrNull { it.second.size }!!

        return card.first.toSet().subtract(card.second).sum() * card.second.last()
    }

    fun day4part2(input: String): Int {
        val draws = input.lines()
            .first()
            .split(",")
            .map(String::toInt)

        val card = input.lines()
            .drop(1)
            .flatMap { it.split("""\s+""".toRegex()) }
            .mapNotNull(String::toIntOrNull)
            .chunked(25) { mk.ndarray(it, 5, 5) }
            .map { it to it.drawnToSolve(draws) }
            .maxByOrNull { it.second.size }!!

        return card.first.toSet().subtract(card.second).sum() * card.second.last()
    }

    data class Line(val x1: Int, val y1: Int, val x2: Int, val y2: Int)

    fun day5part1(input: String): Int {
        val board = mk.zeros<Int>(1000,1000)

        input.lines()
            .map {
                val (x1, y1, x2, y2) = Regex("""(\d+),(\d+) -> (\d+),(\d+)""").matchEntire(it)!!.destructured
                Line(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
            }
            .filter { (it.x1 == it.x2 || it.y1 == it.y2) } // vertical or horizontal
            .forEach {
                if (it.x1 == it.x2) { // is vertical
                    for (i in it.y1..it.y2) {
                        board[it.x1, i] = board[it.x1, i] + 1
                    }
                    for (i in it.y2..it.y1) {
                        board[it.x1, i] = board[it.x1, i] + 1
                    }
                } else { // is horizontal
                    for (i in it.x1..it.x2) {
                        board[i, it.y1] = board[i, it.y1] + 1
                    }
                    for (i in it.x2..it.x1) {
                        board[i, it.y1] = board[i, it.y1] + 1
                    }
                }
            }

        return board.toList().count { it >= 2 }
    }

    fun day5part2(input: String): Int {
        return 0
    }

    fun day6part1(input: String): Int {
        var fish = input
            .split(",")
            .map(String::toInt)
            .fold(IntArray(9)) {acc, i ->
                acc[i] = acc[i] + 1
                acc
            }

        repeat(80) {
            val newFish = IntArray(9)
            newFish[0] = fish[1]
            newFish[1] = fish[2]
            newFish[2] = fish[3]
            newFish[3] = fish[4]
            newFish[4] = fish[5]
            newFish[5] = fish[6]
            newFish[6] = fish[0] + fish[7]
            newFish[7] = fish[8]
            newFish[8] = fish[0]
            fish = newFish.clone()
            println(fish.joinToString())
        }

        return fish.sum()
    }

    fun day6part2(input: String): Long {
        var fish = input
            .split(",")
            .map(String::toInt)
            .fold(LongArray(9)) {acc, i ->
                acc[i] = acc[i] + 1
                acc
            }

        repeat(256) {
            val newFish = LongArray(9)
            newFish[0] = fish[1]
            newFish[1] = fish[2]
            newFish[2] = fish[3]
            newFish[3] = fish[4]
            newFish[4] = fish[5]
            newFish[5] = fish[6]
            newFish[6] = fish[0] + fish[7]
            newFish[7] = fish[8]
            newFish[8] = fish[0]
            fish = newFish.clone()
        }

        return fish.sum()
    }
}

typealias Card = D2Array<Int>
