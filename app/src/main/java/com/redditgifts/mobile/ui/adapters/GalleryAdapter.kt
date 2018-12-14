package com.redditgifts.mobile.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.redditgifts.mobile.R
import com.redditgifts.mobile.models.GalleryPageData
import com.redditgifts.mobile.ui.viewholders.BaseViewHolder
import com.redditgifts.mobile.ui.viewholders.EmptyViewHolder
import com.redditgifts.mobile.ui.viewholders.GalleryViewHolder
import com.redditgifts.mobile.ui.viewholders.LoaderViewHolder

class GalleryAdapter(private val delegate: BaseViewHolder.BaseViewHolderDelegate?,
                     val itemList: MutableList<Any>): RecyclerView.Adapter<BaseViewHolder>() {

    var isLoading: Boolean = true

    fun setItems(galleryPageData: GalleryPageData) {
        this.isLoading = false
        val oldSize = itemCount
        if(galleryPageData.page == 1){
            this.itemList.clear()
            notifyItemRangeRemoved(0, oldSize)
        }
        this.itemList.addAll(galleryPageData.items)
        notifyItemRangeInserted(oldSize, galleryPageData.items.size)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, layout: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(layout, viewGroup, false)
        return viewHolder(layout, view)
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        val data = objectFromPosition(position)
        viewHolder.bindData(data)
    }

    override fun getItemViewType(position: Int): Int {
        return layout()
    }

    private fun layout(): Int {
        if(this.isLoading){
            return R.layout.cell_loader
        }
        if(this.itemList.isEmpty()){
            return R.layout.cell_no_items
        }
        return R.layout.cell_gallery
    }

    private fun viewHolder(@LayoutRes layout: Int, view: View): BaseViewHolder {
        return when (layout) {
            R.layout.cell_gallery ->
                GalleryViewHolder(view, delegate as GalleryViewHolder.Delegate)
            R.layout.cell_loader ->
                LoaderViewHolder(view)
            R.layout.cell_no_items ->
                EmptyViewHolder(view)
            else ->
                EmptyViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return when {
            this.isLoading -> 1
            this.itemList.isEmpty() -> 1
            else -> this.itemList.size
        }
    }

    private fun objectFromPosition(position: Int): Any {
        return when {
            this.isLoading -> false
            this.itemList.isEmpty() -> false
            else -> this.itemList[position]
        }
    }

}