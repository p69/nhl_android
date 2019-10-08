package com.p69.nhl.app.playerdetails.presentation

import android.os.*
import com.p69.nhl.api.*
import kotlinx.coroutines.*

typealias DetailsFetcher = suspend (Int)->Result<PlayerDetails>

class PlayerDetailsPresenter(private val detailsFetcher: DetailsFetcher,
                             private val uiScope: CoroutineScope) {
  private lateinit var state: PlayerDetailsState
  private lateinit var view: PlayerDetailsView
  val parcel: Parcelable
    get() = state

  suspend fun handleViewEvent(event: PlayerDetailsViewEvent) {
    when(event) {
      is PlayerDetailsViewEvent.ViewCreated -> {
        view = event.view
        if(event.restoredState != null) {
          mutateState(event.restoredState)
        } else {
          mutateState(PlayerDetailsState.Idle)
        }
        val currentState = state
        if(currentState is PlayerDetailsState.Loading) {
          loadDetails(currentState.playerId)
        }
      }
      is PlayerDetailsViewEvent.DetailsRequested -> {
        val newState =
          PlayerDetailsState.Loading(event.playerId)
        mutateState(newState)
        loadDetails(event.playerId)
      }
    }
  }

  private suspend fun loadDetails(playerId: Int) = uiScope.launch {
    detailsFetcher(playerId).fold(
      onSuccess = { mutateState(
        PlayerDetailsState.Loaded(
          it
        )
      ) },
      onFailure = { mutateState(PlayerDetailsState.Error) }
    )
  }

  private fun mutateState(newState: PlayerDetailsState) {
    state = newState
    view.render(state)
  }
}