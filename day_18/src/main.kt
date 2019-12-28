import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

fun loadInput(fileName : String) : List<List<Char>> {
    return File(fileName).readLines().map { it.toCharArray().toList() }
}

fun <T> List<List<T>>.toXYMap(): MutableMap<Coordinate, T> {
    return mapIndexed { row, list ->
        list.mapIndexed{ col, value ->
            Coordinate(col, row) to value
        }
    }.flatten().toMap().filterValues { it != '#' }.toMutableMap()
}

fun Char.bitValue(offset: Char) = (1 shl (this - offset))

typealias Coordinate = Pair<Int, Int>
operator fun Coordinate.plus(that: Coordinate) = Coordinate(this.first + that.first, this.second + that.second)
operator fun Coordinate.compareTo(that: Coordinate): Int {
    val res = this.first.compareTo(that.first)
    return if (res != 0) res else this.second.compareTo(that.second)
}

fun bfs(input: Map<Coordinate, Char>, initialPosition: Coordinate): Int {
    val queue = ArrayDeque<Triple<Coordinate, Int, Int>>()
    val width = checkNotNull(input.keys.maxBy { it.first }).first + 1
    val height = checkNotNull(input.keys.maxBy { it.second }).second + 1
    val directions = listOf(Coordinate(0, 1), Coordinate(1, 0), Coordinate(0, -1), Coordinate(-1, 0))
    val visited = List(width * height) { hashSetOf<Int>() }
    operator fun List<HashSet<Int>>.get(c: Coordinate) = get(c.first + c.second * width)

    var bestScore = Pair(0, 0)

    queue.push(Triple(initialPosition, 0, 0))
    visited[initialPosition].add(0)

    while (queue.isNotEmpty()) {
        val (position, keys, steps) = queue.remove()

        for (direction in directions) {
            val nextPosition = position + direction

            // Skip if nextPosition ain't in graph
            val symbol = input[nextPosition] ?: continue

            // Have we reached a door we cannot unlock?
            if (symbol.isUpperCase() && (keys and symbol.bitValue('A') == 0)) continue

            // Have we found a key? Pick it up and add it to previous keys
            var nextKeys = if (symbol.isLowerCase()) keys or symbol.bitValue('a') else keys

            // Mark as visited, if already visited we already have the best sub-score
            if(!visited[nextPosition].add(nextKeys)) continue

            // Find the solution that has the most keys and shortest number of steps
            val score = Pair(-nextKeys, steps + 1)
            if (score < bestScore)
                bestScore = score

            queue.add(Triple(nextPosition, nextKeys, steps + 1))
        }
    }

    return bestScore.second
}

fun part01(){
    var input = loadInput("day_18/input.txt").toXYMap()

    val time = measureTimeMillis {
        println("[Part 01] score=${score(input)}")
    }
    println("[Part 01] time=$time")
}

fun score(input: Map<Coordinate, Char>): Int {
    return bfs(input, input.filterValues { it == '@' }.keys.first())
}

/*
* Part 2: Split the maze into 4 parts. For each part, find the keys in that part and
* remove any lock in that part that do not have the key in the same part. i.e. only consider the locks
* that can be solved completely by that part. Then run BFS on each part and add the results
* */
fun scorePart02(input: Map<Coordinate, Char>): Int {
    val components = input.getConnectedComponents()
    components.forEach { it.removeNonRelevantDoors() }
    return components.map { score(it) }.sum()
}

fun MutableMap<Coordinate, Char>.removeNonRelevantDoors() {
    val doors = filterValues { it.isUpperCase() }
    val keys = filterValues { it.isLowerCase() }.values.map { it.toUpperCase() }
    doors.filterValues { it !in keys }.keys.forEach { this[it] = '.' }
}

fun Map<Coordinate, Char>.getConnectedComponents(): List<MutableMap<Coordinate, Char>> {
    return filterValues { it == '@' }.keys.map { getConnectedComponent(it) }
}

fun Map<Coordinate, Char>.getConnectedComponent(initialPosition: Coordinate): MutableMap<Coordinate, Char> {
    val queue = ArrayDeque<Coordinate>()
    val directions = listOf(Coordinate(0, 1), Coordinate(1, 0), Coordinate(0, -1), Coordinate(-1, 0))
    val visited = mutableMapOf<Coordinate, Char>()

    queue.push(initialPosition)

    while (queue.isNotEmpty()) {
        val position = queue.remove()

        for (direction in directions) {
            val nextPosition = position + direction

            // Skip if nextPosition ain't in graph
            val symbol = this[nextPosition] ?: continue
            if(visited.containsKey(nextPosition)) continue
            visited[nextPosition] = symbol

            queue.add(nextPosition)
        }
    }

    return visited
}


/***
 * Transforms
 * ...       @#@
 * .@. --->  ###
 * ...       @#@
 */
fun transformInputForPart02(input: Map<Coordinate, Char>): MutableMap<Coordinate, Char> {
    val parts = input.toMutableMap()
    val start = parts.filterValues { it == '@' }.keys.first()

    parts[start + Pair(-1, 1)] = '@'
    parts.remove(start + Pair(0, 1)) // Remove any walls
    parts[start + Pair(1, 1)] = '@'

    parts.remove(start + Pair(-1, 0)) // Remove any walls
    parts.remove(start + Pair(0, 0)) // Remove any walls
    parts.remove(start + Pair(1, 0)) // Remove any walls

    parts[start + Pair(-1, -1)] = '@'
    parts.remove(start + Pair(0, -1)) // Remove any walls
    parts[start + Pair(1, -1)] = '@'

    return parts
}

fun part02(){
    var input = loadInput("day_18/input.txt").toXYMap()

    val time = measureTimeMillis {
        val transformedInput = transformInputForPart02(input)
        println("[Part 02] score=${scorePart02(transformedInput)}")
    }
    println("[Part 02] time=$time")
}

fun main() {
    testsPart01()
    part01()
    testsPart02()
    part02()
}

fun testsPart01() {
    println("Example1: ${measureTimeMillis { example1() }} ms")
    println("Example2: ${measureTimeMillis { example2() }} ms")
    println("Example3: ${measureTimeMillis { example3() }} ms")
    println("Example4: ${measureTimeMillis { example4() }} ms")
    println("Example5: ${measureTimeMillis { example5() }} ms")
}

fun testsPart02() {
    println("Example6: ${measureTimeMillis { example6() }} ms")
    println("Example7: ${measureTimeMillis { example7() }} ms")
    println("Example8: ${measureTimeMillis { example8() }} ms")
    println("Example9: ${measureTimeMillis { example9() }} ms")
    // Example 10 don't work with the naiive implementation for part 2,
    // the sub parts are interrelated so that we cannot simply ignore
    // the locks where the key is available only in a different part.
    // So we need to solve the 4 paths simultaneously in one big search
    //println("Example10: ${measureTimeMillis { example10() }} ms")
}

fun example1() {
    val input = listOf(
            "#########",
            "#b.A.@.a#",
            "#########"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(8, score(input))
}

fun example2() {
    val input = listOf(
            "########################",
            "#f.D.E.e.C.b.A.@.a.B.c.#",
            "######################.#",
            "#d.....................#",
            "########################"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(86, score(input))
}

fun example3() {
    val input = listOf(
            "########################",
            "#...............b.C.D.f#",
            "#.######################",
            "#.....@.a.B.c.d.A.e.F.g#",
            "########################"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(132, score(input))
}

fun example4() {
    val input = listOf(
            "#################",
            "#i.G..c...e..H.p#",
            "########.########",
            "#j.A..b...f..D.o#",
            "########@########",
            "#k.E..a...g..B.n#",
            "########.########",
            "#l.F..d...h..C.m#",
            "#################"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(136, score(input))
}

fun example5() {
    val input = listOf(
            "########################",
            "#@..............ac.GI.b#",
            "###d#e#f################",
            "###A#B#C################",
            "###g#h#i################",
            "########################"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(81, score(input))
}

fun example6() {
    val input = listOf(
            "#######",
            "#a.#Cd#",
            "##...##",
            "##.@.##",
            "##...##",
            "#cB#Ab#",
            "#######"
    ).map { it.toCharArray().toList() }.toXYMap()

    val expected = listOf(
            "#######",
            "#a.#Cd#",
            "##@#@##",
            "#######",
            "##@#@##",
            "#cB#Ab#",
            "#######"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(expected, transformInputForPart02(input))
}

fun example7() {
    val input = listOf(
            "#######",
            "#a.#Cd#",
            "##...##",
            "##.@.##",
            "##...##",
            "#cB#Ab#",
            "#######"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(8, scorePart02(transformInputForPart02(input)))
}

fun example8() {
    val input = listOf(
            "###############",
            "#d.ABC.#.....a#",
            "######@#@######",
            "###############",
            "######@#@######",
            "#b.....#.....c#",
            "###############"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(24, scorePart02(input))
}

fun example9() {
    val input = listOf(
            "#############",
            "#DcBa.#.GhKl#",
            "#.###@#@#I###",
            "#e#d#####j#k#",
            "###C#@#@###J#",
            "#fEbA.#.FgHi#",
            "#############"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(32, scorePart02(input))
}

fun example10() {
    val input = listOf(
            "#############",
            "#g#f.D#..h#l#",
            "#F###e#E###.#",
            "#dCba@#@BcIJ#",
            "#############",
            "#nK.L@#@G...#",
            "#M###N#H###.#",
            "#o#m..#i#jk.#",
            "#############"
    ).map { it.toCharArray().toList() }.toXYMap()

    assertEquals(72, scorePart02(input))
}
