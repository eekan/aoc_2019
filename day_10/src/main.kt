import java.io.File
import kotlin.math.PI
import kotlin.math.atan
import kotlin.test.assertEquals

private const val ASTEROID = '#'

fun List<String>.toAsteroidMap(): List<List<Char>> {
    return map { it.toCharArray().toList() }
}

fun List<List<Char>>.getAsteroidCoordinates(): List<Pair<Int, Int>> {
    return indices.flatMap {y ->
        this[y].indices.map { x ->
            Pair(x, y)
        }
    }.filter { (x, y) -> this[y][x] == ASTEROID }
}

fun List<Pair<Int, Int>>.getAngleOfOtherAsteroids(asteroid: Pair<Int, Int>): Map<Double, List<Pair<Int, Int>>> {
    return this.filter { it != asteroid }
            .map{ (x, y) -> Pair(x - asteroid.first, y - asteroid.second)}
            .sortedBy { (x, y) -> x*x + y*y }
            .groupBy { getAngle(it) }
            .map {(k, v) -> k to v.map { it.add(asteroid) }}
            .toMap()
}

fun arctan(a: Int, b: Int): Double {
    return atan(a.toDouble()/ b.toDouble())
}

fun getAngle(p : Pair<Int,Int> ): Double {
    val (x, y) = p
    // Shift PI/2 so that upwards is considered lowest rotation
    return (PI/2 +
                if (x >= 0)
                    2*PI + arctan(y, x)
                else
                    PI + arctan(y, x)).rem(2 * PI)
}

fun getBestStationLocation(input: List<List<Char>>): Pair<Int, Pair<Int, Int>> {
    val asteroids = input.getAsteroidCoordinates()

    /* The first asteroid each group of the result getAngleOfOtherAsteroids
     * is visible from 'it' */
    return asteroids
            .map { asteroids.getAngleOfOtherAsteroids(it).keys.size to it}
            .maxBy { it.first } ?: -1 to (-1 to -1)
}

fun Pair<Int, Int>.add(that: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first + that.first, this.second + that.second)
}

fun getVaporizedAsteroids(input: List<List<Char>>): List<Pair<Int, Int>> {
    val asteroids = input.getAsteroidCoordinates()
    val station = getBestStationLocation(input).second
    val angles = asteroids.getAngleOfOtherAsteroids(station).toSortedMap()

    // take one element from each group per each iteration
    var res = listOf<Pair<Int, Int>>()
    var len = angles.values.map { it.size }.max() ?: 0
    for (i in 0 until len) {
        for(key in angles.keys.toList()) {
            res += angles[key]?.getOrNull(i) ?: continue
        }
    }
    return res
}

fun loadInput(fileName : String): List<List<Char>> {
    return File(fileName).readLines().map{ it.trim().toCharArray().toList()}
}

fun part01() {
    var input = loadInput("day_10/input.txt")
    println("[Part 01] Output: ${getBestStationLocation(input)}")
}

fun part02() {
    var input = loadInput("day_10/input.txt")
    val vaporized= getVaporizedAsteroids(input)
    val cord200 = vaporized[199]
    val res = cord200.first * 100 + cord200.second
    println("[Part 02] Output: ${res}")
}

fun main(){
    tests()
    part01()
    part02()
}

fun example1() {
    val map = listOf(
            ".#..#",
            ".....",
            "#####",
            "....#",
            "...##").toAsteroidMap()
    val expectedResult = Pair(8, Pair(3,4))
    assertEquals(expectedResult, getBestStationLocation(map))
}

fun example2() {
    val map = listOf(
            "......#.#.",
            "#..#.#....",
            "..#######.",
            ".#.#.###..",
            ".#..#.....",
            "..#....#.#",
            "#..#....#.",
            ".##.#..###",
            "##...#..#.",
            ".#....####").toAsteroidMap()
    val expectedResult = Pair(33, Pair(5,8))
    assertEquals(expectedResult, getBestStationLocation(map))
}

fun example3() {
    val map = listOf(
            "#.#...#.#.",
            ".###....#.",
            ".#....#...",
            "##.#.#.#.#",
            "....#.#.#.",
            ".##..###.#",
            "..#...##..",
            "..##....##",
            "......#...",
            ".####.###.").toAsteroidMap()
    val expectedResult = Pair(35, Pair(1,2))
    assertEquals(expectedResult, getBestStationLocation(map))
}

fun example4() {
    val map = listOf(
            ".#..#..###",
            "####.###.#",
            "....###.#.",
            "..###.##.#",
            "##.##.#.#.",
            "....###..#",
            "..#.#..#.#",
            "#..#.#.###",
            ".##...##.#",
            ".....#.#..").toAsteroidMap()
    val expectedResult = Pair(41, Pair(6,3))
    assertEquals(expectedResult, getBestStationLocation(map))
}

fun example5() {
    val map = listOf(
            ".#..##.###...#######",
            "##.############..##.",
            ".#.######.########.#",
            ".###.#######.####.#.",
            "#####.##.#.##.###.##",
            "..#####..#.#########",
            "####################",
            "#.####....###.#.#.##",
            "##.#################",
            "#####.##.###..####..",
            "..######..##.#######",
            "####.##.####...##..#",
            ".#####..#.######.###",
            "##...#.##########...",
            "#.##########.#######",
            ".####.#.###.###.#.##",
            "....##.##.###..#####",
            ".#.#.###########.###",
            "#.#.#.#####.####.###",
            "###.##.####.##.#..##").toAsteroidMap()
    val expectedResult = Pair(210, Pair(11,13))
    assertEquals(expectedResult, getBestStationLocation(map))
}

fun example6() {
    val map = listOf(
            ".#..##.###...#######",
            "##.############..##.",
            ".#.######.########.#",
            ".###.#######.####.#.",
            "#####.##.#.##.###.##",
            "..#####..#.#########",
            "####################",
            "#.####....###.#.#.##",
            "##.#################",
            "#####.##.###..####..",
            "..######..##.#######",
            "####.##.####...##..#",
            ".#####..#.######.###",
            "##...#.##########...",
            "#.##########.#######",
            ".####.#.###.###.#.##",
            "....##.##.###..#####",
            ".#.#.###########.###",
            "#.#.#.#####.####.###",
            "###.##.####.##.#..##").toAsteroidMap()
    val expectedResult = Pair(210, Pair(11,13))
    assertEquals(expectedResult, getBestStationLocation(map))

    val vaporized = getVaporizedAsteroids(map)
    assertEquals(299, vaporized.size)
    assertEquals(Pair(11,12), vaporized[0])
    assertEquals(Pair(12,1), vaporized[1])
    assertEquals(Pair(12,2), vaporized[2])
    assertEquals(Pair(12,8), vaporized[9])
    assertEquals(Pair(16,0), vaporized[19])
    assertEquals(Pair(10,16), vaporized[99])
    assertEquals(Pair(9,6), vaporized[198])
    assertEquals(Pair(8,2), vaporized[199])
    assertEquals(Pair(10,9), vaporized[200])
    assertEquals(Pair(11,1), vaporized[298])
}

fun example7() {
    val map = listOf(
            ".#....#####...#..",
            "##...##.#####..##",
            "##...#...#.#####.",
            "..#.....#...###..",
            "..#.#.....#....##").toAsteroidMap()
    val expectedResult = Pair(30, Pair(8,3))
    assertEquals(expectedResult, getBestStationLocation(map))

    val expected = listOf(
            Pair(8,1), Pair(9,0), Pair(9,1), Pair(10,0), Pair(9,2), Pair(11,1), Pair(12,1), Pair(11,2), Pair(15,1),
            Pair(12,2), Pair(13,2), Pair(14,2), Pair(15,2), Pair(12,3), Pair(16,4), Pair(15,4), Pair(10,4), Pair(4,4),
            Pair(2,4), Pair(2,3), Pair(0,2), Pair(1,2), Pair(0,1), Pair(1,1), Pair(5,2), Pair(1,0), Pair(5,1),
            Pair(6,1), Pair(6,0), Pair(7,0), Pair(8,0), Pair(10,1), Pair(14, 0), Pair(16,1), Pair(13,3), Pair(14,3))
    val vaporized = getVaporizedAsteroids(map)
    assertEquals(expected, vaporized)
}

fun tests() {
    example1()
    example2()
    example3()
    example4()
    example5()
    example6()
    example7()
}