import java.io.File

fun calculateFuelRequirement(moduleMasses : List<Int>) : Int {
    return moduleMasses.map { it / 3 - 2 }.sum()
}

fun calculateExample() {
    val masses : List<Int> = listOf(12, 14, 1969, 100756)
    val expected : Int = listOf(2, 2, 654, 33583).sum()
    println("calculateFuelRequirement=${calculateFuelRequirement(masses)}")
    println("expected=${expected}")
}

fun main() {
    //calculateExample()

    var inputMasses : List<Int> = File("01/input.txt").readLines().map { it.toInt() }
    println("Result=${calculateFuelRequirement(inputMasses)}")

}