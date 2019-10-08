package com.p69.nhl.app.players.presentation

import android.os.*
import com.p69.nhl.api.*
import kotlinx.android.parcel.*

sealed class PlayersState: Parcelable {
  @Parcelize
  object Empty: PlayersState()

  @Parcelize
  class Loading(val team: Team) : PlayersState()

  @Parcelize
  class Error(val team: Team) : PlayersState()

  @Parcelize
  data class Loaded(
    val team: Team,
    val allPlayers: List<Player>,
    val sorting: PlayersSorting = PlayersSorting.Name,
    val positionsFilter: Set<PositionType> = PositionType.values().toSet()
  ) : PlayersState()
}

enum class PlayersSorting {Name, Number}

val PlayersState.Loaded.filteredAndSorted: List<Player>
  get() = allPlayers
    .sortedBy(sorting)
    .filter { positionsFilter.contains(it.position.type) }

val PlayersState.asLoaded: PlayersState.Loaded?
  get() = when(this) {
    is PlayersState.Loaded -> this
    else -> null
  }

val PlayersState.selectedTeam: Team?
  get() = when(this) {
    PlayersState.Empty -> null
    is PlayersState.Loading -> team
    is PlayersState.Error -> team
    is PlayersState.Loaded -> team
  }

private fun List<Player>.sortedBy(sorting: PlayersSorting): List<Player> = when (sorting) {
  PlayersSorting.Name -> sortedBy { it.person.fullName }
  PlayersSorting.Number -> sortedBy { it.jerseyNumber }
}