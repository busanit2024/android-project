import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Review
import com.busanit.searchrestroom.review.ReviewWithMemberAndFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val reviewDao = AppDatabase.getDatabase(application)!!.reviewDao()

    private val _reviews = MutableLiveData<List<ReviewWithMemberAndFilter>>()
    val reviews: LiveData<List<ReviewWithMemberAndFilter>> = _reviews

    private val _latestReviews = MutableLiveData<List<ReviewWithMemberAndFilter>>()
    val latestReviews: LiveData<List<ReviewWithMemberAndFilter>> = _latestReviews

    fun loadAllReviews(restroomId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val reviews = reviewDao.getReviewsWithMember(restroomId)
            _reviews.postValue(reviews)
        }
    }

    fun loadLatestReviews(restroomId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val reviews = reviewDao.getLatestReviewsWithMember(restroomId)
            _latestReviews.postValue(reviews)
        }
    }

    fun insertReview(review: Review) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                reviewDao.insert(review)

                // 리뷰 목록 갱신
                review.restroomId?.let { restroomId ->
                    loadLatestReviews(restroomId)
                }
            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error inserting review", e)
            }
        }
    }

    fun updateReview(review: Review) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                reviewDao.update(review)

                // 리뷰 목록 갱신
                review.restroomId?.let { restroomId ->
                    loadLatestReviews(restroomId)
                }
            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error updating review", e)
            }
        }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 리뷰 삭제
                reviewDao.deleteReviewById(reviewId)

                // 리뷰 목록 갱신 (현재 보고 있는 화장실의 리뷰 목록을 다시 불러옴)
                val currentRestroomId = _latestReviews.value?.firstOrNull()?.restroomId
                currentRestroomId?.let {
                    loadLatestReviews(it)
                }
            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error deleting review", e)
            }
        }
    }
}