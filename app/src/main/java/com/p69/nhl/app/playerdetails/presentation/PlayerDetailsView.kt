package com.p69.nhl.app.playerdetails.presentation

interface PlayerDetailsView {
  fun render(state: PlayerDetailsState)
}

sealed class PlayerDetailsViewEvent {
  data class ViewCreated(
    val restoredState: PlayerDetailsState? = null
  ) : PlayerDetailsViewEvent()

  data class DetailsRequested(val playerId: Int): PlayerDetailsViewEvent()
}