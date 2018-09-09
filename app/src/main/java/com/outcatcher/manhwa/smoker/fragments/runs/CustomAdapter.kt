package com.outcatcher.manhwa.smoker.fragments.runs

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.outcatcher.manhwa.smoker.R
import com.outcatcher.manhwa.smoker.backend.TestStatus
import kotlin.math.roundToInt

/**
 * Provide views to RecyclerView with data from RunsRecyclerViewFragment.dataset
 */
class CustomAdapter(private val context: RunsRecyclerViewFragment) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val rowText = v.findViewById<View>(R.id.main_activity_title) as TextView
        val rowProgress = v.findViewById<View>(R.id.progressBar) as ProgressBar
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
        val max = viewHolder.rowProgress.max
        viewHolder.rowProgress.progress = (item.finishedPercent * max).roundToInt() + 1

        fun ifFinished() {
            val color = when (item.status) {
                TestStatus.FAIL -> context.resources.getColor(R.color.colorFail, null)
                TestStatus.PASS -> context.resources.getColor(R.color.colorPass, null)
                else -> return
            }
            viewHolder.rowText.setBackgroundColor(color)
            viewHolder.rowProgress.visibility = View.GONE
        }

        ifFinished()
    }

    // Return the size of your data set (invoked by the layout manager)
    override fun getItemCount(): Int {
        return context.dataset.size
    }

    companion object {
        private const val TAG = "CustomAdapter"
    }
}
