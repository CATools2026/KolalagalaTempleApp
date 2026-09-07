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

data class TempleSettings(
    val templeImageUrl: String = "",
    val monkImageUrl: String = "",
    val monkNameSi: String = "",
    val monkNameEn: String = "",
    val monkTitleSi: String = "විහාරාධිපති ස්වාමීන් වහන්සේ",
    val monkTitleEn: String = "Temple Resident Monk",
    val facebookUrl: String = "",
    val sabhapathiNameSi: String = "",
    val sabhapathiNameEn: String = "",
    val sabhapathiPhone: String = "",
    val bandagarikaNameSi: String = "",
    val bandagarikaNameEn: String = "",
    val bandagarikaPhone: String = "",
    val lekamNameSi: String = "",
    val lekamNameEn: String = "",
    val lekamPhone: String = ""
)
