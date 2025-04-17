package com.nurgazy_bolushbekov.product_informer.utils

class ParseInputStringToIntHelper {

    companion object {
        fun parseInputStringToInt(inputString: String): Int? {
            val cleanedString = inputString.trim()
            return try {
                cleanedString.toInt()
            } catch (e: NumberFormatException) {
                // Handle the error appropriately, e.g., log it, show a message to the user, etc.
                println("Error: Could not parse '$cleanedString' to an integer.")
                e.printStackTrace()
                null // Or return a default value if appropriate
            }
        }
    }
}