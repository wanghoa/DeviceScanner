package com.devicewifitracker.android.extension

import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView.Adapter extension methods.
 *
 * @author guolin
 * @since 2020/10/11
 */

/**
 * This method will be invoked in the [RecyclerView.Adapter.onBindViewHolder], to give first/last itemView
 * the extra 10dp top/bottom margin to make the space between each item equal.
 */
fun RecyclerView.ViewHolder.setExtraMarginForFirstAndLastItem(firstItem: Boolean, lastItem: Boolean) {
    if (firstItem || lastItem) {
        // We give first/last itemView the extra 10dp top/bottom margin to make the space between each item equal.
        val itemView = itemView
        val params = itemView.layoutParams as RecyclerView.LayoutParams
        if (firstItem) {
            params.topMargin = params.topMargin + 10.dp
        } else {
            params.bottomMargin = params.bottomMargin + 10.dp
        }
    }
}