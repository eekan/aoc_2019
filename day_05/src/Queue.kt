class Queue<T>() {
    private var queue = mutableListOf<T>()

    val size : Int
        get() = queue.size

    constructor(elements : List<T>) : this() {
        queue = elements.toMutableList()
    }

    fun push(element: T) {
        queue.add(element)
    }

    fun pop(): T {
        return queue.removeAt(0)
    }

    fun toList(): List<T> {
        return queue.toList()
    }
}