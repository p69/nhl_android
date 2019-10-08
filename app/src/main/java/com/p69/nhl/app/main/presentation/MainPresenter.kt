package com.p69.nhl.app.main.presentation

import android.os.*
import com.p69.nhl.api.*
import kotlinx.coroutines.*

//Some services and other dependencies.
typealias FetchTeams = suspend ()->Result<List<Team>>
typealias ShowTeamRoster = (Team)->Unit


class MainPresenter(private val uiScope: CoroutineScope,
                    private val view: MainView,
                    private val fetcher: FetchTeams,
                    private val showTeamRoster: ShowTeamRoster) {

  private var state: MainState = MainState.Loading

  val parcel: Parcelable
    get() = state

  fun handleViewEvent(event: MainViewEvent) {
    when(event) {
      is MainViewEvent.ViewStarted -> {
        if (event.restoredState != null) {
          mutateState(event.restoredState)
        } else {
          mutateState(MainState.Loading)
        }
        val currentState = state
        if (currentState is MainState.Loading) {
          loadTeams()
        }
      }
      is MainViewEvent.TeamSelected -> {
        (state as? MainState.Loaded)?.run {
          showTeamRoster(event.team)
          mutateState(copy(selectedTeamId = event.team.id))
        }
      }
    }
  }

  private fun loadTeams() = uiScope.launch {
    fetcher().fold(
      onSuccess = { mutateState(MainState.Loaded(it)) },
      onFailure = { mutateState(MainState.Error) })
  }

  private fun mutateState(newState: MainState) {
    state = newState
    view.render(state)
  }
}