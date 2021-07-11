package com.mobile.searchmvvm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mobile.searchmvvm.data.model.Jokes
import com.mobile.searchmvvm.databinding.ActivityMainBinding
import com.mobile.searchmvvm.extension.subscribeSingleLiveEvent
import org.koin.android.viewmodel.ext.android.viewModel
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class MainActivity : AppCompatActivity() {
    private val _viewModel: MainViewModel by viewModel()
    private lateinit var _binding: ActivityMainBinding
    private val _jokesAdapter by lazy {
        JokesAdapter()
    }
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater)
            .also { _binding = it }
            .root)

        _binding.apply {
            editSearchQuery.doAfterTextChanged {
                searchQuery = editSearchQuery.text.toString()
            }

            swipeRefresh.setOnRefreshListener {
                _viewModel.onEvent(MainViewModel.Event.OnRefresh)
            }

            btnSearchQuery.setOnClickListener {
                _viewModel.onEvent(MainViewModel.Event.OnSearchQueryChanged(searchQuery))
            }

            listJokes.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            listJokes.adapter = _jokesAdapter
        }

        _viewModel.onEvent(MainViewModel.Event.OnCreated)
        bindViewModel()
    }

    private fun bindViewModel() {
        subscribeSingleLiveEvent(_viewModel.state) {
            when (it) {
                is MainViewModel.State.ShowListJokes -> setListJokes(it.list)
                is MainViewModel.State.ShowLoading -> setLoading(it.isLoading)
                is MainViewModel.State.ShowError -> showError(it.message)
                is MainViewModel.State.ShowNetworkError -> showNetworkError(it.exception)
                is MainViewModel.State.ShowEmpty -> showEmpty()
            }
        }
    }

    private fun setListJokes(
        list: List<Jokes>
    ) {
        _jokesAdapter.jokesList = list
    }

    private fun setLoading(isLoading: Boolean) {
        _binding.swipeRefresh.isRefreshing = isLoading
    }

    private fun showEmpty() {
        val snackbar = Snackbar
            .make(_binding.root, "Data tidak ditemukan", Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun showNetworkError(exception: Exception?) {
        var message = ""
        when (exception) {
            is SocketTimeoutException -> {
                message = "Mohon cek sinyal internet atau koneksi wifi Anda"
            }
            is UnknownHostException -> {
                message = "Sepertinya perangkat Anda tidak terhubung ke koneksi internet"
            }
        }
        val snackbar = Snackbar
            .make(_binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun showError(message: String) {
        val snackbar = Snackbar
            .make(_binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
}