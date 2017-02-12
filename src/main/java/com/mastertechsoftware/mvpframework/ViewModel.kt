package com.mastertechsoftware.mvpframework

/**
 * Model that describes a view
 */
data class ViewModel(var viewName : String) {
    var layoutName: String? = null
    var viewResourceId : Int = 0
    var viewType: Class<MVPView>? = null
    var mvpView: MVPView? = null
    constructor(viewName : String, viewResourceId : Int) : this(viewName) {
        this.viewResourceId = viewResourceId
    }
}