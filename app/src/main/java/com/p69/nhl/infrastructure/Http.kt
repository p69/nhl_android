package com.p69.nhl.infrastructure

import com.android.volley.*
import com.android.volley.toolbox.*
import com.p69.nhl.api.*
import com.p69.nhl.app.*
import kotlin.coroutines.*

val requestsQueue: RequestQueue = Volley.newRequestQueue(NhlApplication.instance)

suspend inline fun httpGet(endpoint: Endpoint) = suspendCoroutine<String> { continuation ->
  val request = StringRequest(
    Request.Method.GET,
    endpoint.url,
    Response.Listener<String> { continuation.resume(it) },
    Response.ErrorListener { continuation.resumeWithException(it) })
  //request.setShouldCache(false)
  requestsQueue.add(request)
}
