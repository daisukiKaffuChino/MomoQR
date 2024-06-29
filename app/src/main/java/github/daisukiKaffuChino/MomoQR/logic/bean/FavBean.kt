package github.daisukiKaffuChino.MomoQR.logic.bean

import androidx.lifecycle.LiveData

class FavBean : LiveData<FavBean?>() {

    var content: String? = null
        set(str) {
            field = str
            postValue(this)
        }
    var id: String? = null
        set(str) {
            field = str
            postValue(this)
        }
    var title: String? = null
        set(str) {
            field = str
            postValue(this)
        }
    var img: String? = null
        set(str) {
            field = str
            postValue(this)
        }
    var time: Long = 0
        set(i) {
            field = i
            postValue(this)
        }
    var star = false
        set(star) {
           field = star
            postValue(this)
        }
}
