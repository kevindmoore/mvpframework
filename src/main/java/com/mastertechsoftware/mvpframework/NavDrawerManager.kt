package com.mastertechsoftware.mvpframework

import android.app.Activity
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.MenuRes
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.MenuItem
import android.view.View

/**
 * Handle Navigation Drawer
 */
class NavDrawerManager(val activity: Activity, val navigationView: NavigationView,
                       val drawerLayout: DrawerLayout, var toolbar: Toolbar, val presenter: Presenter) :
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
//    var drawerToggle: ActionBarDrawerToggle
    var navState = NavState.CLOSED
    var homeState = HomeState.HAMBURGER

    init {
//        drawerToggle = ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.open,
//                R.string.close)
//
//        drawerToggle.setToolbarNavigationClickListener(this)
//        drawerToggle.setDrawerIndicatorEnabled(false)
//        drawerLayout.addDrawerListener(drawerToggle)
        navigationView.setNavigationItemSelectedListener(this)
        toolbar.setNavigationOnClickListener(this)
//        drawerToggle.syncState()
    }

    // Toolbar Navigation Handler
    override fun onClick(v: View?) {
        when (homeState) {
            HomeState.HAMBURGER -> {
                when (navState) {
                     NavState.CLOSED -> {drawerLayout.openDrawer(Gravity.START); navState = NavState.OPEN}
                    else -> {drawerLayout.closeDrawer(Gravity.START); navState = NavState.CLOSED}
                }
            }
            else -> {
                presenter.onBackPressed()
            }
        }
    }

    fun isDrawerOpen() : Boolean {
        return drawerLayout.isDrawerOpen(navigationView)
    }
    fun setHomeAsUpIndicator(@DrawableRes id: Int) {
//        drawerToggle.setHomeAsUpIndicator(id)
    }

    fun setHomeAsUpIndicator(drawable: Drawable) {
//        drawerToggle.setHomeAsUpIndicator(drawable)
    }

    fun lockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun setMenu(@MenuRes menuId: Int) {
        navigationView.inflateMenu(menuId)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return presenter.onNavigationItemSelected(item)
    }

    enum class HomeState {
        HAMBURGER,
        BACK
    }
    enum class NavState {
        CLOSED,
        OPEN
    }
}