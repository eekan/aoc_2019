import java.io.File
import java.lang.IllegalStateException
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

const val BUG = '#'
const val EMPTY = '.'
const val REC = '?'

fun loadInput(fileName : String) : List<String> {
    return File(fileName).readLines().toList()
}

fun List<String>.toXYMap(): Map<Pair<Int, Int>, Char> {
    return mapIndexed { row, line ->
        line.mapIndexed { col, symbol ->
            Pair(col, row) to symbol
        }
    }.flatten().toMap()
}

fun List<String>.toXYZMap(level:Int = 0): Map<Triple<Int, Int, Int>, Char> {
    return mapIndexed { row, line ->
        line.mapIndexed { col, symbol ->
            Triple(col, row, level) to symbol
        }
    }.flatten().toMap()
}

operator fun Pair<Int, Int>.plus(that: Pair<Int, Int>) =  Pair(this.first + that.first, this.second + that.second)
fun Pair<Int, Int>.neighbours() = listOf(Pair(0, 1), Pair(1, 0), Pair(0, -1), Pair(-1, 0)).map { it + this }

fun Map<Pair<Int, Int>, Char>.next(): Map<Pair<Int, Int>, Char> {
    return mapValues { (key, symbol) ->
        val neighborBugs = key.neighbours().count { this[it] == BUG }
        when (symbol) {
            BUG -> if (neighborBugs == 1) BUG else EMPTY
            else -> if (neighborBugs in 1..2) BUG else EMPTY
        }
    }
}

fun Map<Pair<Int, Int>, Char>.biodiversityRating(): Int {
    return values.mapIndexed { index, symbol ->  if (symbol == BUG) 1 shl index else 0 }.sum()
}

fun firstRepeated(inputGrid: Map<Pair<Int, Int>, Char>): Map<Pair<Int, Int>, Char> {
    val seen = mutableSetOf<String>()
    var grid = inputGrid

    while(seen.add(grid.values.toString())) {
        grid = grid.next()
    }

    return grid
}

operator fun Triple<Int, Int, Int>.plus(that: Triple<Int, Int, Int>) =
        Triple(this.first + that.first,
                this.second + that.second,
                this.third + that.third)

fun Triple<Int, Int, Int>.neighbours(): Sequence<Triple<Int, Int, Int>> = sequence {
    val dirs = listOf(Pair(0, 1), Pair(1, 0), Pair(0, -1), Pair(-1, 0))
    for ((dx, dy) in dirs) {
        val x = first + dx
        val y = second + dy
        when {
            x !in 0..4 || y !in 0..4 -> yield(Triple(2 + dx, 2 + dy, third - 1))
            x == 2 && y == 2 -> {
                when {
                    dx == 1 -> for (i in 0..4) yield(Triple(0, i, third + 1)) // right
                    dx == -1 -> for (i in 0..4) yield(Triple(4, i, third + 1)) // left
                    dy == 1 -> for (i in 0..4) yield(Triple(i, 0, third + 1)) // down
                    dy == -1 -> for (i in 0..4) yield(Triple(i, 4, third + 1)) // up
                }
            }
            else -> yield(Triple(x, y, third))
        }
    }
}

fun Map<Triple<Int, Int, Int>, Char>.next3(): Map<Triple<Int, Int, Int>, Char> {
    val maxLevel = keys.maxBy { it.third }?.third?.plus(1) ?: throw IllegalStateException("No levels")
    val minLevel = keys.minBy { it.third }?.third?.minus(1) ?: throw IllegalStateException("No levels")

    return (minLevel .. maxLevel).flatMap { z ->
        (0 .. 4).flatMap {y ->
            (0 .. 4).map { x ->
                val key = Triple(x, y, z)
                if (x == 2 && y == 2){
                    key to REC
                } else {
                    val neighborBugs = key.neighbours().count { this[it] == BUG }
                    val res = when (this[key]) {
                        BUG -> key to if (neighborBugs == 1) BUG else EMPTY
                        else -> key to if (neighborBugs in 1..2) BUG else EMPTY
                    }
                    res
                }
            }
        }
    }.toMap()
}

fun part01() {
    val input = loadInput("day_24/input.txt").toXYMap()

    // What is the biodiversity rating for the first layout that appears twice?
    println("[Part 01] output=${firstRepeated(input).biodiversityRating()}")
}

fun part02() {
    var input = loadInput("day_24/input.txt").toXYZMap()

    repeat(200) {
        input = input.next3()
    }

    // What is the biodiversity rating for the first layout that appears twice?
    println("[Part 02] output=${input.values.count { it == BUG }}")
}

fun main() {
    testsPart01()
    testsPart02()
    println("[Part 01] time=${measureTimeMillis { part01() }}")
    println("[Part 02] time=${measureTimeMillis { part02() }}")
}

fun testsPart01() {
    println("[Example 1] time=${measureTimeMillis { example1() }}")
    println("[Example 2] time=${measureTimeMillis { example2() }}")
    println("[Example 3] time=${measureTimeMillis { example3() }}")
}

fun testsPart02() {
    println("[Example 4] time=${measureTimeMillis { example4() }}")
}

fun example1() {
    val input = listOf(
            "....#",
            "#..#.",
            "#..##",
            "..#..",
            "#...."
    ).toXYMap()

    val expectedOutputs = listOf(
            listOf(
                    "#..#.",
                    "####.",
                    "###.#",
                    "##.##",
                    ".##.."
            ),
            listOf(
                    "#####",
                    "....#",
                    "....#",
                    "...#.",
                    "#.###"
            ),
            listOf(
                    "#....",
                    "####.",
                    "...##",
                    "#.##.",
                    ".##.#"
            ),
            listOf(
                    "####.",
                    "....#",
                    "##..#",
                    ".....",
                    "##..."
            )
    ).map { it.toXYMap() }

    var actual = input
    expectedOutputs.forEach {
        actual = actual.next()
        assertEquals(it, actual)
    }
}

fun example2() {
    val input = listOf(
            "....#",
            "#..#.",
            "#..##",
            "..#..",
            "#...."
    ).toXYMap()

    val expected = listOf(
            ".....",
            ".....",
            ".....",
            "#....",
            ".#..."
    ).toXYMap()

    assertEquals(expected, firstRepeated(input))
}

fun example3() {
    val input= listOf(
            ".....",
            ".....",
            ".....",
            "#....",
            ".#..."
    ).toXYMap()

    assertEquals(2129920, input.biodiversityRating())
}

fun example4() {
    val input= listOf(
        "....#",
        "#..#.",
        "#.?##",
        "..#..",
        "#...."
    ).toXYZMap()

    val expected = listOf(
            listOf(
                   "..#..",
                    ".#.#.",
                    "..?.#",
                    ".#.#.",
                    "..#.."
            ).toXYZMap(-5),
            listOf(
                    "...#.",
                    "...##",
                    "..?..",
                    "...##",
                    "...#."
            ).toXYZMap(-4),
            listOf(
                   "#.#..",
                    ".#...",
                    "..?..",
                    ".#...",
                    "#.#.."
            ).toXYZMap(-3),
            listOf(
                    ".#.##",
                    "....#",
                    "..?.#",
                    "...##",
                    ".###."
            ).toXYZMap(-2),
            listOf(
                    "#..##",
                    "...##",
                    "..?..",
                    "...#.",
                    ".####"
            ).toXYZMap(-1),
            listOf(
                    ".#...",
                    ".#.##",
                    ".#?..",
                    ".....",
                    "....."
            ).toXYZMap(0),
            listOf(
                    ".##..",
                    "#..##",
                    "..?.#",
                    "##.##",
                    "#####"
            ).toXYZMap(1),
            listOf(
                    "###..",
                    "##.#.",
                    "#.?..",
                    ".#.##",
                    "#.#.."
            ).toXYZMap(2),
            listOf(
                    "..###",
                    ".....",
                    "#.?..",
                    "#....",
                    "#...#"
            ).toXYZMap(3),
            listOf(
                    ".###.",
                    "#..#.",
                    "#.?..",
                    "##.#.",
                    "....."
            ).toXYZMap(4),
            listOf(
                    "####.",
                    "#..#.",
                    "#.?#.",
                    "####.",
                    "....."
            ).toXYZMap(5)
    ).fold(mapOf<Triple<Int, Int, Int>, Char>()) { acc, element -> acc + element}

    var grid = input
    repeat(10) {
        grid = grid.next3()
    }

    assertEquals(99, grid.values.count { it == BUG })
}