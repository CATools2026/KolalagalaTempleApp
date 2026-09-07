package com.catools.templeapp.data

object SampleData {
    val events = listOf(
        TempleEvent(
            id = "sample-1",
            titleSi = "බෝධි පූජාමය පිංකම",
            titleEn = "Bodhi Pooja",
            date = "2026-09-13",
            time = "18:00",
            descriptionSi = "සැදැහැවත් ඔබ සැමට බැති සිතින් සහභාගී වන ලෙස ආරාධනා කරමු.",
            descriptionEn = "All devotees are warmly invited to take part in the Bodhi Pooja."
        ),
        TempleEvent(
            id = "sample-2",
            titleSi = "විශේෂ ධර්ම දේශනය",
            titleEn = "Special Dhamma Sermon",
            date = "2026-09-20",
            time = "18:30",
            descriptionSi = "විශේෂ ධර්ම දේශනාව සහ ආශිර්වාද පූජාව.",
            descriptionEn = "A special Dhamma sermon and blessing ceremony."
        )
    )

    val notices = listOf(
        TempleNotice(
            id = "sample-1",
            titleSi = "දැනුම්දීම",
            titleEn = "Announcement",
            messageSi = "ඉදිරි ඉරු දින බෝධි පූජාව සවස 6.00ට පැවැත්වේ.",
            messageEn = "The next Sunday Bodhi Pooja will be held at 6.00 PM.",
            date = "2026-09-07"
        ),
        TempleNotice(
            id = "sample-2",
            titleSi = "දායක සභාව",
            titleEn = "Temple Committee",
            messageSi = "මාසික දායක සභා රැස්වීම පිළිබඳ විස්තර ඉදිරියේදී දැනුම් දෙනු ලැබේ.",
            messageEn = "Details of the monthly committee meeting will be announced soon.",
            date = "2026-09-05"
        )
    )

    val poojaTimes = listOf(
        "05:30" to "Morning Buddha Pooja",
        "18:00" to "Evening Bodhi Pooja",
        "19:00" to "Dhamma / Pirith"
    )
}
