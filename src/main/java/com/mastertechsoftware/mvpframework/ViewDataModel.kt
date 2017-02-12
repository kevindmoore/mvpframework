package com.mastertechsoftware.mvpframework

import java.util.*

/**
 * Data Model class for passing data to views
 */
data class ViewDataModel(var data: Any) {
    val dataMap = HashMap<String, Any>()
}