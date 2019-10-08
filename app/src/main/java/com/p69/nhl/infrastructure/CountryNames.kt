package com.p69.nhl.infrastructure

import android.content.*

fun Context.getCountryName(alpha3Code: String): String {
  val resId = resources.getIdentifier(alpha3Code, "string", packageName)
  return resources.getString(resId)
}