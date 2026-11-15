package com.plin.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.plin.data.FriendOfTheWeekSettingsStore
import com.plin.data.FriendService
import com.plin.data.PlinDatabase
import com.plin.domain.models.Friend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FriendListViewModel(private val friendService: FriendService) : ViewModel() {

    val friends = friendService.observeFriends()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            friendService.migrateFromPrefsIfNeeded()
        }
    }

    fun addFriend(name: String, frequency: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            friendService.addFriend(name = name, frequency = frequency)
        }
    }

    fun updateFriend(friend: Friend) {
        viewModelScope.launch(Dispatchers.IO) {
            friendService.updateFriend(friend)
        }
    }

    fun deleteFriend(friend: Friend) {
        viewModelScope.launch(Dispatchers.IO) {
            friendService.removeFriend(friend)
        }
    }

    class FriendViewModelFactory(
        private val application: Application,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FriendListViewModel::class.java)) {
                val database = PlinDatabase.getInstance(application)
                val settingsStore = FriendOfTheWeekSettingsStore(application)
                val friendService = FriendService(
                    friendDao = database.friendDao(),
                    settingsStore = settingsStore,
                )
                @Suppress("UNCHECKED_CAST")
                return FriendListViewModel(friendService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
