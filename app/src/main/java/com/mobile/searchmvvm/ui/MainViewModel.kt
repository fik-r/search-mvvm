package com.mobile.searchmvvm.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.searchmvvm.base.ResponseError
import com.mobile.searchmvvm.data.model.Jokes
import com.mobile.searchmvvm.domain.GetSearchJokesUseCase
import com.mobile.searchmvvm.helper.StateWrapper
import com.mobile.searchmvvm.network.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainViewModel(
    private val getSearchJokesUseCase: GetSearchJokesUseCase
) : ViewModel(), CoroutineScope {

    sealed class Event {
        object OnCreated : Event()
        data class OnSearchQueryChanged(val searchQuery: String) : Event()
        object OnRefresh : Event()
    }

    sealed class State {
        data class ShowError(val code: Int, val message: String) : State()
        data class ShowLoading(val isLoading: Boolean) : State()
        data class ShowNetworkError(val exception: Exception) : State()
        object ShowEmpty : State()
        data class ShowListJokes(
            val list: List<Jokes>
        ) : State()
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val _state = MutableLiveData<StateWrapper<State>>()
    val state: LiveData<StateWrapper<State>> = _state

    private var _searchQuery: String = ""

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnCreated -> onCreated()
            is Event.OnSearchQueryChanged -> setSearchQuery(event.searchQuery)

        }
    }

    private fun onCreated() = launch {
        setState(State.ShowLoading(true))
        retrieveListJokes()
        setState(State.ShowLoading(false))
    }

    private fun setSearchQuery(searchQuery: String) = launch {
        _searchQuery = searchQuery
        retrieveListJokes()
    }

    private suspend fun retrieveListJokes() {
        setState(State.ShowLoading(true))
        val params = _searchQuery
        when (val response = getSearchJokesUseCase.execute(params)) {
            is NetworkResponse.Success -> {
                val listJokes = response.body.result
                if (listJokes.isEmpty())
                    setState(State.ShowEmpty)
                else
                    setState(State.ShowListJokes(listJokes))
            }
            else -> setError(response)
        }
        setState(State.ShowLoading(false))
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private fun NetworkResponse<Any, ResponseError>.setErrorResponse(
        onServerError: (code: Int, message: String) -> Unit,
        onNetworkError: (Exception) -> Unit,
        onUnknownError: ((Throwable) -> Unit)? = null
    ) {
        if (this is NetworkResponse.Success) return
        when (this) {
            is NetworkResponse.ServerError -> {
                onServerError(this.code, this.body?.message ?: "")
            }
            is NetworkResponse.NetworkError -> onNetworkError(this.error)
            is NetworkResponse.UnknownError -> {
                Log.e(this.javaClass.simpleName, this.error.message ?: "")
                onUnknownError?.invoke(this.error)
            }
            else -> {
            }
        }
    }

    private fun setError(networkResponse: NetworkResponse<Any, ResponseError>) {
        networkResponse.setErrorResponse(
            onServerError = { code, message -> setState(State.ShowError(code, message)) },
            onNetworkError = { exception -> setState(State.ShowNetworkError(exception)) }
        )
    }

    private fun setState(state: State) {
        _state.value = StateWrapper(state)
    }
}