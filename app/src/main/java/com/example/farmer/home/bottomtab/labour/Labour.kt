package com.example.farmer.home.bottomtab.labour

data class Labour(
    var name: String?,
    var date: String,
    var cropName: String?,
    var workingType: String?
) {
    val weights =mutableListOf<Int>()
    var totalWeight: Int = 0
        private set

    init {
        totalWeight = calculateTotalWeight()
    }

    private fun calculateTotalWeight(): Int {
        return weights.toList().sum()
    }

}
