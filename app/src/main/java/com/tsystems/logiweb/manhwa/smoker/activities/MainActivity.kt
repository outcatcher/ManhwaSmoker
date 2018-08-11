package com.tsystems.logiweb.manhwa.smoker.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.tsystems.logiweb.manhwa.smoker.R
import com.tsystems.logiweb.manhwa.smoker.recycler.Page
import com.tsystems.logiweb.manhwa.smoker.recycler.RecyclerViewFragment


class MainActivity : FragmentActivity() {

    private lateinit var mPagesAdapter: TestsCollectionAdapter
    private lateinit var mViewPager: ViewPager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fab = findViewById<FloatingActionButton>(R.id.newRun)
        fab.setOnClickListener {
            val runIntent = Intent(this, StartRunActivity::class.java)
            startActivity(runIntent)
        }

        mPagesAdapter = TestsCollectionAdapter(supportFragmentManager, this)
        mViewPager = findViewById(R.id.pager)
        mViewPager.adapter = mPagesAdapter
    }

}

class TestsCollectionAdapter(val fm: FragmentManager, private val context: Context) : FragmentStatePagerAdapter(fm) {
    var currentPage: Page = Page.CURRENT_RUNS

    override fun getItem(position: Int): Fragment {
        val recyclerViewFragment = RecyclerViewFragment()

        currentPage = when (position) {
            Page.CURRENT_RUNS.ordinal -> Page.CURRENT_RUNS
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
