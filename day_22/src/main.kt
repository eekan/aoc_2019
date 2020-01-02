import java.io.File
import java.math.BigInteger
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

fun loadInput(fileName : String) : List<String> {
    return File(fileName).readLines().toList()
}

fun List<String>.parseInput(limit: Long): List<OperationFunction> =
        map(String::trim).map { line ->
            when {
                line.matches("cut -?\\d+".toRegex()) ->
                    OperationFunction(
                            1.toBigInteger(),
                            (-line.split(" ").last().toInt() + limit).toBigInteger(),
                            limit.toBigInteger())
                line.matches("deal with increment \\d+".toRegex()) ->
                    OperationFunction(
                            line.split(" ").last().toBigInteger(),
                            0.toBigInteger(),
                            limit.toBigInteger())
                line.matches("deal into new stack".toRegex()) ->
                    OperationFunction(
                            (-1).toBigInteger(),
                            (limit - 1).toBigInteger(),
                            limit.toBigInteger())
                else ->
                    throw IllegalArgumentException("Line is not an shuffle operation: $line")
            }
        }

data class OperationFunction(val a: BigInteger, val b: BigInteger, val limit: BigInteger)

fun OperationFunction.compose(that: OperationFunction) =
        OperationFunction((this.a * that.a).mod(limit), (this.a * that.b + this.b).mod(limit), limit)

fun OperationFunction.pow(e: Long): OperationFunction =
        when {
            e == 0L -> OperationFunction(1.toBigInteger(), 0.toBigInteger(), limit)
            e % 2L == 0L -> this.compose(this).pow(e / 2L)
            else -> this.compose(pow(e - 1L))
        }

fun OperationFunction.inverse(): OperationFunction {
    check(limit.isProbablePrime(14))

    // Use Fermat's little theorem to find inverse
    return pow(limit.toLong() - 2)
}

operator fun OperationFunction.invoke(index: Long) = (a * index.toBigInteger() + b).mod(limit).toLong()

operator fun OperationFunction.get(index: Long) = inverse()(index)

fun List<OperationFunction>.compose() =
        foldRight(OperationFunction(1.toBigInteger(), 0.toBigInteger(), first().limit)) {
            g, f -> f.compose(g)
        }

fun part01() {
    val size = 10007L
    val index = 2019L
    val operations = loadInput("day_22/input.txt").parseInput(size)

    // To what index would 2019 go?
    println("[Part 01] output=${operations.compose()(index)}")
}

fun part02() {
    val size = 119315717514047L
    val times = 101741582076661L
    val index = 2020L
    val operations = loadInput("day_22/input.txt").parseInput(size)

    // What number is on the card that ends up in position 2020?
    println("[Part 02] output=${operations.compose().pow(times)[index]}")
}

fun main() {
    testsPart01()
    println("[Part 01] time=${measureTimeMillis { part01() }}")
    println("[Part 02] time=${measureTimeMillis { part02() }}")
}

fun testsPart01() {
    println("[Example 1] time=${measureTimeMillis { example1() }}")
    println("[Example 2] time=${measureTimeMillis { example2() }}")
    println("[Example 3] time=${measureTimeMillis { example3() }}")
    println("[Example 4] time=${measureTimeMillis { example4() }}")
}

fun shuffle(deck: List<Long>, operation: OperationFunction): List<Long> {
    val result = MutableList(deck.size) { 0L }

    result.indices.forEach {
        result[operation(it.toLong()).toInt()] = deck[it]
    }

    return result
}

fun example1() {
    val expected = listOf(0, 3, 6, 9, 2, 5, 8, 1, 4, 7L)
    val deck = List(expected.size) { it.toLong() }
    val operation = listOf(
            "deal with increment 7",
            "deal into new stack",
            "deal into new stack"
    ).parseInput(expected.size.toLong()).compose()


    assertEquals(expected, shuffle(deck, operation))
}

fun example2() {
    val expected = listOf(3L, 0, 7, 4, 1, 8, 5, 2, 9, 6)
    val deck = List(expected.size) { it.toLong() }
    val operation = listOf(
            "cut 6",
            "deal with increment 7",
            "deal into new stack"
    ).parseInput(expected.size.toLong()).compose()


    assertEquals(expected, shuffle(deck, operation))
}

fun example3() {
    val expected = listOf(6L, 3, 0, 7, 4, 1, 8, 5, 2, 9)
    val deck = List(expected.size) { it.toLong() }
    val operation = listOf(
            "deal with increment 7",
            "deal with increment 9",
            "cut -2"
    ).parseInput(expected.size.toLong()).compose()


    assertEquals(expected, shuffle(deck, operation))
}

fun example4() {
    val expected = listOf(9L, 2, 5, 8, 1, 4, 7, 0, 3, 6)
    val deck = List(expected.size) { it.toLong() }
    val operation = listOf(
            "deal into new stack",
            "cut -2",
            "deal with increment 7",
            "cut 8",
            "cut -4",
            "deal with increment 7",
            "cut 3",
            "deal with increment 9",
            "deal with increment 3",
            "cut -1"
    ).parseInput(expected.size.toLong()).compose()

    assertEquals(expected, shuffle(deck, operation))
}