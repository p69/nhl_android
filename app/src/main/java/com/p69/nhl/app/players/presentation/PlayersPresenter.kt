package com.p69.nhl.app.players.presentation

import android.os.*
import com.p69.nhl.api.*
import kotlinx.coroutines.*

typealias FetchPlayers = suspend (Int)->Result<List<Player>>
typealias PositionFilterDialog = (PositionFilterDialogData)->Unit

class PlayersPresenter(private val uiScope: CoroutineScope,
                       private val view: PlayersView,
                       private val playersFetcher: FetchPlayers,
                       private val showFilterDialog: PositionFilterDialog) {
  private lateinit var state: PlayersState

  val parcel: Parcelable
    get() = state

  fun handleViewEvent(event: PlayersViewEvent) {
    when (event) {
      is PlayersViewEvent.ViewCreated -> {
        val newState = event.restoredState ?: PlayersState.Empty
        mutateState(newState)
      }
      is PlayersViewEvent.ViewWithTeamCreated -> {
        mutateState(PlayersState.Loading(event.team))
        state.selectedTeam?.apply { loadPlayers(this) }
      }
      is PlayersViewEvent.Sort -> {
        state.asLoaded?.apply {
          val newState = copy(sorting = event.sorting)
          mutateState(newState)
        }
      }
      is PlayersViewEvent.Filter -> {
        state.asLoaded?.apply { showFilterDialog(this) }
      }
      is PlayersViewEvent.PositionFilterApplied -> {
        state.asLoaded?.apply {
          val newState = copy(positionsFilter = event.selected)
          mutateState(newState)
        }
      }
    }
  }

  private fun loadPlayers(team: Team) = uiScope.launch {
    playersFetcher(team.id).fold(
      onSuccess = { mutateState(PlayersState.Loaded(team, it)) },
      onFailure = { mutateState(PlayersState.Error(team)) }
    )
  }

  private fun showFilterDialog(state: PlayersState.Loaded) {
    val dialogData =
      PositionFilterDialogData(state.positionsFilter)
    showFilterDialog(dialogData)
  }

  private fun mutateState(newState: PlayersState) {
    state = newState
    view.render(state)
  }
}

