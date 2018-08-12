package com.tsystems.logiweb.manhwa.smoker.fragments.runs

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
 * Provide views to RecyclerView with data from RunsRecyclerViewFragment.dataset
 */
class CustomAdapter(private val context: RunsRecyclerViewFragment) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

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
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.row_item, viewGroup, false)

        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")
        val item = context.dataset[position]
        viewHolder.rowText.text = item.description
        val max = viewHolder.rowProgress.max + 1
        val percent = (item.percent * max).roundToInt() + 1
        viewHolder.rowProgress.progress = percent
    }

    // Return the size of your data set (invoked by the layout manager)
    override fun getItemCount(): Int {
        return context.dataset.size
    }

    companion object {
        private const val TAG = "CustomAdapter"
    }
}
