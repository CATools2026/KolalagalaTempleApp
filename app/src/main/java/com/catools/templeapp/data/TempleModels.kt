package com.catools.templeapp.data

data class TempleEvent(
    val id: String = "",
    val titleSi: String = "",
    val titleEn: String = "",
    val date: String = "",
    val time: String = "",
    val descriptionSi: String = "",
    val descriptionEn: String = "",
    val locationSi: String = "කොළලෑගල පුරාණ විහාරස්ථානය",
    val locationEn: String = "Kolalagala Ancient Temple",
    val posterUrl: String = "",
    val notifyUsers: Boolean = true,
    val createdAt: Long = 0L
)

data class TempleNotice(
    val id: String = "",
    val titleSi: String = "",
    val titleEn: String = "",
    val messageSi: String = "",
    val messageEn: String = "",
    val date: String = "",
    val notifyUsers: Boolean = true,
    val createdAt: Long = 0L
)

data class GalleryImage(
    val id: String = "",
    val titleSi: String = "",
    val titleEn: String = "",
    val imageUrl: String = "",
    val createdAt: Long = 0L
)
