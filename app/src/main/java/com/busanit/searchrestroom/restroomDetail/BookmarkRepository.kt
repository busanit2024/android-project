
import com.busanit.searchrestroom.dao.BookmarkDao
import com.busanit.searchrestroom.dao.RestroomDao
import com.busanit.searchrestroom.database.Bookmark
import com.busanit.searchrestroom.database.Restroom

class BookmarkRepository(private val bookmarkDao: BookmarkDao, private val restroomDao: RestroomDao) {

    suspend fun addBookmark(memberId: Int, restroomId: Int) {
        val bookmark = Bookmark(
            bookmarkId = 0,  // auto-increment
            restroomId = restroomId,
            memberId = memberId
        )
        bookmarkDao.insert(bookmark)
    }

    suspend fun removeBookmark(memberId: Int, restroomId: Int) {
        val bookmark = bookmarkDao.getBookmarkByRestroomIdAndMemberId(restroomId, memberId)
        bookmark?.let {
            bookmarkDao.delete(it)
        }
    }

    suspend fun getBookmarkedRestrooms(memberId: Int): List<Restroom> {
        val bookmarks = bookmarkDao.getBookmarkByMemberId(memberId)
        return bookmarks.mapNotNull { bookmark ->
            bookmark.restroomId?.let { restroomDao.getRestroomById(it) }
        }
    }

    suspend fun isBookmarked(memberId: Int, restroomId: Int): Boolean {
        return bookmarkDao.getBookmarkByRestroomIdAndMemberId(restroomId, memberId) != null
    }
}