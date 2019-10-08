package com.p69.nhl.infrastructure

import android.os.*
import androidx.fragment.app.*
import com.google.android.material.bottomsheet.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

abstract class ScopedFragment: DialogFragment(), CoroutineScope {
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

abstract class ScopedBottomSheetDialogFragment: BottomSheetDialogFragment(), CoroutineScope {
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