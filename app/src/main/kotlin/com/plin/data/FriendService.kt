import com.plin.data.FriendDao
import com.plin.domain.models.Friend

class FriendService(private val friendDao: FriendDao) {
    val allFriends = friendDao.getAllFriends()
    suspend fun addFriend(name: String, frequency: Int) {
        friendDao.insert(Friend(name = name, frequency = frequency))
    }
}