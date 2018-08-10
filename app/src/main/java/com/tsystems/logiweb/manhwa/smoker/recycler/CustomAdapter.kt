package com.tsystems.logiweb.manhwa.smoker.recycler

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.tsystems.logiweb.manhwa.smoker.R
import java.lang.Math.random
import kotlin.math.roundToInt

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
class CustomAdapter(private val mDataSet: Array<String>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val rowText: TextView
        val rowProgress: ProgressBar

        init {
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener { Log.d(TAG, "Element $adapterPosition clicked.") }
            rowText = v.findViewById<View>(R.id.mainActivityTitle) as TextView
            rowProgress = v.findViewById<View>(R.id.progressBar) as ProgressBar
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_item, viewGroup, false)

        return ViewHolder(v)
    }

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.rowText.text = mDataSet[position]
        viewHolder.rowProgress.progress = (random() * viewHolder.rowProgress.max).roundToInt()
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataSet.size
    }

    companion object {
        private val TAG = "CustomAdapter"
    }
}