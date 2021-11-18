package kr.khs.oneboard.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun Long.toDateTime(): String = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(this)

@SuppressLint("SimpleDateFormat")
fun String.toTimeInMillis(): Long = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(this).time