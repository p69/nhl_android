package com.p69.nhl.app

import android.app.*
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.*

class NhlApplication : Application() {
  init {
    instance = this
  }

  override fun onCreate() {
    super.onCreate()
    ObjectMapper().registerModule(KotlinModule())
  }

  companion object {
    lateinit var instance: NhlApplication
      private set
  }
}