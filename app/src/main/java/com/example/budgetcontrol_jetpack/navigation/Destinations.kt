package com.example.budgetcontrol_jetpack.navigation

import android.net.Uri

object Destinations {
    const val AUTH = "auth"
    const val HOME = "home"
    const val TRANSACTIONS = "transactions"
    const val TRANSACTION_HISTORY = "transaction_history"
    const val TRANSACTION_HISTORY_ROUTE =
        "transaction_history?type={type}&categoryId={categoryId}&categoryName={categoryName}"
    const val TRANSACTION_EDITOR = "transaction_editor"
    const val CATEGORIES = "categories"
    const val CATEGORY_EDITOR = "category_editor"
    const val DASHBOARD = "dashboard"

    fun transactionHistoryRoute(
        type: String? = null,
        categoryId: Long? = null,
        categoryName: String? = null
    ): String {
        val typeValue = type ?: ""
        val categoryIdValue = categoryId?.toString() ?: ""
        val categoryNameValue = Uri.encode(categoryName ?: "")
        return "$TRANSACTION_HISTORY?type=$typeValue&categoryId=$categoryIdValue&categoryName=$categoryNameValue"
    }
}
