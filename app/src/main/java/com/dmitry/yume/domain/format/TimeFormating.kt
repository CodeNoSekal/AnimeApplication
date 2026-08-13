package com.dmitry.yume.domain.format

fun formatTime(time: Long): String{
    val secondsTotal = time.coerceAtLeast(0L) / 1000
    val minutesTotal = secondsTotal / 60

    val minutes = minutesTotal % 60
    val seconds = secondsTotal % 60
    val hours = minutesTotal / 60

    return if (hours > 0){
        "${formatPart(hours)}:${formatPart(minutes)}:${formatPart(seconds)}"
    } else {
        "${formatPart(minutes)}:${formatPart(seconds)}"
    }
}



fun formatPart(part: Long): String {
    return if (part < 10) {
        "0$part"
    } else {
        part.toString()
    }
}
