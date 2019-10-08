package com.p69.nhl.infrastructure

import android.view.*
import com.android.volley.*
import com.android.volley.toolbox.*
import com.p69.nhl.app.*
import com.pixplicity.sharp.*

private val queue = Volley.newRequestQueue(NhlApplication.instance)

private fun downloadSvg(url: String, onSuccess: String.()->Unit) {
  val request = StringRequest(
    Request.Method.GET,
    url,
    Response.Listener<String> { it.onSuccess() },
    Response.ErrorListener {})
  queue.add(request)
}

fun MenuItem.loadSvgIcon(url: String) {
  downloadSvg(url) {
    val sharp = Sharp.loadString(this)
    icon = sharp.drawable
  }
}