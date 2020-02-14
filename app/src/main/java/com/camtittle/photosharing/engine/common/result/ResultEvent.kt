package com.camtittle.photosharing.engine.common.result

class ResultEvent<out T>(content: Result<T>) : Event<Result<T>>(content) {

    companion object {
        fun <T> success(data: T): ResultEvent<T> {
            return ResultEvent(Result.success(data))
        }

        fun <T> error(message: String, data: T? = null): ResultEvent<T> {
            return ResultEvent(Result.error(message, data))
        }

        fun <T> loading(data: T? = null): ResultEvent<T> {
            return ResultEvent(Result.loading(data))
        }
    }
}