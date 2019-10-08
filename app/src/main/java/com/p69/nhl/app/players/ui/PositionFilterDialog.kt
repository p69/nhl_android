package com.p69.nhl.app.players.ui

import androidx.appcompat.app.*
import com.p69.nhl.*
import com.p69.nhl.api.*
import com.p69.nhl.app.players.presentation.*

fun PlayersFragment.showPositionFilterDialog(data: PositionFilterDialogData, onOk: (Set<PositionType>)->Unit) {
  val allOptions = PositionType.values()
    .map { it.toString() }
    .toTypedArray()

  val currentOptions = PositionType.values()
    .map { data.selected.contains(it) }
    .toBooleanArray()

  val selectedItems = data.selected.toMutableSet()

  val builder = AlertDialog.Builder(activity!!)
  builder.setTitle(R.string.dialog_filter_position_title)
    .setMultiChoiceItems(allOptions, currentOptions) { _, which, isChecked ->
      val position = PositionType.valueOf(allOptions[which])
      if (isChecked) {
        selectedItems.add(position)
      } else {
        selectedItems.remove(position)
      }
    }
    .setPositiveButton(R.string.btn_ok) { _, _ ->
      onOk(selectedItems)
    }
    .setNegativeButton(R.string.btn_cancel) {_, _ ->}

  builder.create().show()
}