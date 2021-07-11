package com.mobile.searchmvvm.helper


open class StateWrapper <out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getEventIfNotHandled(): T? = if (hasBeenHandled) {
        null
    } else {
        hasBeenHandled = true
        content
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StateWrapper<*>) return false

        if (content != other.content) return false
        if (hasBeenHandled != other.hasBeenHandled) return false

        return true
    }
}