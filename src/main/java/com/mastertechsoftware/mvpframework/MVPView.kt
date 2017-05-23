package com.mastertechsoftware.mvpframework

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

/**
 * Class that defines a simple view
 */
abstract class MVPView {
    var view : View? = null
    var viewModel : ViewModel? = null
    var viewDataModel : ViewDataModel? = null
    var transitionAnimation : Animation? = AlphaAnimation(0.0f, 1.0f) // Default
    open fun setupView(view : View) { this.view = view}
    open fun bindView(data : Any?) {}
    open fun onViewReturn() {}
    open fun onResume() {}
    open fun onPause() {}
    open fun onDestroy() {}
    open fun onMenuClicked(menuItem: MenuItem) : Boolean { return false }
    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) : Boolean { return false }
    open fun onNavigationItemSelected(menuItem: MenuItem): Boolean { return false }
    open fun onBackPressed(): Boolean { return false  }
    open fun onOptionsItemSelected(menuItem: MenuItem?): Boolean? { return false  }
}