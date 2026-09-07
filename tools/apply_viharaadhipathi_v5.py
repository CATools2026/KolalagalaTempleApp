from pathlib import Path
import base64

ui_path = Path("app/src/main/java/com/catools/templeapp/ui/TempleAppUi.kt")
portrait_b64 = Path("tools/viharaadhipathi_portrait.b64")
portrait_path = Path("app/src/main/res/drawable-nodpi/viharaadhipathi_portrait.jpg")
portrait_path.parent.mkdir(parents=True, exist_ok=True)
portrait_path.write_bytes(base64.b64decode(portrait_b64.read_text().strip()))

text = ui_path.read_text(encoding="utf-8")

if "Page.VIHARAADHIPATHI" not in text:
    def replace_once(old: str, new: str):
        global text
        if old not in text:
            raise SystemExit(f"V5 patch pattern not found:\n{old[:180]}")
        text = text.replace(old, new, 1)

    replace_once(
        "private enum class Page { HOME, POOJA, EVENTS, FACEBOOK, MORE, ABOUT, CONTACT, NOTICES, ADMIN }",
        "private enum class Page { HOME, POOJA, EVENTS, FACEBOOK, MORE, ABOUT, CONTACT, NOTICES, VIHARAADHIPATHI, ADMIN }"
    )

    replace_once(
        "                Page.NOTICES -> NoticesScreen(language, notices) { page = Page.HOME }\n                Page.ADMIN -> AdminScreen(",
        "                Page.NOTICES -> NoticesScreen(language, notices) { page = Page.HOME }\n                Page.VIHARAADHIPATHI -> ViharaadhipathiScreen(language) { page = Page.MORE }\n                Page.ADMIN -> AdminScreen("
    )

    replace_once(
        "        item { MonkProfileCard(language, settings) }",
        "        item { ViharaadhipathiHomeCard(language) { onPage(Page.VIHARAADHIPATHI) } }"
    )

    replace_once(
        "        ActionItem(\"Facebook\", tr(language, \"ඡායාරූප හා වීඩියෝ\", \"Photos & videos\"), Icons.Default.Public, Page.FACEBOOK),\n        ActionItem(tr(language, \"විහාරය ගැන\", \"About Temple\"),",
        "        ActionItem(\"Facebook\", tr(language, \"ඡායාරූප හා වීඩියෝ\", \"Photos & videos\"), Icons.Default.Public, Page.FACEBOOK),\n        ActionItem(tr(language, \"විහාරාධිපති\", \"Viharaadhipathi\"), tr(language, \"ගරු ස්වාමීන් වහන්සේ\", \"Chief Incumbent\"), Icons.Default.Person, Page.VIHARAADHIPATHI),\n        ActionItem(tr(language, \"විහාරය ගැන\", \"About Temple\"),"
    )

    replace_once(
        "    val rows = listOf(\n        MoreItem(\"Facebook\", Icons.Default.Public, Page.FACEBOOK),",
        "    val rows = listOf(\n        MoreItem(tr(language, \"විහාරාධිපති\", \"Viharaadhipathi\"), Icons.Default.Person, Page.VIHARAADHIPATHI),\n        MoreItem(\"Facebook\", Icons.Default.Public, Page.FACEBOOK),"
    )

    marker = "@Composable\nprivate fun QuotePanel(language: AppLanguage) {"
    new_code = r'''@Composable
private fun ViharaadhipathiHomeCard(language: AppLanguage, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 18.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.viharaadhipathi_portrait),
                contentDescription = tr(language, "විහාරාධිපති ස්වාමීන් වහන්සේ", "Viharaadhipathi"),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(112.dp).clip(RoundedCornerShape(20.dp))
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    tr(language, "විහාරාධිපති", "Viharaadhipathi"),
                    color = TempleGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    tr(
                        language,
                        "ගරු කටයුතු ගිණිපෙන්දේ ධම්මරතන ස්වාමින් වහන්සේ",
                        "Venerable Ginipende Dhammarathana Thero"
                    ),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TempleDeepGold,
                    lineHeight = 22.sp
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    tr(language, "උභය විහාරාධිපති", "Chief Incumbent of Both Temples"),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tr(language, "විස්තර බලන්න", "View profile"), color = TempleGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TempleGold, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun ViharaadhipathiScreen(language: AppLanguage, onBack: () -> Unit) {
    val sinhalaFullName = "කොළලෑගල පුරාණ විහාරය සහ නාථගනය රාජමහා විහාරය යන උභය විහාරාධිපති ගරු කටයුතු ගිණිපෙන්දේ ධම්මරතන ස්වාමින් වහන්සේ"
    val englishFullName = "Venerable Ginipende Dhammarathana Thero, Chief Incumbent of Kolalagala Ancient Temple and Nathaganaya Rajamaha Viharaya"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            PageHeader(
                tr(language, "විහාරාධිපති", "Viharaadhipathi"),
                tr(language, "කොළලෑගල පුරාණ විහාරස්ථානය", "Kolalagala Ancient Temple"),
                onBack
            )
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(R.drawable.viharaadhipathi_portrait),
                        contentDescription = tr(language, "විහාරාධිපති ස්වාමීන් වහන්සේ", "Viharaadhipathi"),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    )
                    Column(Modifier.padding(22.dp)) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = TempleSoftGold
                        ) {
                            Text(
                                tr(language, "උභය විහාරාධිපති", "Chief Incumbent of Both Temples"),
                                color = TempleDeepGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        Text(
                            tr(language, sinhalaFullName, englishFullName),
                            color = TempleDeepGold,
                            fontSize = if (language == AppLanguage.SI) 20.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = if (language == AppLanguage.SI) 30.sp else 26.sp
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(
                            tr(
                                language,
                                "කොළලෑගල පුරාණ විහාරය සහ නාථගනය රාජමහා විහාරය යන උභය විහාරස්ථානවල විහාරාධිපති ස්වාමීන් වහන්සේ.",
                                "Chief Incumbent of Kolalagala Ancient Temple and Nathaganaya Rajamaha Viharaya."
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

'''
    if marker not in text:
        raise SystemExit("Could not locate QuotePanel insertion point")
    text = text.replace(marker, new_code + marker, 1)

    ui_path.write_text(text, encoding="utf-8")

print("V5 Viharaadhipathi UI patch applied")
