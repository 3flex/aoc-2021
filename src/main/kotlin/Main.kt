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
import kotlin.math.abs

fun main() {
    println(Main.day8part1(getInput(8)))
    println(Main.day8part2(getInput(8)))
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
        val board = mk.zeros<Int>(1000,1000)

        // straight lines
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

        // diagonal lines
        input.lines()
            .map {
                val (x1, y1, x2, y2) = Regex("""(\d+),(\d+) -> (\d+),(\d+)""").matchEntire(it)!!.destructured
                Line(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
            }
            .filterNot { (it.x1 == it.x2 || it.y1 == it.y2) } // diagonal lines
            .forEach {
                when {
                    it.x1 < it.x2 && it.y1 < it.y2 -> {
                        var y = it.y1
                        for (x in it.x1..it.x2) {
                            board[x, y] = board[x, y] + 1
                            y++
                        }
                    }
                    it.x1 < it.x2 && it.y1 > it.y2 -> {
                        var y = it.y1
                        for (x in it.x1..it.x2) {
                            board[x, y] = board[x, y] + 1
                            y--
                        }
                    }
                    it.x1 > it.x2 && it.y1 < it.y2 -> {
                        var y = it.y1
                        for (x in it.x1 downTo it.x2) {
                            board[x, y] = board[x, y] + 1
                            y++
                        }
                    }
                    it.x1 > it.x2 && it.y1 > it.y2 -> {
                        var y = it.y1
                        for (x in it.x1 downTo it.x2) {
                            board[x, y] = board[x, y] + 1
                            y--
                        }
                    }
                }
            }

        return board.toList().count { it >= 2 }
    }

    fun newFish(input: LongArray) = longArrayOf(
        input[1],
        input[2],
        input[3],
        input[4],
        input[5],
        input[6],
        input[0] + input[7],
        input[8],
        input[0],
    )

    fun day6part1(input: String): Long {
        var fish = input
            .split(",")
            .map(String::toInt)
            .fold(LongArray(9)) {acc, i ->
                acc[i] = acc[i] + 1
                acc
            }

        repeat(80) {
            fish = newFish(fish)
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
            fish = newFish(fish)
        }

        return fish.sum()
    }

    fun day7part1(input: String): Int {
        val crabs = input.split(",").map(String::toInt)

        var result: Int = Integer.MAX_VALUE

        for (steps in crabs.minOrNull()!!..crabs.maxOrNull()!!) {
            val fuelUsed = crabs.sumOf { abs(it - steps) }
            if (fuelUsed < result) result = fuelUsed
        }

        return result
    }

    fun day7part2(input: String): Int {
        val crabs = input.split(",").map(String::toInt)

        var result: Int = Integer.MAX_VALUE

        for (steps in crabs.minOrNull()!!..crabs.maxOrNull()!!) {
            val fuelUsed = crabs.sumOf {
                val n = abs(steps - it)
                n * (n + 1) / 2
            }
            if (fuelUsed < result) result = fuelUsed
        }

        return result
    }

    fun day8part1(input: String) =
        input.lines()
            .map { it.substringAfter('|').split(' ') }
            .sumOf { it.count { it.length in setOf(2, 3, 4, 7) } }

    fun day8part2(input: String): Int {
        var realOutput = 0
        input.lines().forEach { segment ->
            val patterns = segment.substringBefore('|').trim().split(' ').map { it.toSet() }
            val output = segment.substringAfter('|').trim().split(' ')
            val candidates = buildMap {
                ('a'..'g').forEach { this[it] = ('a'..'g').toMutableSet() }
            }

            patterns.forEach {
                when (it.size) {
                    2 -> { // number 1
                        candidates.get('a')!!.removeAll(it)
                        candidates.get('b')!!.removeAll(it)
                        candidates.get('c')!!.removeAll(('a'..'g').minus(it))
                        candidates.get('d')!!.removeAll(it)
                        candidates.get('e')!!.removeAll(it)
                        candidates.get('f')!!.removeAll(('a'..'g').minus(it))
                        candidates.get('g')!!.removeAll(it)
                    }
                    3 -> { // number 7
                        candidates.get('a')!!.removeAll(('a'..'g').minus(it))
                        candidates.get('b')!!.removeAll(it)
                        candidates.get('c')!!.removeAll(('a'..'g').minus(it))
                        candidates.get('d')!!.removeAll(it)
                        candidates.get('e')!!.removeAll(it)
                        candidates.get('f')!!.removeAll(('a'..'g').minus(it))
                        candidates.get('g')!!.removeAll(it)
                    }
                    4 -> { // number 4
                        candidates.get('a')!!.removeAll(it)
                        candidates.get('b')!!.removeAll(('a'..'g').minus(it))
                        candidates.get('c')!!.removeAll(('a'..'g').minus(it))
                        candidates.get('d')!!.removeAll(('a'..'g').minus(it))
                        candidates.get('e')!!.removeAll(it)
                        candidates.get('f')!!.removeAll(('a'..'g').minus(it))
                        candidates.get('g')!!.removeAll(it)
                    }
                }
            }

            // get 'c', 'f'
            if (patterns.filter { it.size == 6 }.count { it.contains(candidates.get('c')!!.first()) } == 2) {
                candidates.get('c')!!.remove(candidates.get('c')!!.last())
                candidates.get('f')!!.remove(candidates.get('c')!!.first())
            } else {
                candidates.get('c')!!.remove(candidates.get('c')!!.first())
                candidates.get('f')!!.remove(candidates.get('c')!!.last())
            }

            // get 'b', 'd'
            if (patterns.filter { it.size == 6 }.count { it.contains(candidates.get('d')!!.first()) } == 2) {
                candidates.get('d')!!.remove(candidates.get('d')!!.last())
                candidates.get('b')!!.remove(candidates.get('d')!!.first())
            } else {
                candidates.get('d')!!.remove(candidates.get('d')!!.first())
                candidates.get('b')!!.remove(candidates.get('d')!!.last())
            }

            // get 'e', 'g'
            if (patterns.filter { it.size == 6 }.count { it.contains(candidates.get('e')!!.first()) } == 2) {
                candidates.get('e')!!.remove(candidates.get('e')!!.last())
                candidates.get('g')!!.remove(candidates.get('e')!!.first())
            } else {
                candidates.get('e')!!.remove(candidates.get('e')!!.first())
                candidates.get('g')!!.remove(candidates.get('e')!!.last())
            }

            val outputCodes = output.map {
                it.map { (candidates.entries.associateBy({ it.value.single() }) { it.key }).get(it)!! }.sorted()
                    .joinToString("")
            }

            realOutput += outputCodes.map {
                when (it.toCharArray().sorted().joinToString("")) {
                    "abcefg" -> 0
                    "cf" -> 1
                    "acdeg" -> 2
                    "acdfg" -> 3
                    "bcdf" -> 4
                    "abdfg" -> 5
                    "abdefg" -> 6
                    "acf" -> 7
                    "abcdefg" -> 8
                    "abcdfg" -> 9
                    else -> error(it)
                }
            }.joinToString("").toInt()
        }

        return realOutput
    }

    fun day10part1(input: String): Int {
        val lines = input.lines()
        val tracker = mutableListOf<Char>()
        val danglers = mutableListOf<Char>()
        for (line in lines) {
            for (char in line) {
                if (char in setOf('(', '[', '{', '<')) tracker.add(char)
                else if ((tracker.last() == '(' && char == ')') || (tracker.last() == '[' && char == ']') || (tracker.last() == '{' && char == '}') || (tracker.last() == '<' && char == '>')) {
                    tracker.removeLast()
                } else {
                    danglers.add(char)
                    break
                }
            }
        }
        return danglers.count { it == ')' } * 3 + danglers.count { it == ']' } * 57 + danglers.count { it == '}' } * 1197 + danglers.count { it == '>' } * 25137
    }

    fun day10part2(input: String): Long {
        val lines = input.lines()
        val filteredLines = lines.toMutableList()
        val tracker = mutableListOf<Char>()
        for (line in lines) {
            for (char in line) {
                if (char in setOf('(', '[', '{', '<')) tracker.add(char)
                else if ((tracker.last() == '(' && char == ')') || (tracker.last() == '[' && char == ']') || (tracker.last() == '{' && char == '}') || (tracker.last() == '<' && char == '>')) {
                    tracker.removeLast()
                } else {
                    filteredLines.remove(line)
                    tracker.clear()
                    break
                }
            }
        }

        val completionStrings = mutableListOf<Long>()

        filteredLines.forEach { line ->
            tracker.clear()
            line.forEach { char ->
                if (char in setOf('(', '[', '{', '<')) tracker.add(char)
                else if ((tracker.last() == '(' && char == ')') || (tracker.last() == '[' && char == ']') || (tracker.last() == '{' && char == '}') || (tracker.last() == '<' && char == '>')) {
                    tracker.removeLast()
                }
            }
            completionStrings.add(tracker.joinToString("").reversed().fold(0L) { i, char ->
                i * 5 + when(char) {
                    '(' -> 1
                    '[' -> 2
                    '{' -> 3
                    '<' -> 4
                    else -> error("")
                }
            })
        }

        return completionStrings.sorted().drop(completionStrings.size / 2).first()
    }
}

typealias Card = D2Array<Int>
