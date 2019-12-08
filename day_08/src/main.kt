import java.io.File
import kotlin.test.assertEquals

fun loadInput(fileName : String) : String {
    return File(fileName).readText().trim()
}
typealias Layer = String

fun getLayers(data: String, width: Int, height: Int): List<Layer> {
    return data.chunked(width * height)
}

fun example1() {
    var input = "123456789012"
    var layers = getLayers(input, 3, 2)
    println("Layers=$layers")
    assertEquals(listOf("123456", "789012"), getLayers(input, 3, 2))
}

fun part01() {
    val layers = getLayers(loadInput("day_08/input.txt"), 25, 6)
    val layerWithFewestZeros = layers.minBy { it.count { d -> d == '0' } } ?: ""
    val result = layerWithFewestZeros.count { it == '1' } * layerWithFewestZeros.count { it == '2' }

    println("[Part 01] Product of the number of 1's and 2's in the layer with fewest 0's is $result")
}

fun main() {
    example1()
    part01()
}