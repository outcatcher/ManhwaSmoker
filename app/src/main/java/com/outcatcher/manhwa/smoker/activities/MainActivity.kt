package com.outcatcher.manhwa.smoker.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.widget.PopupMenu
import com.outcatcher.manhwa.smoker.R
import com.outcatcher.manhwa.smoker.backend.StartSmokeAsync
import com.outcatcher.manhwa.smoker.fragments.runs.Page
import com.outcatcher.manhwa.smoker.fragments.runs.RunsRecyclerViewFragment


class MainActivity : FragmentActivity() {

    private lateinit var pagesAdapter: TestsCollectionAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var runStatus: String


    private fun confirmStart(env: String) {
        val builder = AlertDialog.Builder(this)
        val onClick = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    runStatus = startRun(env)
                    val fab = findViewById<FloatingActionButton>(R.id.newRun)
                    Snackbar.make(fab, runStatus, Snackbar.LENGTH_LONG).show()
                }
                DialogInterface.BUTTON_NEGATIVE -> { }
            }
        }
        val envPretty = env.removePrefix("eld").capitalize()
        builder
            .setMessage(getString(R.string.confirm_run_title, envPretty))
            .setPositiveButton(R.string.confirm_run_yes, onClick)
            .setNegativeButton(R.string.confirm_run_no, onClick)
            .show()
    }

    private fun startRun(env: String): String {
        return StartSmokeAsync(env).execute().get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fab = findViewById<FloatingActionButton>(R.id.newRun)
        val popupMenu = PopupMenu(this, fab, Gravity.CENTER)
        popupMenu.inflate(R.menu.env_selection_menu)
        popupMenu.setOnMenuItemClickListener {
            val env = when (it.itemId) {
                R.id.run_test -> "test"
                R.id.run_regress -> "regress"
                R.id.run_field -> "field"
                R.id.run_reference -> "reference"
                R.id.run_prod -> "prod"
                else -> ""
            }
            confirmStart(env)
            true
        }
        fab.setOnClickListener { popupMenu.show() }


        pagesAdapter = TestsCollectionAdapter(supportFragmentManager, this)
        viewPager = findViewById(R.id.pager)
        viewPager.adapter = pagesAdapter
    }

}

class TestsCollectionAdapter(fm: FragmentManager, private val context: MainActivity) : FragmentStatePagerAdapter(fm) {
    var currentPage: Page = Page.CURRENT_RUNS

    override fun getItem(position: Int): Fragment {
        val recyclerViewFragment = RunsRecyclerViewFragment()
        currentPage = when (position) {
            Page.CURRENT_RUNS.ordinal -> {
                Page.CURRENT_RUNS
            }
            Page.PREVIOUS_RUNS.ordinal -> Page.PREVIOUS_RUNS
            else -> throw IndexOutOfBoundsException("Page ID can be in range 0..1")
        }
        recyclerViewFragment.arguments = Bundle().apply { putSerializable("page", currentPage) }
        return recyclerViewFragment
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        Page.CURRENT_RUNS.ordinal -> context.getString(R.string.current_runs)
        Page.PREVIOUS_RUNS.ordinal -> context.getString(R.string.previous_runs)
        else -> ""
    }

}
