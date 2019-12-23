import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException
import kotlin.math.pow

class Memory(program: Program) {
    private var data = ArrayList<Long>(program)

    operator fun set(address: Long, value: Long) {
        if (address >= data.size) {
            data.addAll(List<Long>((address- data.size + 1).toInt()) { 0 })
        }
        data[address.toInt()] = value
    }

    operator fun get(address: Long): Long {
        if (address >= data.size)
            return 0
        return data[address.toInt()]
    }

    private fun Long.pow(i: Long): Long {
        return toDouble().pow(i.toDouble()).toLong()
    }

    fun store(instructionPointer: Long, parameterIndex: Long, relativeBase: Long, value : Long) {
        when (val parameterMode = getParameterMode(instructionPointer, parameterIndex)) {
            // position mode
            0L -> this[this[instructionPointer + parameterIndex]] = value
            // relative mode
            2L -> this[relativeBase + this[instructionPointer + parameterIndex]] = value
            // Parameters that an instruction writes to will never be in immediate mode.
            else -> throw UnsupportedOperationException("Unsupported parameterMode encountered in store: $parameterMode")
        }
    }

    fun getOpCode(instructionPointer: Long) : Long {
        // Op code is the two least significant digits
        return this[instructionPointer] % 100
    }

    fun getParameterMode(instructionPointer: Long, parameterIndex: Long) : Long {
        // Parameter mode is stored starting at the third digit
        return (this[instructionPointer] / (100 * 10L.pow(parameterIndex - 1))) % 10
    }

    fun getParameter(instructionPointer: Long, parameterIndex: Long, relativeBase: Long) : Long {
        return when (val parameterMode = getParameterMode(instructionPointer, parameterIndex)) {
            // position mode
            0L -> this[this[instructionPointer + parameterIndex]]
            // immediate mode
            1L -> this[instructionPointer + parameterIndex]
            // relative mode
            2L -> this[relativeBase + this[instructionPointer + parameterIndex]]
            else -> throw IllegalStateException("Unknown parameterMode encountered: $parameterMode")
        }
    }

    fun toProgram(): Program {
        return data.toMutableList()
    }
}





