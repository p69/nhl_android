package com.p69.nhl.app.main.presentation

import com.p69.nhl.api.*

interface MainView {
  fun render(state: MainState)
}

sealed class MainViewEvent {
  data class ViewStarted(val view: MainView, val restoredState: MainState? = null): MainViewEvent()
  data class TeamSelected(val team: Team): MainViewEvent()
  object RetryLoadTeams: MainViewEvent()
}