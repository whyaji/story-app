package com.whyaji.storyapp.ui.story.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.whyaji.storyapp.data.remote.response.ListStoryItem
import com.whyaji.storyapp.databinding.ItemStoryBinding
import com.bumptech.glide.Glide
import com.whyaji.storyapp.R

class ListStoryAdapter :
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListStoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.roll_scale)
    }

    class ListStoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            binding.tvItemDesc.text = story.description

            val drawable = CircularProgressDrawable(binding.root.context)
            drawable.strokeWidth = 5f
            drawable.centerRadius = 30f
            drawable.start()

            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .placeholder(drawable)
                .into(binding.ivItemPhoto)

            val bundle = bundleOf(
                "storyName" to story.name,
                "storyDesc" to story.description,
                "storyId" to story.id,
                "storyPhotoUrl" to story.photoUrl
            )
            binding.cardView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_listStoryFragment_to_detailStoryFragment, bundle))
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
