package com.p69.nhl.app.main.presentation

import android.os.Parcelable
import com.p69.nhl.api.Team
import kotlinx.android.parcel.Parcelize


sealed class MainState: Parcelable {
  @Parcelize
  object Loading: MainState()

  @Parcelize
  object Error: MainState()

  @Parcelize
  data class Loaded(
    val teams: List<Team>,
    val selectedTeamId: Int = 0): MainState()
}

