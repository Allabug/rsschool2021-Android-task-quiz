package com.rsschool.quiz

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val index: Int,
    @StringRes val question: Int,
    val answers: List<String>,
    val answerPosition: Int,
    var checkedRadioButtonId: Int,
    var indexCheckedRadioButton: Int,
    val title: String
) : Parcelable
