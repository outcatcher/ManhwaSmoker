package com.tsystems.logiweb.manhwa.smoker.fragments.runs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tsystems.logiweb.manhwa.smoker.R
import com.tsystems.logiweb.manhwa.smoker.backend.GetCurrentRunsAsync
import com.tsystems.logiweb.manhwa.smoker.backend.GetPreviousRunsAsync
import com.tsystems.logiweb.manhwa.smoker.backend.RunResult

class RunsRecyclerViewFragment : Fragment() {

    private var recyclerView: RecyclerView? = null

    var dataset: List<RunResult> = emptyList()
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataSet()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.recyler_view_frag, container, false)
        rootView.tag = TAG

        recyclerView = rootView.findViewById(R.id.recyclerView)


        setRecyclerViewLayoutManager()

        val adapter = CustomAdapter(this)
        recyclerView!!.adapter = adapter

        val swipeContainer = rootView.findViewById(R.id.swipeRefresh) as SwipeRefreshLayout

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
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private fun initDataSet() {
        Log.d(TAG, "List going to be updated")
        dataset = when (page) {
            Page.CURRENT_RUNS -> GetCurrentRunsAsync().execute().get()
            Page.PREVIOUS_RUNS -> GetPreviousRunsAsync().execute().get()
        }
    }

    val page: Page
        get() {
            return arguments["page"] as Page
        }

    companion object {
        private const val TAG = "RunsViewFragment"
    }
}

enum class Page { CURRENT_RUNS, PREVIOUS_RUNS }
