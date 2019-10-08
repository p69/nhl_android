package com.p69.nhl.app.playerdetails.presentation

import android.os.*
import com.p69.nhl.api.*
import kotlinx.android.parcel.*

sealed class PlayerDetailsState: Parcelable {
  @Parcelize
  object Idle: PlayerDetailsState()

  @Parcelize
  data class Loading(val playerId: Int) : PlayerDetailsState()

  @Parcelize
  object Error : PlayerDetailsState()

  @Parcelize
  data class Loaded(val playerDetails: PlayerDetails) : PlayerDetailsState()
}