import java.io.File
import java.lang.IllegalStateException
import java.lang.Long.max
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.test.assertEquals

data class Substance(val name: String, val quantity: Long)

fun String.toSubstance(): Substance {
    val (quantity, name) = this.trim().split(" ")
    return Substance(name, quantity.toLong())
}

fun String.toSubstances(): List<Substance> {
    return this.split(",").map { it.toSubstance() }
}

fun List<String>.getSubstances() : Map<String, Pair<Long, List<Substance>>> {
    return map {
        val (input, output) = it.split("=>")
            val s = output.toSubstance()
            s.name to Pair(s.quantity, input.toSubstances())
    }.toMap()
}

fun MutableMap<String, Long>.consume(substance: Substance): Substance {
    val available = getOrDefault(substance.name, 0L)
    val leftToProduce = Substance(substance.name, max(substance.quantity - available, 0L))
    this[substance.name] = max(available - substance.quantity, 0L)

    return leftToProduce
}

fun produce(recipes: Map<String, Pair<Long, List<Substance>>>, remainings : MutableMap<String, Long>, substance: Substance): Long {
    if (substance.name == "ORE" || substance.quantity == 0L) return substance.quantity

    val toProduce = remainings.consume(substance)
    val recipe = recipes[substance.name] ?: throw IllegalStateException("No recipe for ${substance.name}")

    val n = ceil(toProduce.quantity.toDouble() / recipe.first.toDouble()).toInt()
    val remaining = n * recipe.first - toProduce.quantity

    remainings[substance.name] = remainings.getOrDefault(substance.name, 0) + remaining

    return recipe.second.map { s ->
        produce(recipes, remainings, Substance(s.name, s.quantity * n))
    }.sum()
}

fun getOresPerFuel(recipes: Map<String, Pair<Long, List<Substance>>>): Long {
    return produce(recipes, mutableMapOf(), Substance("FUEL", 1L))
}

fun getProducibleFuel(recipes: Map<String, Pair<Long, List<Substance>>>, ores: Long): Long {

    var left = 0L
    var right = ores
    var mid = -1L

    while (left <= right) {
        mid = floor((left.toDouble() + right.toDouble()) / 2).toLong()

        val usedOres =  produce(recipes, mutableMapOf(), Substance("FUEL", mid))

        if (usedOres < ores) {
            left = mid + 1
        } else if (usedOres > ores) {
            right = mid -1
        } else {
            return mid
        }
    }

    return mid -1
}

fun loadInput(fileName : String) : List<String> {
    return File(fileName).readLines().toList()
}

fun part01() {
    val input = loadInput("day_14/input.txt")

    println("[Part 01] Output: ${getOresPerFuel(input.getSubstances())}")
}

fun part02() {
    val input = loadInput("day_14/input.txt")
    val availableOREs = 1000000000000

    println("[Part 02] Output: ${getProducibleFuel(input.getSubstances(), availableOREs)}")
}

fun main() {
    tests()
    part01()
    part02()
}

fun tests() {
    example0()
    example1()
    example2()
    example3()
    example4()
    example5()
    example6()
    example7()
    example8()
}


fun example0() {
    val input = listOf(
            "10 ORE => 10 A",
            "1 ORE => 1 B",
            "7 A, 1 B => 1 C",
            "7 A, 1 C => 1 D",
            "7 A, 1 D => 1 E",
            "7 A, 1 E => 1 FUEL"
    ).getSubstances()

    val remainings = mutableMapOf<String, Long>()

    assertEquals(10, produce(input, mutableMapOf(), Substance("A", 10)))
    assertEquals(10, produce(input, mutableMapOf(), Substance("B", 10)))
    assertEquals(11, produce(input,  mutableMapOf(), Substance("B", 11)))
    assertEquals(11, produce(input,  mutableMapOf(), Substance("C", 1)))
    assertEquals(21, produce(input,  mutableMapOf(), Substance("D", 1)))
    assertEquals(31, produce(input,  mutableMapOf(), Substance("E", 1)))
    assertEquals(31, produce(input,  remainings, Substance("FUEL", 1)))
    assertEquals(mutableMapOf("A" to 2L), remainings.filterValues { it > 0 })
}

fun example1() {
    val input = listOf(
            "10 ORE => 10 A",
            "1 ORE => 1 B",
            "7 A, 1 B => 1 C",
            "7 A, 1 C => 1 D",
            "7 A, 1 D => 1 E",
            "7 A, 1 E => 1 FUEL"
    ).getSubstances()

    assertEquals(31, getOresPerFuel(input))
}

fun example2() {
    val input = listOf(
            "9 ORE => 2 A",
            "8 ORE => 3 B",
            "7 ORE => 5 C",
            "3 A, 4 B => 1 AB",
            "5 B, 7 C => 1 BC",
            "4 C, 1 A => 1 CA",
            "2 AB, 3 BC, 4 CA => 1 FUEL"
    ).getSubstances()

    assertEquals(165, getOresPerFuel(input))
}


fun example3() {
    val input = listOf(
            "157 ORE => 5 NZVS",
            "165 ORE => 6 DCFZ",
            "44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL",
            "12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ",
            "179 ORE => 7 PSHF",
            "177 ORE => 5 HKGWZ",
            "7 DCFZ, 7 PSHF => 2 XJWVT",
            "165 ORE => 2 GPVTF",
            "3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT"
    ).getSubstances()

    assertEquals(13312, getOresPerFuel(input))
}

fun example4() {
    val input = listOf(
            "2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG",
            "17 NVRVD, 3 JNWZP => 8 VPVL",
            "53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL",
            "22 VJHF, 37 MNCFX => 5 FWMGM",
            "139 ORE => 4 NVRVD",
            "144 ORE => 7 JNWZP",
            "5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC",
            "5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV",
            "145 ORE => 6 MNCFX",
            "1 NVRVD => 8 CXFTF",
            "1 VJHF, 6 MNCFX => 4 RFSQX",
            "176 ORE => 6 VJHF"
    ).getSubstances()

    assertEquals(180697, getOresPerFuel(input))
}

fun example5() {
    val input = listOf(
            "171 ORE => 8 CNZTR",
            "7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL",
            "114 ORE => 4 BHXH",
            "14 VRPVC => 6 BMBT",
            "6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL",
            "6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT",
            "15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW",
            "13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW",
            "5 BMBT => 4 WPTQ",
            "189 ORE => 9 KTJDG",
            "1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP",
            "12 VRPVC, 27 CNZTR => 2 XDBXC",
            "15 KTJDG, 12 BHXH => 5 XCVML",
            "3 BHXH, 2 VRPVC => 7 MZWV",
            "121 ORE => 7 VRPVC",
            "7 XCVML => 6 RJRHP",
            "5 BHXH, 4 VRPVC => 5 LTCX"
    ).getSubstances()

    assertEquals(2210736, getOresPerFuel(input))
}

fun example6() {
    val input = listOf(
            "157 ORE => 5 NZVS",
            "165 ORE => 6 DCFZ",
            "44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL",
            "12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ",
            "179 ORE => 7 PSHF",
            "177 ORE => 5 HKGWZ",
            "7 DCFZ, 7 PSHF => 2 XJWVT",
            "165 ORE => 2 GPVTF",
            "3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT"
    ).getSubstances()
    assertEquals(82892753, getProducibleFuel(input, 1000000000000))
}

fun example7() {
    val input = listOf(
            "2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG",
            "17 NVRVD, 3 JNWZP => 8 VPVL",
            "53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL",
            "22 VJHF, 37 MNCFX => 5 FWMGM",
            "139 ORE => 4 NVRVD",
            "144 ORE => 7 JNWZP",
            "5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC",
            "5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV",
            "145 ORE => 6 MNCFX",
            "1 NVRVD => 8 CXFTF",
            "1 VJHF, 6 MNCFX => 4 RFSQX",
            "176 ORE => 6 VJHF"
    ).getSubstances()
    assertEquals(5586022, getProducibleFuel(input, 1000000000000))
}

fun example8() {
    val input = listOf(
            "171 ORE => 8 CNZTR",
            "7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL",
            "114 ORE => 4 BHXH",
            "14 VRPVC => 6 BMBT",
            "6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL",
            "6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT",
            "15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW",
            "13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW",
            "5 BMBT => 4 WPTQ",
            "189 ORE => 9 KTJDG",
            "1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP",
            "12 VRPVC, 27 CNZTR => 2 XDBXC",
            "15 KTJDG, 12 BHXH => 5 XCVML",
            "3 BHXH, 2 VRPVC => 7 MZWV",
            "121 ORE => 7 VRPVC",
            "7 XCVML => 6 RJRHP",
            "5 BHXH, 4 VRPVC => 5 LTCX"
    ).getSubstances()
    assertEquals(460664, getProducibleFuel(input, 1000000000000))
}

