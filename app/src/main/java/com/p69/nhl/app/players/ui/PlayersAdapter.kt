package com.p69.nhl.app.players.ui

import android.view.*
import androidx.recyclerview.widget.*
import com.bumptech.glide.*
import com.p69.nhl.R
import com.p69.nhl.api.*
import kotlinx.android.synthetic.main.players_list_item.view.*

class PlayersRecyclerAdapter(private val itemClickHandler: (Player).()->Unit = {}): RecyclerView.Adapter<PlayersRecyclerAdapter.Holder>() {
  private var data = listOf<Player>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    val view =
      LayoutInflater.from(parent.context).inflate(R.layout.players_list_item, parent, false)
    return Holder(view, itemClickHandler)
  }

  override fun getItemCount() = data.size

  override fun onBindViewHolder(holder: Holder, position: Int) {
    data.getOrNull(position)?.apply {
      holder.bind(this)
    }
  }

  fun setData(players: List<Player>) {
    data = players
    notifyDataSetChanged()
  }

  class Holder(view: View, private val itemClickHandler: (Player).() -> Unit) :
    RecyclerView.ViewHolder(view), View.OnClickListener {
    private var player: Player? = null

    init {
      view.setOnClickListener(this)
    }

    fun bind(player: Player) {
      this.player = player
      itemView.name.text = player.person.fullName
      itemView.position.text = player.position.name
      itemView.number.text = player.jerseyNumber.toString()
      Glide.with(itemView).clear(itemView.image)
      Glide.with(itemView)
        .load(Endpoint.PlayerPhoto(player.person.id).url)
        .placeholder(R.drawable.ic_menu_gallery)
        .fitCenter()
        .into(itemView.image)
    }

    override fun onClick(v: View?) {
      player?.apply(itemClickHandler)
    }
  }
}