package com.outcatcher.manhwa.smoker.fragments.runs

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.outcatcher.manhwa.smoker.R
import com.outcatcher.manhwa.smoker.backend.DefaultConnector
import com.outcatcher.manhwa.smoker.backend.TestRunResult
import com.outcatcher.manhwa.smoker.backend.TestRunResultList

class RunsRecyclerViewFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var swipeContainer: SwipeRefreshLayout? = null

    var dataset: TestRunResultList = emptyList()
        private set

    var noRunsFoundTextView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.recyler_view_frag, container, false)
        rootView.tag = TAG

        recyclerView = rootView.findViewById(R.id.recycler_view)
        noRunsFoundTextView = rootView.findViewById(R.id.no_runs_text)

        setRecyclerViewLayoutManager()

        val adapter = CustomAdapter(this)
        recyclerView!!.adapter = adapter

        val runsDescription = when (page) {
            Page.CURRENT_RUNS -> "running"
            Page.PREVIOUS_RUNS -> "finished"
        }
        noRunsFoundTextView!!.text = getString(R.string.no_runs_found, runsDescription)

        swipeContainer = rootView.findViewById(R.id.swipe_refresh) as SwipeRefreshLayout
        swipeContainer!!.isRefreshing = true
        swipeContainer!!.setOnRefreshListener(::initDataSet)

        initDataSet()

        return rootView
    }


    abstract inner class TestResultListAsyncTask : AsyncTask<Unit, Unit, TestRunResultList>() {
        override fun onPostExecute(resultList: TestRunResultList) {
            dataset = resultList

            noRunsFoundTextView!!.visibility = if (dataset.isEmpty()) View.VISIBLE else View.GONE
            recyclerView!!.adapter.notifyDataSetChanged()
            swipeContainer!!.isRefreshing = false
        }
    }

    inner class GetCurrentRunsAsync : TestResultListAsyncTask() {
        override fun doInBackground(vararg params: Unit?): List<TestRunResult> {
            return DefaultConnector.getCurrentRunList()
        }
    }

    inner class GetPreviousRunsAsync : TestResultListAsyncTask() {
        override fun doInBackground(vararg params: Unit?): List<TestRunResult> {
            return DefaultConnector.getFinishedRunList()
        }
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     */
    private fun setRecyclerViewLayoutManager() {
        val scrollPosition = 0

        val layoutManager = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.scrollToPosition(scrollPosition)
    }

    /**
     * Generates data for RecyclerView's adapter
     */
    private fun initDataSet() {
        Log.d(TAG, "List going to be updated")
        when (page) {
            Page.CURRENT_RUNS -> GetCurrentRunsAsync().execute()
            Page.PREVIOUS_RUNS -> GetPreviousRunsAsync().execute()
        }
    }

    private val page: Page
        get() {
            return arguments!!["page"] as Page
        }

    companion object {
        private const val TAG = "RunsViewFragment"
    }
}

enum class Page { CURRENT_RUNS, PREVIOUS_RUNS }
