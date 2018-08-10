package com.tsystems.logiweb.manhwa.smoker.recycler

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tsystems.logiweb.manhwa.smoker.R
import com.tsystems.logiweb.manhwa.smoker.backend.GetCurrentRunsAsync

/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager] and a
 * [GridLayoutManager].
 */
class RecyclerViewFragment : Fragment() {

    private var mRecyclerView: RecyclerView? = null
    private var mDataset: Array<String> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataSet()  // TODO: insert data request here
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.recyler_view_frag, container, false)
        rootView.tag = TAG

        mRecyclerView = rootView.findViewById(R.id.recyclerView)

        setRecyclerViewLayoutManager()

        val mAdapter = CustomAdapter(mDataset)
        mRecyclerView!!.adapter = mAdapter

        return rootView
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     */
    private fun setRecyclerViewLayoutManager() {
        val scrollPosition = 0

        val mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView!!.layoutManager = mLayoutManager
        mRecyclerView!!.scrollToPosition(scrollPosition)
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private fun initDataSet() {
        this.mDataset = when (arguments["page"] as Page) {
            Page.CURRENT_RUNS -> GetCurrentRunsAsync().execute().get()
            Page.PREVIOUS_RUNS -> emptyArray()
        }
    }

    companion object {
        private const val TAG = "RecyclerViewFragment"
    }
}

enum class Page { CURRENT_RUNS, PREVIOUS_RUNS }
