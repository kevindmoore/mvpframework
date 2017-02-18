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
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.FrameLayout
import com.mastertechsoftware.logging.Logger
import com.mastertechsoftware.logging.json.JSONData
import com.mastertechsoftware.logging.json.JSONDataException
import java.util.*

/**
 * Handle Presentation of Views
 */
open class Presenter(var activity: Activity) : Application.ActivityLifecycleCallbacks {
    lateinit var toolbarManager : ToolbarManager
//    lateinit var navDrawerManager : NavDrawerManager
    lateinit var topLevelLayout : ViewGroup
    lateinit var frameLayout : FrameLayout
    var views = Stack<MVPView>()
    var viewModels: MutableList<ViewModel> = ArrayList()
    var layoutInflater : LayoutInflater
    var menuInflater : MenuInflater
    protected var currentView: MVPView? = null

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
        topLevelLayout = topLayout
        frameLayout = topLayout.findViewById(R.id.frameLayout) as FrameLayout
        toolbarManager = ToolbarManager(topLayout.findViewById(R.id.toolbar) as Toolbar, this,
                topLayout.findViewById(R.id.nav_view) as NavigationView,
                topLayout.findViewById(R.id.drawer_layout) as DrawerLayout)
//        navDrawerManager = NavDrawerManager(activity, topLayout.findViewById(R.id.nav_view) as NavigationView,
//                topLayout.findViewById(R.id.drawer_layout) as DrawerLayout,
//                toolbarManager.toolbar, this)
    }

    fun shutDown() {
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
    fun loadView(viewModel: ViewModel) : MVPView? {
        try {
            val viewClass = viewModel.viewType
            val mvpView = viewClass?.newInstance()
            viewModel.mvpView = mvpView
            mvpView?.view = inflateLayout(viewModel.viewResourceId)
            return mvpView
        } catch (e : InstantiationException ) {
            Logger.error("Problems creating view of type " + viewModel.viewType?.getName(), e);
        } catch (e : IllegalAccessException) {
            Logger.error("Problems creating view of type " + viewModel.viewType?.getName(), e);
        }
        return null
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
        topLevelLayout.findViewById(R.id.fab).visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * Set the drawable for the FAB
     */
    fun setFABDrawable(drawable: Drawable) {
        (topLevelLayout.findViewById(R.id.fab) as FloatingActionButton).setImageDrawable(drawable)
    }

    /**
     * Set the click listener for the fab
     */
    fun setFABOnClickListener(clickListener: (View) -> Unit) {
        topLevelLayout.findViewById(R.id.fab).setOnClickListener(clickListener)
    }

    /**
     * Load the given view and add it to our layout
     */
    fun showView(viewModel: ViewModel) : MVPView? {
        val view = loadView(viewModel)
        view?.let {
            addViewToLayout(view)
        }
        return view
    }

    /**
     * Add the view to the main layout
     * @param view
     */
    fun addViewToLayout(view: MVPView) {
        replaceView(view)
        views.add(view)
    }

    /**
     * Replace whatever view is showing with the given view
     * @param view
     */
    fun replaceView(mvpView: MVPView) {
        frameLayout.removeAllViews()
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
        currentView = mvpView
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
        return currentView?.onActivityResult(requestCode, resultCode, data) ?: false
    }

    /**
     * Handle toolbar menu item
     */
    fun onMenuClicked(menuItem: MenuItem?) : Boolean {
        return currentView?.onMenuClicked(menuItem) ?: false
    }

    /**
     * Handle Drawer Menu item
     */
    fun onNavigationItemSelected(item: MenuItem): Boolean {
        return currentView?.onNavigationItemSelected(item) ?: false
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        currentView?.onDestroy()
    }

    override fun onActivityPaused(activity: Activity?) {
        currentView?.onPause()
    }

    override fun onActivityResumed(activity: Activity?) {
        currentView?.onResume()
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
        val FIELD_MODELS = "fieldModels"
        val FIELD_NAME = "fieldName"
        val FIELD_TYPE = "fieldType"
        val MODELS = "models"
        val CLASS_NAME = "className"
        val VIEW_NAME = "viewName"
        val MAIN_VIEW = "mainView"
        val FIRST_VIEW = "firstView"

    }

    fun onBackPressed(): Boolean {
        return currentView?.onBackPressed() ?: false
    }

    fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return currentView?.onOptionsItemSelected(item) ?: false
    }

}