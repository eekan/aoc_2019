import java.io.File
import kotlin.test.assertEquals

fun loadInput(fileName : String) : String {
    return File(fileName).readText().trim()
}
typealias Layer = String

fun getLayers(data: String, width: Int, height: Int): List<Layer> {
    return data.chunked(width * height)
}

fun List<Layer>.getLayeredPixels(width: Int, height: Int): List<List<Char>> {
    return (0 until width * height).map { index -> this.map { it[index] } }
}

private const val BLACK = '0'
private const val WHITE = '1'
private const val TRANSPARENT = '2'

fun renderImage(layers: List<Layer>, width: Int, height: Int) : List<String> {
    if (layers.isEmpty()) return listOf()

    // Calculate color of each pixel
    val pixels = layers.getLayeredPixels(width, height)
            .map { it.firstOrNull() { c -> c == BLACK || c == WHITE } ?: TRANSPARENT }

    // Order pixels in list of 'height' strings of length 'width'
    return pixels.chunked(width).map { it.joinToString(separator = "") }
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

fun example2() {
    var layers = getLayers("0222112222120000", 2, 2)
    assertEquals(listOf("01", "10"), renderImage(layers, 2, 2))
}

fun part02() {
    val width = 25
    val height = 6
    val layers = getLayers(loadInput("day_08/input.txt"), width, height)
    val result = renderImage(layers, width, height)
            .joinToString(separator = "\n")
            .replace('0', ' ')
    println("[Part 02] Rendered image:\n$result")
}

fun main() {
    example1()
    part01()
    example2()
    part02()
}