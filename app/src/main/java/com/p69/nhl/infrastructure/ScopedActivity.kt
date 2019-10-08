package com.p69.nhl.infrastructure

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlin.coroutines.*

abstract class ScopedAppActivity: AppCompatActivity(), CoroutineScope {
  lateinit var job: Job

  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    job = SupervisorJob()
  }

  override fun onDestroy() {
    super.onDestroy()
    job.cancel()
  }
}