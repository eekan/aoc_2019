import java.io.File
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

val basePattern = listOf(0, 1, 0, -1)

fun String.toInputSequence(): List<Int> {
    return this.chunked(1).map { it.toInt() }
}

fun loadInput(fileName : String) : List<Int> {
    return File(fileName).readText().trim().toInputSequence()
}

/**
 * We have
 * n = 1 -> 1 0 -1 0
 * n = 2 -> 0 1 1 0 0 -1 -1 0
 * n = 3 -> 0 0 1 1 1 0 0 0 -1 -1 -1 0
 * n = 4 -> 0 0 0 1 1 1 1 0 0 0 0 -1 -1 -1 -1 0
 * ...
 * n = N -> (N-1) 0's N 1's N 0's N -1's 0
 */
fun generatePattern(n: Int): List<Int> {
    val seq = basePattern.flatMap { i ->
        List(n) { i }
    }
    return seq.drop(1) + seq.take(1)
}

fun flawedFrequencyTransmission(elements: List<Int>, index: Int) : Int {
    val pattern = generatePattern(index)
    return elements.indices.map { i->
        elements[i] * pattern[i.rem(pattern.size)]
    }.sum().rem(10).absoluteValue
}

fun flawedFrequencyTransmission(elements: List<Int>): List<Int> {
    return elements.indices.map { i ->
        flawedFrequencyTransmission(elements, i + 1)
    }
}


fun flawedFrequencyTransmissionPart2(elements: List<Int>): List<Int> {
    return elements.indices.map { i ->
        flawedFrequencyTransmission(elements, i + 1)
    }
}

/**
 * The pattern for digit N, where N > length/2, will be N-1 0's followed by only 1's (see generatePattern()). This
 * implies that the last digit at index R will have a pattern of R-1 0's followed by a single 1, i.e. its value
 * is the same through all phases. The digit at index R-1 then has the pattern R-2 0's followed by two 1's,
 * so its value is the sum of the R-1 and R values from the previous phase. We can use the above observations to reduce
 * both the range of and the number of steps in the calculations:
 * - The OFFSET first digits can be ignored, since all digits in the (index) range [OFFSET, END] all have patterns
 *   starting with at least OFFSET-1 0's (so the range [0, OFFSET) has no effect on the calculation).
 * - For each n in [OFFSET, END] we have, Value'[n] = Mod10(Value[n] + Value[n+1]+...) = Mod10(Value[n] + Value'[n+1]),
 *   that means that we effectively can calculate a phase as a sum series from the last digit up to OFFSET.
 */
fun fftPart2(inputValues: List<Int>): List<Int> {
    val offset = inputValues.take(7).joinToString(separator = "").toInt()

    /* Offset must be greater than size / 2 for optimizations to hold */
    assertTrue { offset > 10000 * inputValues.size / 2 }

    val elements = (1..10000).flatMap { inputValues }.drop(offset).toMutableList()
    val indexes = elements.size-2 downTo 0 // Skip last element, it will always be the same

    repeat(100) {
        for (i in indexes) {
            elements[i] = (elements[i] + elements[i+1]) % 10
        }
    }

    return elements
}

fun List<Long>.toLong(): Long {
    return joinToString(separator = "").toLong()
}

fun Int.toList(): List<Int> {
    return toString().toInputSequence()
}

fun part01() {
    var input = loadInput("day_16/input.txt")

    var value = input
    repeat(100) {
        value = flawedFrequencyTransmission(value)
    }
    println("[Part 01] Output: ${value.take(8).joinToString(separator = "")}")
}


fun part02() {
    var input = loadInput("day_16/input.txt")
    println("[Part 02] Output: ${fftPart2(input).take(8).joinToString(separator = "")}")
}

fun main() {
    tests()
    part01()
    part02()
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

fun example1() {
    var value = "12345678".toInputSequence()

    value = flawedFrequencyTransmission(value)
    assertEquals(48226158.toList(), value)

    value = flawedFrequencyTransmission(value)
    assertEquals(34040438.toList(), value)

    value = flawedFrequencyTransmission(value)
    assertEquals("03415518".toInputSequence(), value)

    value = flawedFrequencyTransmission(value)
    assertEquals("01029498".toInputSequence(), value)
}

fun example2() {
    var value = "80871224585914546619083218645595".toInputSequence()

    repeat(100) {
        value = flawedFrequencyTransmission(value)
    }
    assertEquals(24176176.toList(), value.take(8))
}

fun example3() {
    var value = "19617804207202209144916044189917".toInputSequence()

    repeat(100) {
        value = flawedFrequencyTransmission(value)
    }
    assertEquals(73745418.toList(), value.take(8))
}

fun example4() {
    var value = "69317163492948606335995924319873".toInputSequence()

    repeat(100) {
        value = flawedFrequencyTransmission(value)
    }
    assertEquals(52432133.toList(), value.take(8))
}

fun example5() {
    var value = "03036732577212944063491565474664".toInputSequence()
    assertEquals(84462026.toList(), fftPart2(value).take(8))
}

fun example6() {
    var value = "02935109699940807407585447034323".toInputSequence()

    assertEquals(78725270.toList(), fftPart2(value).take(8))
}

fun example7() {
    var value = "03081770884921959731165446850517".toInputSequence()

    assertEquals(53553731.toList(), fftPart2(value).take(8))
}