package com.catools.templeapp.data

object SampleData {
    val events = listOf(
        TempleEvent(
            id = "1",
            titleSi = "බෝධි පූජාමය පිංකම",
            titleEn = "Bodhi Pooja",
            date = "2026-09-13",
            time = "18:00",
            description = "සැදැහැවත් ඔබ සැමට බැති සිතින් සහභාගී වන ලෙස ආරාධනා කරමු."
        ),
        TempleEvent(
            id = "2",
            titleSi = "විශේෂ ධර්ම දේශනය",
            titleEn = "Special Dhamma Sermon",
            date = "2026-09-20",
            time = "18:30",
            description = "විශේෂ ධර්ම දේශනාව සහ ආශිර්වාද පූජාව."
        ),
        TempleEvent(
            id = "3",
            titleSi = "ශ්‍රමදාන වැඩසටහන",
            titleEn = "Temple Volunteer Day",
            date = "2026-09-27",
            time = "08:00",
            description = "විහාරස්ථානයේ ශ්‍රමදාන වැඩසටහන සඳහා ඔබ සැමට ආරාධනා."
        )
    )

    val notices = listOf(
        TempleNotice("1", "දැනුම්දීම", "ඉදිරි ඉරු දින බෝධි පූජාව සවස 6.00ට පැවැත්වේ.", "2026-09-07"),
        TempleNotice("2", "දායක සභාව", "මාසික දායක සභා රැස්වීම පිළිබඳ විස්තර ඉදිරියේදී දැනුම් දෙනු ලැබේ.", "2026-09-05")
    )
}
