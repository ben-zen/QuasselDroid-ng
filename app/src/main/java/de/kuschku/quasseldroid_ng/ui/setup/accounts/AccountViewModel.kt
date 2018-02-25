package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import de.kuschku.quasseldroid_ng.persistence.AccountDatabase

class AccountViewModel(application: Application) : AndroidViewModel(application) {
  private val database: AccountDatabase = AccountDatabase.Creator.init(
    getApplication()
  )
  val accounts: LiveData<List<AccountDatabase.Account>> = database.accounts().all()
  val selectedItem = MutableLiveData<Pair<Long, Long>>()
}
