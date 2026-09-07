package com.catools.templeapp.data

data class TempleEvent(
    val id: String = "",
    val titleSi: String = "",
    val titleEn: String = "",
    val date: String = "",
    val time: String = "",
    val description: String = "",
    val location: String = "කොළලෑගල පුරාණ විහාරස්ථානය",
    val posterUrl: String = ""
)

data class TempleNotice(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val date: String = ""
)
