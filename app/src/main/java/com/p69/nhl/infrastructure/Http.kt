package com.p69.nhl.infrastructure

import com.p69.nhl.api.*
import okhttp3.*
import java.io.*

private val okClient = lazy { OkHttpClient() }

fun httpGetOk(endpoint: Endpoint): Result<String> {
  val request = Request.Builder().url(endpoint.url).build()
  try {
    val response = okClient.value.newCall(request).execute()
    val body = response.body?.string()
    if (body != null) {
      return Result.success(body)
    }
    return Result.failure(IOException("Failed to read response"))
  } catch (exc: Throwable) {
    return Result.failure(exc)
  }
}