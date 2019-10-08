package com.p69.nhl.app.main.ui

import android.os.*
import android.view.*
import android.view.View.*
import android.widget.*
import androidx.appcompat.app.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.*
import com.google.android.material.navigation.*
import com.p69.nhl.*
import com.p69.nhl.api.*
import com.p69.nhl.app.main.presentation.*
import com.p69.nhl.app.players.ui.*
import com.p69.nhl.infrastructure.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.*


class MainActivity : ScopedAppActivity(), MainView {
  private val presenter: MainPresenter =
    MainPresenter(this, this, NhlApi::getTeams, ::showRoster)

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


    //Set click listener for retry button
    retryButton.setOnClickListener {
      presenter.handleViewEvent(MainViewEvent.RetryLoadTeams)
    }

    //Trying to get saved state
    val restoredState = savedInstanceState?.getParcelable<MainState>(Constants.stateParcelKey)

    //Open drawer for clean launch
    if(restoredState == null) {
      drawerLayout.openDrawer(GravityCompat.START)
    }

    //Notify presenter that view is ready
    presenter.handleViewEvent(MainViewEvent.ViewStarted(restoredState))
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
        retryButton.visibility = GONE
        loader.visibility = VISIBLE
        menuTitle.setText(R.string.nav_header_title_loading)
      }
      MainState.Error -> {
        retryButton.visibility = VISIBLE
        loader.visibility = GONE
        menuTitle.setText(R.string.nav_header_title_error)
      }
      is MainState.Loaded -> {
        retryButton.visibility = GONE
        loader.visibility = GONE
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
          presenter.handleViewEvent(MainViewEvent.TeamSelected(team))
          true
        }
        loadSvgIcon(Endpoint.TeamLogo(team.id), this)
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

//Easy access properties for views in menu header
private val MainActivity.retryButton: View
  get() = navView.getHeaderView(0).retryBtn

private val MainActivity.loader: View
  get() = navView.getHeaderView(0).teamsLoader

private val MainActivity.menuTitle: TextView
  get() = navView.getHeaderView(0).menuTitle

private object Constants {
  const val stateParcelKey = "state"
}

