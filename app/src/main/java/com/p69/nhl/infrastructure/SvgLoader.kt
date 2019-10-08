package com.p69.nhl.infrastructure

import android.view.*
import com.p69.nhl.api.*
import com.pixplicity.sharp.*
import kotlinx.coroutines.*

fun CoroutineScope.loadSvgIcon(endpoint: Endpoint, menuItem: MenuItem) = launch {
  val svg = downloadSvg(endpoint).getOrNull()
  if (svg != null) {
    val sharp = Sharp.loadString(svg)
    menuItem.icon = sharp.drawable
  }
}

private suspend fun downloadSvg(endpoint: Endpoint) = withContext(Dispatchers.IO) {
  httpGetOk(endpoint)
}