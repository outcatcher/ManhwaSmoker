package com.tsystems.logiweb.manhwa.smoker.recycler

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tsystems.logiweb.manhwa.smoker.R
import com.tsystems.logiweb.manhwa.smoker.backend.GetCurrentRunsAsync
import com.tsystems.logiweb.manhwa.smoker.backend.GetPreviousRunsAsync

/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager] and a
 * [GridLayoutManager].
 */
class RecyclerViewFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var dataset: Array<String> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataSet()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.recyler_view_frag, container, false)
        rootView.tag = TAG

        recyclerView = rootView.findViewById(R.id.recyclerView)

        val swipeContainer = rootView.findViewById(R.id.swipeRefresh) as SwipeRefreshLayout
        swipeContainer.setOnRefreshListener {
            this.initDataSet()
            swipeContainer.isRefreshing = false
        }

        setRecyclerViewLayoutManager()

        val adapter = CustomAdapter(dataset)
        recyclerView!!.adapter = adapter

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
        this.dataset = when (arguments["page"] as Page) {
            Page.CURRENT_RUNS -> GetCurrentRunsAsync.execute().get()
            Page.PREVIOUS_RUNS -> GetPreviousRunsAsync.execute().get()
        }
    }

    companion object {
        private const val TAG = "RecyclerViewFragment"
    }
}

enum class Page { CURRENT_RUNS, PREVIOUS_RUNS }
