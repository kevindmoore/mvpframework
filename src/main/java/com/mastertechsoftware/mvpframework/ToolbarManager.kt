package com.mastertechsoftware.mvpframework

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator


/**
 * Class for dealing with toolbar
 */
open class ToolbarManager(val toolbar: Toolbar, val presenter: Presenter, val navigationView: NavigationView,
                          val drawerLayout: DrawerLayout, val appBarLayout: AppBarLayout) :
        Toolbar.OnMenuItemClickListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    var navState = NavState.CLOSED
    var homeState = HomeState.HAMBURGER
    val drawerArrowDrawable = DrawerArrowDrawable(presenter.activity)

    init {
        drawerArrowDrawable.color = Color.WHITE
        drawerArrowDrawable.isSpinEnabled = true
        toolbar.setNavigationIcon(drawerArrowDrawable)
        toolbar.setOnMenuItemClickListener(this)
        navigationView.setNavigationItemSelectedListener(this)
        toolbar.setNavigationOnClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item == null) {
            return false
        }
        return presenter.onMenuClicked(item)
    }

    override fun onClick(v: View?) {
        when (homeState) {
            HomeState.HAMBURGER -> {
                when (navState) {
                    NavState.CLOSED -> {
                        openDrawer()
                    }
                    else -> {
                        closeDrawer()
                    }
                }
            }
            else -> {
                presenter.onBackPressed()
            }
        }
    }

    fun showToolbar(show : Boolean) {
        appBarLayout.setExpanded(show)
    }

    fun openDrawer() {
        drawerLayout.openDrawer(Gravity.START); navState = NavState.OPEN
    }

    fun closeDrawer() {
        drawerLayout.closeDrawer(Gravity.START); navState = NavState.CLOSED
    }

    fun isDrawerOpen() : Boolean {
        return drawerLayout.isDrawerOpen(navigationView)
    }

    fun setDrawerBackgroundColor(@ColorRes color : Int) {
        drawerLayout.setBackgroundColor(drawerLayout.context.resources.getColor(color))
    }

    fun setDrawerStatusBarColor(@ColorRes color : Int) {
        drawerLayout.setStatusBarBackgroundColor(drawerLayout.context.resources.getColor(color))
    }

    fun showToolbar() {
        toolbar.animate().translationY(0f).setInterpolator(DecelerateInterpolator()).start()
    }

    fun lockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        closeDrawer()
        return presenter.onNavigationItemSelected(item)
    }

    fun setNavMenu(@MenuRes menuId: Int) {
        navigationView.menu?.clear()
        navigationView.inflateMenu(menuId)
    }

    fun setBackButton(on: Boolean) {
        if (on) {
            drawerArrowDrawable.progress = 1.0f
//            toolbar.setNavigationIcon(drawerArrowDrawable)
            homeState = HomeState.BACK
        } else {
            drawerArrowDrawable.progress = 0.0f
//            toolbar.setNavigationIcon(drawerArrowDrawable)
            homeState = HomeState.HAMBURGER
        }
    }
    fun setNavigationIcon(@Nullable icon: Drawable) {
        toolbar.setNavigationIcon(icon)
    }

    fun setNavigationIcon(@DrawableRes resId : Int) {
        toolbar.setNavigationIcon(resId)
    }

    fun getToolbarMenu(): Menu {
        return toolbar.menu
    }


    fun setTitle(@StringRes stringId: Int) {
        toolbar.setTitle(stringId)
    }

    fun setTitle(title : String) {
        toolbar.setTitle(title)
    }

    fun clearMenu() {
        toolbar.menu.clear()
    }

    fun setMenu(@MenuRes menuId: Int) {
        clearMenu()
        toolbar.inflateMenu(menuId)
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