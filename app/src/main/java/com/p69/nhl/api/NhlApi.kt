package com.p69.nhl.api

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.module.kotlin.*
import com.p69.nhl.infrastructure.*
import kotlinx.coroutines.*

sealed class Endpoint(val path: String) {
  object Teams : Endpoint("/teams")

  class Players(teamId: Int) : Endpoint("/teams/$teamId/roster")

  class Player(playerId: Int) : Endpoint("/people/$playerId")

  class TeamLogo(teamId: Int) :
    Endpoint("/logos/teams-current-primary-light/$teamId.svg")

  class PlayerPhoto(playerId: Int) :
    Endpoint("/headshots/current/168x168/$playerId.jpg")
}

private const val baseNhlApiUrl = "https://statsapi.web.nhl.com/api/v1"
private const val baseTeamLogoUrl = "https://www-league.nhlstatic.com/images"
private const val basePlayerPhotoUrl = "https://nhl.bamcontent.com/images"

val Endpoint.url: String
  get() = when(this) {
    is Endpoint.TeamLogo -> baseTeamLogoUrl + path
    is Endpoint.PlayerPhoto -> basePlayerPhotoUrl + path
    else -> baseNhlApiUrl + path
  }

object NhlApi {
  suspend fun getTeams() = withContext(Dispatchers.IO) {
    httpGetOk(Endpoint.Teams)
      .flatMap {
        tryDecode<TeamsResponse>(it)
      }
      .map {it.teams}
  }

  suspend fun getPlayers(teamId: Int) = withContext(Dispatchers.IO) {
    httpGetOk(Endpoint.Players(teamId))
      .flatMap { tryDecode<PlayersResponse>(it) }
      .map { it.roster }
  }

  suspend fun getPlayerDetails(playerId: Int) = withContext(Dispatchers.IO) {
    httpGetOk(Endpoint.Player(playerId))
      .flatMap { tryDecode<PlayerDetailsResponse>(it) }
      .map { it.people[0] }
  }

  private inline fun <reified T> tryDecode(json: String): Result<T> {
    return try {
      val obj = jacksonObjectMapper().readValue<T>(json)
      Result.success(obj)
    } catch (exc: Exception) {
      Result.failure(exc)
    }
  }
}

@JsonIgnoreProperties("copyright")
private data class TeamsResponse(val teams: List<Team>)

@JsonIgnoreProperties("copyright", "link")
private data class PlayersResponse(val roster: List<Player>)

@JsonIgnoreProperties("copyright")
private data class PlayerDetailsResponse(val people: List<PlayerDetails>)