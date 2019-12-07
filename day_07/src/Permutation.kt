
fun <T> List<T>.getAllPermutations(): List<List<T>> {
    if (size <= 1) return listOf(this)

    val element = this[0]
    val permutations = mutableListOf<List<T>>()

    for (permutation in this.drop(1).getAllPermutations()) {
        (0..permutation.size).forEach {
            var p = permutation.toMutableList()
            p.add(it, element)
            permutations.add(p)
        }
    }

    return permutations
}