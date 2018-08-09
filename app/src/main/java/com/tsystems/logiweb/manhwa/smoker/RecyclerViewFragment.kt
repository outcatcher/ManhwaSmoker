package com.tsystems.logiweb.manhwa.smoker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Demonstrates the use of [RecyclerView] with a [LinearLayoutManager] and a
 * [GridLayoutManager].
 */
class RecyclerViewFragment : Fragment() {

    private var mRecyclerView: RecyclerView? = null
    private var mDataset: Array<String> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset()  // TODO: insert data request here
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.recyler_view_frag, container, false)
        rootView.tag = TAG

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = rootView.findViewById<View>(R.id.recyclerView) as RecyclerView

        setRecyclerViewLayoutManager()

        val mAdapter = CustomAdapter(mDataset)
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView!!.adapter = mAdapter
        // END_INCLUDE(initializeRecyclerView)

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
    private fun initDataset() {
        for (i in 0 until DATASET_COUNT) {
            this.mDataset += "Smoke on ELDTEST"
        }
    }

    companion object {

        private val TAG = "RecyclerViewFragment"
        private val DATASET_COUNT = 3
    }
}