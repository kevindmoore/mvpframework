package com.mastertechsoftware.mvpframework

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.annotation.MenuRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.view.*
import android.widget.FrameLayout
import com.mastertechsoftware.logging.Logger
import com.mastertechsoftware.logging.json.JSONData
import com.mastertechsoftware.logging.json.JSONDataException
import java.lang.Class
import java.lang.ClassNotFoundException
import java.lang.Exception
import java.lang.IllegalAccessException
import java.lang.InstantiationException
import java.util.*
import kotlin.IllegalArgumentException

/**
 * Handle Presentation of Views
 */
open class Presenter(var activity: Activity) : Application.ActivityLifecycleCallbacks {
    lateinit var toolbarManager : ToolbarManager
    lateinit var topLevelLayout : ViewGroup
    lateinit var frameLayout : FrameLayout
    var views = Stack<MVPView>()
    var viewModels: MutableList<ViewModel> = ArrayList()
    var layoutInflater : LayoutInflater
    var menuInflater : MenuInflater
    protected var currentView: MVPView? = null
    protected var showDebugging = true
    protected var resumeCalled = false
    protected var currentAndroidView : View? = null

    init {
        activity.application.registerActivityLifecycleCallbacks(this)
        layoutInflater = activity.layoutInflater
        menuInflater = activity.menuInflater
    }

    /**
     * Return the layout that the activity should use as the main layout
     */
    fun getLayout(): Int {
        return R.layout.mvp_layout
    }

    /**
     * Activity should call this once the layout has been loaded.
     */
    fun setLayout(topLayout: ViewGroup) {
        if (showDebugging) {
            Logger.debug("setLayout")
        }
        topLevelLayout = topLayout
        frameLayout = topLayout.findViewById(R.id.frameLayout)
        val appBarLayout  = topLayout.findViewById<AppBarLayout>(R.id.appBarLayout)
        toolbarManager = ToolbarManager(topLayout.findViewById(R.id.toolbar), this,
                topLayout.findViewById(R.id.nav_view),
                topLayout.findViewById(R.id.drawer_layout), appBarLayout)
    }

    fun shutDown() {
        if (showDebugging) {
            Logger.debug("shutDown")
        }
        activity.application.unregisterActivityLifecycleCallbacks(this)
    }

    /**
     * Inflate a layout
     */
    private fun inflateLayout(@LayoutRes layoutId: Int): View {
        return layoutInflater.inflate(layoutId, frameLayout, false)
    }

    /**
     * Inflate a menu
     */
    fun inflateMenu(@MenuRes menuId: Int, menu: Menu) {
        menuInflater.inflate(menuId, menu)
    }

    /**
     * Load a view from the given view model
     */
    fun createMVPView(viewModel: ViewModel) : MVPView? {
        if (showDebugging) {
            Logger.debug("createMVPView")
        }
        try {
            val viewClass = viewModel.viewType
            val mvpView = viewClass?.newInstance()
            viewModel.mvpView = mvpView
            return mvpView
        } catch (e : InstantiationException ) {
            Logger.error("Problems creating view of type " + viewModel.viewType?.getName(), e);
        } catch (e : IllegalAccessException) {
            Logger.error("Problems creating view of type " + viewModel.viewType?.getName(), e);
        }
        return null
    }

    /**
     * Load an Android View
     */
    fun loadView(mvpView: MVPView?, viewModel: ViewModel) {
        if (showDebugging) {
            Logger.debug("loadView ${mvpView}")
        }
        mvpView?.setupView(inflateLayout(viewModel.viewResourceId))
    }

    /**
     * Load a drawable
     */
    fun loadDrawable(@DrawableRes id: Int): Drawable {
        return activity.resources.getDrawable(id)
    }

    /************* FAB methods **********/

    /**
     * Show/Hide Fab
     */
    fun showFAB(show: Boolean) {
        if (showDebugging) {
            Logger.debug("showFAB ${show}")
        }
        topLevelLayout.findViewById<View>(R.id.fab).visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * Set the drawable for the FAB
     */
    fun setFABDrawable(drawable: Drawable) {
        (topLevelLayout.findViewById<FloatingActionButton>(R.id.fab)).setImageDrawable(drawable)
    }

    fun setFABDrawable(@DrawableRes id: Int) {
        (topLevelLayout.findViewById<FloatingActionButton>(R.id.fab)).setImageResource(id)
    }

    /**
     * Set the click listener for the fab
     */
    fun setFABOnClickListener(clickListener: (View) -> Unit) {
        topLevelLayout.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener(clickListener)
    }

    /**
     * Clear the Toolbar menu
     */
    fun clearMenu() {
        if (showDebugging) {
            Logger.debug("clearMenu")
        }
        toolbarManager.clearMenu()
    }

    /**
     * Set the Right Toolbar Menu
     */
    fun setMenu(@MenuRes menuId: Int) {
        if (showDebugging) {
            Logger.debug("setMenu ${menuId}")
        }
        toolbarManager.setMenu(menuId)
    }

    /**
     * Load the given view and add it to our layout
     */
    fun showView(viewModel: ViewModel, data : Any?) : MVPView? {
        if (showDebugging) {
            Logger.debug("showView ${viewModel}")
        }
        val mvpView = createMVPView(viewModel)
        loadView(mvpView, viewModel)
        mvpView?.bindView(data)
        if (mvpView != null) {
            addViewToLayout(mvpView, true)
        }
        return mvpView
    }

    /**
     * Add the view to the main layout
     * @param view
     */
    fun addViewToLayout(view: MVPView, finishLoading: Boolean) {
        if (showDebugging) {
            Logger.debug("addViewToLayout  ${view}")
        }
        replaceView(view, finishLoading)
        views.add(view)
    }

    /**
     * Replace whatever view is showing with the given view
     * @param view
     */
    fun replaceView(mvpView: MVPView, finishLoading: Boolean) {
        if (showDebugging) {
            Logger.debug("replaceView ${mvpView}")
        }
        if (currentAndroidView == mvpView.view) {
            Logger.error("${mvpView.view} already added")
            return
        }
        try {
            frameLayout.removeAllViews()
        } catch (e : Exception) {
            Logger.error("Problems Removing All Views ${e.message}")
        }
        if (mvpView.view == null && mvpView.viewModel != null) {
            mvpView.view = inflateLayout(mvpView.viewModel!!.viewResourceId)
        }
        if (mvpView.view == null) {
            throw IllegalArgumentException("Could not inflate view")
        }
        if (mvpView.transitionAnimation != null) {
            mvpView.view?.startAnimation(mvpView.transitionAnimation)
        }
        frameLayout.addView(mvpView.view, getFrameLayoutParams())
        currentAndroidView = mvpView.view
        currentView = mvpView
        if (finishLoading) {
            finishLoading()
        }
    }

    /**
     * Check to see if the current view is the same as this one
     */
    fun isCurrentView(view : MVPView?) : Boolean {
        return view == currentView
    }

    /**
     * We've finished loading the view, now
     * call the view's onViewLoaded and onResume
     */
    fun finishLoading() {
        if (showDebugging) {
            Logger.debug("finishLoading ${currentView}")
        }
        currentView?.onResume()
        resumeCalled = true
    }


    private fun getFrameLayoutParams(): FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    /**
     * Read in a json file that has all the views associated with this app
     * @param context
     * @param json
     */
    fun readConfiguration(context: Context, json: String) {
        try {
            val resources = context.resources
            val jsonData = JSONData(json)
            if (jsonData.has(VIEWS)) {
                val views = jsonData.findChild(VIEWS)
                var children = views.getChildren()
                for (childData in children) {
                    var viewModel : ViewModel? = null
                    if (childData.has(VIEW_NAME)) {
                        val viewName = childData.getChildString(VIEW_NAME)
                        viewModel = ViewModel(viewName)
                        viewModels.add(viewModel)
                    }
                    if (childData.has(LAYOUT_NAME)) {
                        val layout_name = childData.getChildString(LAYOUT_NAME)
                        val layoutId = resources.getIdentifier(layout_name, "layout", context.packageName)
                        viewModel?.layoutName = layout_name
                        viewModel?.viewResourceId = layoutId
                    }
                    if (childData.has(VIEW_TYPE)) {
                        val view_type = childData.getChildString(VIEW_TYPE)
                        val viewClass = Class.forName(view_type) as Class<MVPView>
                        viewModel?.viewType = viewClass
                    }
                }
            }
        } catch (e: JSONDataException) {
            Logger.error("Problems parsing json data", e)
        } catch (e: ClassNotFoundException) {
            Logger.error("Problems creating class file", e)
        }

    }

    /**
     * Handle Activity result
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) : Boolean {
        if (showDebugging) {
            Logger.debug("onActivityResult ${requestCode} ${resultCode}")
        }
        return currentView?.onActivityResult(requestCode, resultCode, data) ?: false
    }

    /**
     * Handle toolbar menu item
     */
    fun onMenuClicked(menuItem: MenuItem) : Boolean {
        if (showDebugging) {
            Logger.debug("onMenuClicked ${menuItem}")
        }
        return currentView?.onMenuClicked(menuItem) ?: false
    }

    /**
     * Handle Drawer Menu item
     */
    fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (showDebugging) {
            Logger.debug("onNavigationItemSelected ${item}")
        }
        return currentView?.onNavigationItemSelected(item) ?: false
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (showDebugging) {
            Logger.debug("onActivityDestroyed")
        }
        currentView?.onDestroy()
    }

    override fun onActivityPaused(activity: Activity?) {
        if (showDebugging) {
            Logger.debug("onActivityPaused")
        }
        currentView?.onPause()
    }

    override fun onActivityResumed(activity: Activity?) {
        if (showDebugging) {
            Logger.debug("onActivityResumed")
        }
        if (!resumeCalled) {
            currentView?.onResume()
        }
        resumeCalled = false
    }

    /**
     * Find a view that matches the given class. Useful for finding the parent FrameLayout
     * @param parent
     * *
     * @param type
     * *
     * @return View
     */
    fun findViewByClass(parent: ViewGroup, type: Class<*>): View? {
        val childCount = parent.childCount
        for (i in 0..childCount - 1) {
            val childAt = parent.getChildAt(i)
            if (childAt != null && childAt.javaClass == type) {
                return childAt
            }
            if (childAt is ViewGroup) {
                val foundView = findViewByClass(childAt, type)
                if (foundView != null) {
                    return foundView
                }
            }
        }
        return null
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    companion object {
        val VIEWS = "views"
        val LAYOUT_NAME = "layoutName"
        val VIEW_TYPE = "viewType"
        val VIEW_NAME = "viewName"
    }

    fun onBackPressed(): Boolean {
        if (showDebugging) {
            Logger.debug("onBackPressed")
        }
        return currentView?.onBackPressed() ?: false
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (showDebugging) {
            Logger.debug("onBackPressed ${item}")
        }
        return currentView?.onOptionsItemSelected(item) ?: false
    }

}