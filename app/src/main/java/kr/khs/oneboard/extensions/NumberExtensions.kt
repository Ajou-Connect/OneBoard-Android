package kr.khs.oneboard.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun Long.toDateTime(): String = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(this)

@SuppressLint("SimpleDateFormat")
fun Long.toDateTimeWithoutSec(): String = SimpleDateFormat("yyyy-MM-dd hh:mm").format(this)

@SuppressLint("SimpleDateFormat")
fun String.toTimeInMillis(): Long = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(this).time

@SuppressLint("SimpleDateFormat")
fun String.toTimeInMillisWithoutSec(): Long = SimpleDateFormat("yyyy-MM-dd hh:mm").parse(this).time