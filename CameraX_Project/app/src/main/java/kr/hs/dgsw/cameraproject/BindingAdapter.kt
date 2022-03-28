package kr.hs.dgsw.cameraproject

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("app:setVisible")
    fun setVisible(view: View, isOnCamera: LiveData<Boolean>) {
        view.visibility = if (isOnCamera.value == true) View.VISIBLE else View.INVISIBLE
    }
}