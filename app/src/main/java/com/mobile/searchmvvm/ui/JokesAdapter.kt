package com.mobile.searchmvvm.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.searchmvvm.data.model.Jokes
import com.mobile.searchmvvm.databinding.ItemJokesBinding
import com.mobile.searchmvvm.extension.loadImageFromUrl

class JokesAdapter() :
    RecyclerView.Adapter<JokesAdapter.ViewHolder>() {

    var jokesList = listOf<Jokes>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = jokesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            ItemJokesBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = jokesList[position]
        holder.binding.apply {
            imgIcon.loadImageFromUrl(model.iconUrl)
            txtValue.text = model.value
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position

    }

    class ViewHolder(val binding: ItemJokesBinding) : RecyclerView.ViewHolder(binding.root)
}