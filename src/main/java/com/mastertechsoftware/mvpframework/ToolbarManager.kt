package com.mastertechsoftware.mvpframework

import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.MenuRes
import android.support.annotation.Nullable
import android.support.annotation.StringRes
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View

/**
 * Class for dealing with toolbar
 */
open class ToolbarManager(val toolbar: Toolbar, val presenter: Presenter, val navigationView: NavigationView,
                          val drawerLayout: DrawerLayout) :
        Toolbar.OnMenuItemClickListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    var navState = NavState.CLOSED
    var homeState = HomeState.HAMBURGER

    init {
        toolbar.setNavigationIcon(DrawerArrowDrawable(presenter.activity))
        toolbar.setOnMenuItemClickListener(this)
        navigationView.setNavigationItemSelectedListener(this)
        toolbar.setNavigationOnClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return presenter.onMenuClicked(item)
    }

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return presenter.onNavigationItemSelected(item)
    }

    fun setNavMenu(@MenuRes menuId: Int) {
        navigationView.inflateMenu(menuId)
    }

    fun setBackButton(on: Boolean) {
        if (on) {
            homeState = HomeState.BACK
        } else {
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

    fun setMenu(@MenuRes menuId: Int) {
        toolbar.menu.clear()
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