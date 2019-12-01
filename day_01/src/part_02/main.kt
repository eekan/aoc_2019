package part_02

import java.io.File

fun calculateFuelRequirement(mass : Int) : Int {
    val requirement = mass / 3 - 2
    if (requirement > 0) return requirement + calculateFuelRequirement(requirement)
    return 0
}

fun calculateFuelRequirement(moduleMasses : List<Int>) : Int {
    return moduleMasses.map { calculateFuelRequirement(it) }.sum()
}

fun calculateExample() {
    val masses : List<Int> = listOf(14, 1969, 100756)
    val expected : Int = listOf(2, 966, 50346).sum()
    println("calculateFuelRequirement=${calculateFuelRequirement(masses)}")
    println("expected=${expected}")
}

fun main() {
//    calculateExample()

    var inputMasses : List<Int> = File("day_01/input.txt").readLines().map { it.toInt() }
    println("Result=${calculateFuelRequirement(inputMasses)}")

}