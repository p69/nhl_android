package com.p69.nhl.app.players.ui

import android.os.*
import android.view.*
import android.view.View.*
import androidx.appcompat.app.*
import androidx.recyclerview.widget.*
import com.p69.nhl.api.*
import com.p69.nhl.infrastructure.*
import com.p69.nhl.R
import com.p69.nhl.app.playerdetails.ui.*
import com.p69.nhl.app.players.presentation.*
import kotlinx.android.synthetic.main.fragment_players.*
import kotlinx.android.synthetic.main.fragment_players.view.*

class PlayersFragment : ScopedFragment(), PlayersView {

  private lateinit var recyclerLayoutManager: LinearLayoutManager
  private lateinit var playersAdapter: PlayersRecyclerAdapter
  private val presenter = PlayersPresenter(this,this, NhlApi::getPlayers, ::showFilterDialog)
  private val playerDetails = lazy { PlayerDetailsFragment() }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    val restoredState = savedInstanceState?.getParcelable<PlayersState>(Constants.stateParcelKey)
    presenter.handleViewEvent(PlayersViewEvent.ViewCreated(restoredState))
  }

  fun selectTeam(team: Team) {
    presenter.handleViewEvent(PlayersViewEvent.ViewWithTeamCreated(team))
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(Constants.stateParcelKey, presenter.parcel)
  }


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.fragment_players, container, false)
    recyclerLayoutManager = LinearLayoutManager(context)
    root.recycler.layoutManager = recyclerLayoutManager
    playersAdapter = PlayersRecyclerAdapter {
      playerDetails.value.show(fragmentManager!!, person.id)
    }
    root.recycler.adapter = playersAdapter
    root.recycler.setHasFixedSize(true)
    return root
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.main, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.actionSortByName -> {
        presenter.handleViewEvent(PlayersViewEvent.Sort(PlayersSorting.Name))
      }
      R.id.actionSortByNumber -> {
        presenter.handleViewEvent(PlayersViewEvent.Sort(PlayersSorting.Number))
      }
      R.id.actionPositionFilter -> {
        presenter.handleViewEvent(PlayersViewEvent.Filter)
      }
    }
    return true
  }

  override fun render(state: PlayersState): Unit = when(state) {
    is PlayersState.Empty -> {
      (activity as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.appbar_select_team)
      errorLayout.visibility = GONE
      loader.visibility = GONE
      recycler.visibility = GONE
    }
    is PlayersState.Loading -> {
      (activity as? AppCompatActivity)?.supportActionBar?.title = state.team.name
      errorLayout.visibility = GONE
      loader.visibility = VISIBLE
      recycler.visibility = GONE
    }
    is PlayersState.Error -> {
      (activity as? AppCompatActivity)?.supportActionBar?.title = state.team.name
      errorLayout.visibility = VISIBLE
      loader.visibility = GONE
      recycler.visibility = GONE
    }
    is PlayersState.Loaded -> {
      (activity as? AppCompatActivity)?.supportActionBar?.title = state.team.name
      errorLayout.visibility = GONE
      loader.visibility = GONE
      playersAdapter.setData(state.filteredAndSorted)
      recycler.visibility = VISIBLE
    }
  }

  private fun showFilterDialog(data: PositionFilterDialogData) {
    showPositionFilterDialog(data) { selected ->
      presenter.handleViewEvent(PlayersViewEvent.PositionFilterApplied(selected))
    }
  }
}

object Constants {
  const val stateParcelKey = "state"
}