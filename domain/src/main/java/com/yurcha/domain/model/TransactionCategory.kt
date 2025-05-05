package com.yurcha.domain.model

enum class TransactionCategory(val displayName: String) {
    GROCERIES("GROCERIES"),
    TAXI("TAXI"),
    ELECTRONICS("ELECTRONICS"),
    RESTAURANT("RESTAURANT"),
    OTHER("OTHER");

    companion object {
        fun fromDisplayName(name: String): TransactionCategory? {
            return entries.find { it.displayName == name }
        }
    }
}