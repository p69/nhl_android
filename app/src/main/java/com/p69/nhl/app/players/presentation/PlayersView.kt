package com.p69.nhl.app.players.presentation

import com.p69.nhl.api.*

interface PlayersView {
  fun render(state: PlayersState)
}

sealed class PlayersViewEvent {
  data class ViewCreated(val view: PlayersView, val restoredState: PlayersState?) :
    PlayersViewEvent()

  data class ViewWithTeamCreated(
    val view: PlayersView,
    val team: Team
  ) : PlayersViewEvent()

  data class Sort(val sorting: PlayersSorting) : PlayersViewEvent()
  object Filter : PlayersViewEvent()
  data class PositionFilterApplied(val selected: Set<PositionType>) : PlayersViewEvent()
}

data class PositionFilterDialogData(val selected: Set<PositionType>)