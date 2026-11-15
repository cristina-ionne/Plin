package com.plin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plin.data.FriendDao
import com.plin.domain.models.Friend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider



class FriendListViewModel(private val friendDao: FriendDao) : ViewModel() {

    // Show the friends list as StateFlow for the UI
    val friends = friendDao.getAllFriends()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFriend(name: String, frequency: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            friendDao.insert(Friend(name = name, frequency = frequency))
        }
    }

    fun updateFriend(friend: Friend) {
        viewModelScope.launch(Dispatchers.IO) {
            friendDao.update(friend)
        }
    }

    fun deleteFriend(friend: Friend) {
        viewModelScope.launch(Dispatchers.IO) {
            friendDao.delete(friend)
        }
    }

    class FriendViewModelFactory(private val friendDao: FriendDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FriendListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FriendListViewModel(friendDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}