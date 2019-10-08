package com.p69.nhl.api

import android.os.Parcelable
import com.fasterxml.jackson.annotation.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(
  "link", "venue", "abbreviation", "teamName", "locationName", "firstYearOfPlay",
  "division", "conference", "franchise", "shortName", "officialSiteUrl", "franchiseId", "active")
data class Team(
  val id: Int,
  val name: String
): Parcelable

@Parcelize
data class Player(
  val person: Person,
  val jerseyNumber: Int,
  val position: Position
): Parcelable

@Parcelize
@JsonIgnoreProperties("link")
data class Person(
  val id: Int,
  val fullName: String
): Parcelable

@Parcelize
data class Position(
  val code: String,
  val name: String,
  val type: PositionType,
  val abbreviation: String
): Parcelable

enum class PositionType {
  Forward,
  Goalie,
  Defenseman
}

@Parcelize
@JsonIgnoreProperties(
  "link", "fullName", "firstName", "lastName", "birthDate", "birthCity", "birthStateProvince",
  "birthCountry", "height", "weight", "active", "alternateCaptain", "captain", "rookie",
  "shootsCatches", "rosterStatus", "currentTeam", "primaryNumber", "primaryPosition", "currentAge")
data class PlayerDetails(
  val id: Int,
  val nationality: String
): Parcelable