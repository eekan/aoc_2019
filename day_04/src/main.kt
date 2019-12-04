import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun isPassword(value : String) : Boolean {
    // It is a six-digit number.
    if (value.length != 6) return false
    // At least one digit is repeated
    if (value.toSet().size > 5) return false
    // Going left to right: digits never decrease (implies repeats are adjacent)
    if (value.toList().zipWithNext().any{(a, b) -> a > b }) return false
    // All the above is true
    return true
}

fun isPassword2(value : String) : Boolean {
    // isPassword() is true and there is a digit that is repeated exactly once
    return isPassword(value) && value.toList().groupingBy { it }.eachCount().any { it.value == 2 }
}

fun part1(start: Int, end: Int) {
    val passwordsInRange = (start..end).count { isPassword(it.toString()) }
    println("[PART 1]: Number of passwords in [${start}, ${end}]: $passwordsInRange")
}

fun part2(start: Int, end: Int) {
    val passwordsInRange = (start..end).count { isPassword2(it.toString()) }
    println("[PART 2]: Number of passwords in [${start}, ${end}]: $passwordsInRange")
}

fun main() {
    examplesPart1()
    part1(372304, 847060)

    examplesPart2()
    part2(372304, 847060)
}

fun examplesPart1() {
    assertTrue { isPassword(111111.toString()) }
    assertFalse { isPassword(223450.toString()) }
    assertFalse { isPassword(123789.toString()) }
}

fun examplesPart2() {
    assertTrue { isPassword2(112233.toString()) }
    assertFalse { isPassword2(123444.toString()) }
    assertTrue { isPassword(111122.toString()) }
}
