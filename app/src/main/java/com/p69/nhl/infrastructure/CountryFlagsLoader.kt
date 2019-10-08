package com.p69.nhl.infrastructure

import android.graphics.drawable.*
import android.util.*
import android.widget.*

fun ImageView.loadFag(country3alpha: String) {
  context.assets.open("flags/$country3alpha.png").use {
    val drawable = Drawable.createFromResourceStream(context.resources, TypedValue(), it, null)
    setImageDrawable(drawable)
  }
}