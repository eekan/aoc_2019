import java.io.File
import kotlin.math.abs
import kotlin.math.sign
import kotlin.test.assertEquals

data class Moon(var position: MutableList<Long>, var velocity: MutableList<Long> = MutableList(3) { 0L })

fun Moon.applyGravity(that: Moon) {
    position.indices.forEach {
        val change = (this.position[it] - that.position[it]).sign.toLong()
        this.velocity[it] -= change
        that.velocity[it] += change
    }
}

fun Moon.applyVelocity() {
    position.indices.forEach { position[it] += velocity[it] }
}

fun <T> List<T>.getAllIndicesPairs(): List<Pair<Int, Int>> {
    return indices.flatMap {i ->
        indices.map { j-> Pair(i, j)  }
                .filter { (a, b) -> a < b }
    }
}

fun simulateStep(moons: List<Moon>, indices: List<Pair<Int, Int>>) {
    indices.forEach { (i, j) ->
        moons[i].applyGravity(moons[j])
    }

    moons.forEach { it.applyVelocity() }
}

fun toMoon(moonString: String): Moon {
    var pattern = "pos=<x=\\s*(-?\\d+), y=\\s*(-?\\d+), z=\\s*(-?\\d+)>, vel=<x=\\s*(-?\\d+), y=\\s*(-?\\d+), z=\\s*(-?\\d+)>".toRegex()
    var result = pattern.find(moonString)
    if (result != null) {
        return Moon(
                mutableListOf(
                    result.groupValues[1].toLong(),
                    result.groupValues[2].toLong(),
                    result.groupValues[3].toLong()),
                mutableListOf(
                    result.groupValues[4].toLong(),
                    result.groupValues[5].toLong(),
                    result.groupValues[6].toLong()))
    } else {
        pattern = "<x=\\s*(-?\\d+), y=\\s*(-?\\d+), z=\\s*(-?\\d+)>".toRegex()
        result = pattern.find(moonString) ?:
                throw IllegalArgumentException("String does not represent a moon: $moonString")
        return Moon(
                mutableListOf(
                        result.groupValues[1].toLong(),
                        result.groupValues[2].toLong(),
                        result.groupValues[3].toLong()))
    }
}

fun List<Long>.energy() : Long {
    return map { abs(it) }.sum()
}

fun List<Moon>.calculateTotalEnergy(): Long {
    return this.map {moon ->
        moon.position.energy() * moon.velocity.energy()
    }.sum()
}

fun Long.gcd(that: Long): Long {
    return if (that == 0L) this
    else that.gcd(rem(that))
}

fun Long.lcm(that: Long): Long {
    return abs(this * that) / this.gcd(that)
}

fun Collection<Long>.lcm() : Long {
    return this.fold(1L) { acc, value -> acc.lcm(value) }
}

fun stepUntilRepeat(moons: List<Moon>): Long {
    val indices = moons.getAllIndicesPairs()
    var steps = 0L
    val zeros = moons.map { Moon(it.position.toMutableList(), it.velocity.toMutableList()) }

    val periods = mutableMapOf<Int, Long>()
    do {
        simulateStep(moons, indices)
        steps++

        // Check periods for each axis of the system
        (0..2).forEach {axis ->
            if (moons.all { it.velocity[axis] == 0L } && !periods.containsKey(axis)) {
                // The moons follows an circular path, so there will be two points per period where all have 0 velocity.
                // This is the second one in a period, so it is in the middle of the period, hence * 2 to get the full period
                periods[axis] = 2*steps
            }
        }

        // Stop when the period is known for all 3 axis
    } while (periods.size != 3)

    return periods.values.lcm()
}

fun loadInput(fileName : String) : List<String> {
    return File(fileName).readLines().toList()
}

fun part01() {
    var moons = loadInput("day_12/input.txt").map{toMoon(it)}
    val indices = moons.getAllIndicesPairs()

    (1..1000).forEach { _ ->
        simulateStep(moons, indices)
    }

    println("[Part 01] Total energy: ${ moons.calculateTotalEnergy()}")
}

fun part02() {
    var moons = loadInput("day_12/input.txt").map{toMoon(it)}
    var steps = stepUntilRepeat(moons)
    println("[Part 02] Total steps: $steps")
}

fun main() {
    tests()
    part01()
    part02()
}

fun tests() {
    //example1()
    //example2()
    example3()
}

fun example1() {
    var moons = listOf(
            "<x=-1, y=0, z=2>",
            "<x=2, y=-10, z=-7>",
            "<x=4, y=-8, z=8>",
            "<x=3, y=5, z=-1>").map{toMoon(it)}
    val initialMoons = listOf("pos=<x=-1, y=  0, z= 2>, vel=<x= 0, y= 0, z= 0>",
            "pos=<x= 2, y=-10, z=-7>, vel=<x= 0, y= 0, z= 0>",
            "pos=<x= 4, y= -8, z= 8>, vel=<x= 0, y= 0, z= 0>",
            "pos=<x= 3, y=  5, z=-1>, vel=<x= 0, y= 0, z= 0>").map { toMoon(it) }
    assertEquals(initialMoons, moons)

    val expectedResults = listOf(
            // After 1 step:
            listOf("pos=<x= 2, y=-1, z= 1>, vel=<x= 3, y=-1, z=-1>",
                    "pos=<x= 3, y=-7, z=-4>, vel=<x= 1, y= 3, z= 3>",
                    "pos=<x= 1, y=-7, z= 5>, vel=<x=-3, y= 1, z=-3>",
                    "pos=<x= 2, y= 2, z= 0>, vel=<x=-1, y=-3, z= 1>"),

            // After 2 steps:
            listOf("pos=<x= 5, y=-3, z=-1>, vel=<x= 3, y=-2, z=-2>",
                    "pos=<x= 1, y=-2, z= 2>, vel=<x=-2, y= 5, z= 6>",
                    "pos=<x= 1, y=-4, z=-1>, vel=<x= 0, y= 3, z=-6>",
                    "pos=<x= 1, y=-4, z= 2>, vel=<x=-1, y=-6, z= 2>"),
            // After 3 steps:
            listOf("pos=<x= 5, y=-6, z=-1>, vel=<x= 0, y=-3, z= 0>",
                    "pos=<x= 0, y= 0, z= 6>, vel=<x=-1, y= 2, z= 4>",
                    "pos=<x= 2, y= 1, z=-5>, vel=<x= 1, y= 5, z=-4>",
                    "pos=<x= 1, y=-8, z= 2>, vel=<x= 0, y=-4, z= 0>"),
            // After 4 steps:
            listOf("pos=<x= 2, y=-8, z= 0>, vel=<x=-3, y=-2, z= 1>",
                    "pos=<x= 2, y= 1, z= 7>, vel=<x= 2, y= 1, z= 1>",
                    "pos=<x= 2, y= 3, z=-6>, vel=<x= 0, y= 2, z=-1>",
                    "pos=<x= 2, y=-9, z= 1>, vel=<x= 1, y=-1, z=-1>"),
            // After 5 steps:
            listOf("pos=<x=-1, y=-9, z= 2>, vel=<x=-3, y=-1, z= 2>",
                    "pos=<x= 4, y= 1, z= 5>, vel=<x= 2, y= 0, z=-2>",
                    "pos=<x= 2, y= 2, z=-4>, vel=<x= 0, y=-1, z= 2>",
                    "pos=<x= 3, y=-7, z=-1>, vel=<x= 1, y= 2, z=-2>"),
            // After 6 steps:
            listOf("pos=<x=-1, y=-7, z= 3>, vel=<x= 0, y= 2, z= 1>",
                    "pos=<x= 3, y= 0, z= 0>, vel=<x=-1, y=-1, z=-5>",
                    "pos=<x= 3, y=-2, z= 1>, vel=<x= 1, y=-4, z= 5>",
                    "pos=<x= 3, y=-4, z=-2>, vel=<x= 0, y= 3, z=-1>"),
            // After 7 steps:
            listOf("pos=<x= 2, y=-2, z= 1>, vel=<x= 3, y= 5, z=-2>",
                    "pos=<x= 1, y=-4, z=-4>, vel=<x=-2, y=-4, z=-4>",
                    "pos=<x= 3, y=-7, z= 5>, vel=<x= 0, y=-5, z= 4>",
                    "pos=<x= 2, y= 0, z= 0>, vel=<x=-1, y= 4, z= 2>"),
            // After 8 steps:
            listOf("pos=<x= 5, y= 2, z=-2>, vel=<x= 3, y= 4, z=-3>",
                    "pos=<x= 2, y=-7, z=-5>, vel=<x= 1, y=-3, z=-1>",
                    "pos=<x= 0, y=-9, z= 6>, vel=<x=-3, y=-2, z= 1>",
                    "pos=<x= 1, y= 1, z= 3>, vel=<x=-1, y= 1, z= 3>"),
            // After 9 steps:
            listOf("pos=<x= 5, y= 3, z=-4>, vel=<x= 0, y= 1, z=-2>",
                    "pos=<x= 2, y=-9, z=-3>, vel=<x= 0, y=-2, z= 2>",
                    "pos=<x= 0, y=-8, z= 4>, vel=<x= 0, y= 1, z=-2>",
                    "pos=<x= 1, y= 1, z= 5>, vel=<x= 0, y= 0, z= 2>"),
            // After 10 steps:
            listOf("pos=<x= 2, y= 1, z=-3>, vel=<x=-3, y=-2, z= 1>",
                    "pos=<x= 1, y=-8, z= 0>, vel=<x=-1, y= 1, z= 3>",
                    "pos=<x= 3, y=-6, z= 1>, vel=<x= 3, y= 2, z=-3>",
                    "pos=<x= 2, y= 0, z= 4>, vel=<x= 1, y=-1, z=-1>")
        ).map { it.map{ line -> toMoon(line) }}

    val indices = moons.getAllIndicesPairs()
    for (expected in expectedResults) {
        simulateStep(moons, indices)
        assertEquals(expected, moons)
    }

    assertEquals(179, moons.calculateTotalEnergy())
}

fun example2() {
    var moons = listOf(
            "<x=-1, y=0, z=2>",
            "<x=2, y=-10, z=-7>",
            "<x=4, y=-8, z=8>",
            "<x=3, y=5, z=-1>").map { toMoon(it) }
    var steps = stepUntilRepeat(moons)
    assertEquals(2772, steps)
}

fun example3() {
    var moons = listOf(
            "<x=-8, y=-10, z=0>",
            "<x=5, y=5, z=10>",
            "<x=2, y=-7, z=3>",
            "<x=9, y=-8, z=-3>").map { toMoon(it) }
    var steps = stepUntilRepeat(moons)
    assertEquals(4686774924, steps)
}





