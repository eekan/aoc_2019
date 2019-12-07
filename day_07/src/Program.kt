import java.lang.IllegalStateException
import kotlin.math.pow

typealias Program = MutableList<Int>

private fun Int.pow(i: Int): Int {
    return toDouble().pow(i).toInt()
}

fun Program.store(instructionPointer: Int, parameterIndex: Int, value : Int) {
    // Parameters that an instruction writes to will never be in immediate mode.
    this[this[instructionPointer + parameterIndex]] = value
}

fun Program.getOpCode(instructionPointer: Int) : Int {
    // Op code is the two least significant digits
    return this[instructionPointer] % 100
}

fun Program.getParameterMode(instructionPointer: Int, parameterIndex: Int) : Int {
    // Parameter mode is stored starting at the third digit
    return (this[instructionPointer] / (100 * 10.pow(parameterIndex - 1))) % 10
}

fun Program.getParameter(instructionPointer: Int, parameterIndex: Int) : Int {
    return when (val parameterMode = getParameterMode(instructionPointer, parameterIndex)) {
        // position mode
        0 -> this[this[instructionPointer + parameterIndex]]
        // immediate mode
        1 -> this[instructionPointer + parameterIndex]
        else -> throw IllegalStateException("Unknown parameterMode encountered: $parameterMode")
    }
}
