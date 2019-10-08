package com.p69.nhl.app.main.ui

import android.os.*
import android.view.*
import android.view.View.*
import androidx.appcompat.widget.*
import androidx.core.view.*
import com.google.android.material.navigation.*

import com.p69.nhl.api.*
import com.p69.nhl.infrastructure.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.*
import androidx.appcompat.app.*
import com.p69.nhl.R
import com.p69.nhl.app.main.presentation.*
import com.p69.nhl.app.players.ui.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : ScopedAppActivity(), MainView {
  private val presenter: MainPresenter =
    MainPresenter(this, NhlApi::getTeams, ::showRoster)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    //Setup toolbar with drawer icon
    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)
    val toggle = ActionBarDrawerToggle(
      this,
      drawerLayout,
      toolbar,
      R.string.navigation_drawer_open,
      R.string.navigation_drawer_close
    )
    drawerLayout.addDrawerListener(toggle)
    toggle.isDrawerIndicatorEnabled = true
    toggle.syncState()

    val restoredState = savedInstanceState?.getParcelable<MainState>(Constants.stateParcelKey)

    //Notify presenter that view is ready
    launch {
      delay(100)
      presenter.handleViewEvent(
        MainViewEvent.ViewStarted(
          this@MainActivity,
          restoredState
        )
      )
    }

    //Open drawer
    drawerLayout.openDrawer(GravityCompat.START)

    //Set click listener for retry button
    retryBtn.setOnClickListener {
      launch {
        presenter.handleViewEvent(MainViewEvent.RetryLoadTeams)
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(Constants.stateParcelKey, presenter.parcel)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    return false
  }

  //Render function is responsible for updating UI according to current state
  override fun render(state: MainState) {
    when (state) {
      MainState.Loading -> {
        retryBtn.visibility = GONE
        teamsLoader.visibility = VISIBLE
        menuTitle.setText(R.string.nav_header_title_loading)
      }
      MainState.Error -> {
        retryBtn.visibility = VISIBLE
        teamsLoader.visibility = GONE
        menuTitle.setText(R.string.nav_header_title_error)
      }
      is MainState.Loaded -> {
        retryBtn.visibility = GONE
        teamsLoader.visibility = GONE
        menuTitle.setText(R.string.nav_header_title)
        navView.renderMenu(state)
      }
    }
  }

  private fun NavigationView.renderMenu(state: MainState.Loaded) {
    if (menu.isNotEmpty()) {
      updateSelectedItem(state.selectedTeamId)
    } else {
      populateMenuItems(state.teams, state.selectedTeamId)
    }
  }

  private fun showRoster(team: Team) {
    players.selectTeam(team)
    drawerLayout.closeDrawers()
  }

  //Function for population menu in drawer
  private fun NavigationView.populateMenuItems(teams: List<Team>, selectedId: Int) {
    teams.forEachIndexed { index, team ->
      menu.add(1, team.id, index + 1, team.name).apply {
        isChecked = team.id == selectedId
        setOnMenuItemClickListener {
          launch { presenter.handleViewEvent(MainViewEvent.TeamSelected(team)) }
          true
        }
        loadSvgIcon(Endpoint.TeamLogo(team.id).url)
      }
    }
  }

  private fun NavigationView.updateSelectedItem(selectedId: Int) {
    menu.forEach { item ->
      item.isChecked = item.itemId == selectedId
    }
  }
}

//Access to PlayersFragment using unsafe cast.
//It's better to fail fast, if it can't be casted.
private val MainActivity.players: PlayersFragment
  get() = playersFragment as PlayersFragment

private object Constants {
  const val stateParcelKey = "state"
}

