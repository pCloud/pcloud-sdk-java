@file:JvmName("Utils")

package com.pcloud.sdk

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.resume

suspend fun <T> Call<T>.await(): T = suspendCancellableCoroutine { continuation ->
    continuation.invokeOnCancellation { this.cancel() }
    val callback = object : Callback<T> {
        override fun onResponse(call: Call<T>, response: T) {
            continuation.resume(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            continuation.resumeWithException(t)
        }
    }

    this.enqueue(callback)
}