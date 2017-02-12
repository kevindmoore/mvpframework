package com.mastertechsoftware.mvpframework

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.ViewGroup

/**
 * Base class for handling Activity
 */
abstract class MVPActivity : AppCompatActivity() {
    lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(presenter.getLayout())
        presenter.setLayout(findViewById(R.id.drawer_layout) as ViewGroup)
//        setSupportActionBar(presenter.toolbarManager.toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeButtonEnabled(true)
        getScreenManager().start()
    }

    abstract fun getScreenManager() : ScreenManager

    override fun onDestroy() {
        presenter.shutDown()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (!presenter.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item)
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!presenter.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (!presenter.onBackPressed()) {
            super.onBackPressed()
        }
    }

    companion object {
    }
}