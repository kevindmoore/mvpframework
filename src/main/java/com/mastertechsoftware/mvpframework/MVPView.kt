package com.mastertechsoftware.mvpframework

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation

/**
 * Class that defines a simple view
 */
open class MVPView {
    var view : View? = null
    var viewModel : ViewModel? = null
    var viewDataModel : ViewDataModel? = null
    var transitionAnimation : Animation? = null
    open fun onViewLoaded() {}
    open fun onResume() {}
    open fun onPause() {}
    open fun onDestroy() {}
    open fun onMenuClicked(menuItem: MenuItem?) : Boolean { return false }
    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) : Boolean { return false }
    open fun onNavigationItemSelected(item: MenuItem): Boolean { return false }
    open fun onBackPressed(): Boolean { return false  }
    open fun onOptionsItemSelected(item: MenuItem?): Boolean? { return false  }
}