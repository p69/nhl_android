package com.p69.nhl.app.playerdetails.ui

import android.os.*
import android.view.*
import android.view.View.*
import androidx.fragment.app.*
import com.p69.nhl.*
import com.p69.nhl.api.*
import com.p69.nhl.app.playerdetails.presentation.*
import com.p69.nhl.infrastructure.*
import kotlinx.android.synthetic.main.fragment_player_details.*

class PlayerDetailsFragment : ScopedBottomSheetDialogFragment(),
  PlayerDetailsView {
  private var playerId = 0

  private val presenter = PlayerDetailsPresenter(NhlApi::getPlayerDetails,this, this)

  fun show(fragmentManager: FragmentManager, playerId:Int) {
    this.playerId = playerId
    show(fragmentManager, "details_$playerId")
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val restoredState = savedInstanceState?.getParcelable<PlayerDetailsState>(Constants.stateParcelKey)
    if (restoredState != null) {
      presenter.handleViewEvent(PlayerDetailsViewEvent.ViewCreated(restoredState))
    } else if (playerId > 0) {
      presenter.handleViewEvent(PlayerDetailsViewEvent.DetailsRequested(playerId))
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.fragment_player_details, container, false)
    return root
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putParcelable(Constants.stateParcelKey, presenter.parcel)
  }

  override fun render(state: PlayerDetailsState) {
    when(state) {
      is PlayerDetailsState.Loading -> {
        flagsLoader.visibility = VISIBLE
        flagsError.visibility = GONE
        flag.visibility = GONE
        countryName.visibility = GONE
      }
      PlayerDetailsState.Error -> {
        flagsLoader.visibility = GONE
        flagsError.visibility = VISIBLE
        flag.visibility = GONE
        countryName.visibility = GONE
      }
      is PlayerDetailsState.Loaded -> {
        flagsLoader.visibility = GONE
        flagsError.visibility = GONE
        flag.visibility = VISIBLE
        countryName.visibility = VISIBLE
        flag.loadFag(state.playerDetails.nationality.toLowerCase())
        countryName.text = context!!.getCountryName(state.playerDetails.nationality.toUpperCase())
      }
    }
  }
}

private object Constants {
  const val stateParcelKey = "state"
}