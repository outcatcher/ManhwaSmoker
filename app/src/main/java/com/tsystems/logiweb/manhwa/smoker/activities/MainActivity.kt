package com.tsystems.logiweb.manhwa.smoker.activities

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
import com.tsystems.logiweb.manhwa.smoker.R
import com.tsystems.logiweb.manhwa.smoker.backend.StartRunAsync
import com.tsystems.logiweb.manhwa.smoker.fragments.runs.Page
import com.tsystems.logiweb.manhwa.smoker.fragments.runs.RunsRecyclerViewFragment


class MainActivity : FragmentActivity() {

    private lateinit var pagesAdapter: TestsCollectionAdapter
    private lateinit var viewPager: ViewPager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fab = findViewById<FloatingActionButton>(R.id.newRun)
        val popupMenu = PopupMenu(this, fab, Gravity.CENTER)
        popupMenu.inflate(R.menu.env_selection_menu)
        popupMenu.setOnMenuItemClickListener {
            val env = when (it.itemId) {
                R.id.run_test -> "eldtest"
                R.id.run_regress -> "eldregress"
                R.id.run_field -> "eldfield"
                R.id.run_reference -> "eldreference"
                R.id.run_prod -> "eldprod"
                else -> ""
            }
            val runStatus = StartRunAsync(env).execute().get()
            Snackbar.make(fab, runStatus, Snackbar.LENGTH_LONG).show()
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
