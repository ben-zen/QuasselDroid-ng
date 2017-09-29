package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import de.kuschku.quasseldroid_ng.Keys
import de.kuschku.quasseldroid_ng.ui.chat.ChatActivity
import de.kuschku.quasseldroid_ng.ui.setup.SetupActivity
import de.kuschku.quasseldroid_ng.util.helper.editCommit

class AccountSelectionActivity : SetupActivity() {
  companion object {
    const val REQUEST_CHAT = 0
    const val REQUEST_CREATE_FIRST = 1
    const val REQUEST_CREATE_NEW = 2
  }

  override val fragments = listOf(
    AccountSelectionSlide()
  )

  private lateinit var statusPreferences: SharedPreferences
  override fun onDone(data: Bundle) {
    statusPreferences.editCommit {
      putLong(Keys.Status.selectedAccount, data.getLong(Keys.Status.selectedAccount, -1))
      putBoolean(Keys.Status.reconnect, true)
    }
    startActivityForResult(Intent(this, ChatActivity::class.java), REQUEST_CHAT)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    statusPreferences = this.getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
    val data = Bundle()
    val selectedAccount = statusPreferences.getLong(Keys.Status.selectedAccount, -1)
    data.putLong(Keys.Status.selectedAccount, selectedAccount)
    setInitData(data)

    if (statusPreferences.getBoolean(Keys.Status.reconnect, false) && selectedAccount != -1L) {
      startActivityForResult(Intent(this, ChatActivity::class.java), REQUEST_CHAT)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CHAT && resultCode == Activity.RESULT_CANCELED) {
      finish()
    }
  }
}
