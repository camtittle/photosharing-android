package com.camtittle.photosharing.engine.common.async

interface ServiceCallback<T> {
    fun onSuccess(response: T)
    fun onError(error: CallbackError)
}