package com.outcatcher.manhwa.smoker.fragments.runs

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
import com.outcatcher.manhwa.smoker.backend.GetCurrentRunsAsync
import com.outcatcher.manhwa.smoker.backend.GetPreviousRunsAsync
import com.outcatcher.manhwa.smoker.backend.TestRunResultList
import java.net.ProtocolException
import java.util.concurrent.TimeUnit

class RunsRecyclerViewFragment : Fragment() {

    private var recyclerView: RecyclerView? = null

    var dataset: TestRunResultList = emptyList()
        private set

    var noRunsFoundTextView: TextView? = null

    private fun dataUpdateHandler(dataSet: TestRunResultList) {
        val textView = noRunsFoundTextView!!
        val runsDescription = when (page) {
            Page.CURRENT_RUNS -> "running"
            Page.PREVIOUS_RUNS -> "finished"
        }
        textView.text = getString(R.string.no_runs_found, runsDescription)
        textView.visibility = if (dataSet.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.recyler_view_frag, container, false)
        rootView.tag = TAG

        recyclerView = rootView.findViewById(R.id.recycler_view)
        noRunsFoundTextView = rootView.findViewById(R.id.no_runs_text)
        initDataSet()

        setRecyclerViewLayoutManager()

        val adapter = CustomAdapter(this)
        recyclerView!!.adapter = adapter

        val swipeContainer = rootView.findViewById(R.id.swipe_refresh) as SwipeRefreshLayout

        swipeContainer.setOnRefreshListener {
            initDataSet()
            adapter.notifyDataSetChanged()
            swipeContainer.isRefreshing = false
        }
        return rootView
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

        dataset = when (page) {
            Page.CURRENT_RUNS -> GetCurrentRunsAsync().execute().get(25, TimeUnit.SECONDS)
            Page.PREVIOUS_RUNS -> GetPreviousRunsAsync().execute().get(25, TimeUnit.SECONDS)
        }
        dataUpdateHandler(dataset)

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
