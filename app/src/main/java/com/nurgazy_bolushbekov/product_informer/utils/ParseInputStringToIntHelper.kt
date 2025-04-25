package com.nurgazy_bolushbekov.product_informer.utils

class ParseInputStringToIntHelper {

    companion object {
        fun parseInputStringToInt(inputString: String): Int? {
            val cleanedString = inputString.trim()
            return try {
                cleanedString.toInt()
            } catch (e: NumberFormatException) {
                println("Error: Could not parse '$cleanedString' to an integer.")
                e.printStackTrace()
                null
            }
        }
    }
}