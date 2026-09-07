package com.catools.templeapp.ui

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.catools.templeapp.R
import com.catools.templeapp.data.SampleData
import com.catools.templeapp.data.TempleEvent
import com.catools.templeapp.data.TempleNotice
import com.catools.templeapp.data.TempleRepository
import com.catools.templeapp.data.TempleSettings
import com.catools.templeapp.ui.theme.TempleDeepGold
import com.catools.templeapp.ui.theme.TempleGold
import com.catools.templeapp.ui.theme.TempleSoftGold

private enum class AppLanguage { SI, EN }
private enum class Page { HOME, POOJA, EVENTS, FACEBOOK, MORE, ABOUT, CONTACT, NOTICES, ADMIN }

private fun tr(language: AppLanguage, si: String, en: String): String =
    if (language == AppLanguage.SI) si else en

private fun normalizedUrl(url: String): String {
    val value = url.trim()
    return if (value.startsWith("http://") || value.startsWith("https://")) value else "https://$value"
}

@Composable
fun TempleApp(repository: TempleRepository) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("temple_settings", 0) }
    var language by remember {
        mutableStateOf(if (prefs.getString("language", "si") == "en") AppLanguage.EN else AppLanguage.SI)
    }
    var page by remember { mutableStateOf(Page.HOME) }
    var events by remember { mutableStateOf(SampleData.events) }
    var notices by remember { mutableStateOf(SampleData.notices) }
    var settings by remember { mutableStateOf(TempleSettings()) }

    DisposableEffect(repository.firebaseReady) {
        val eventListener = repository.observeEvents { events = it }
        val noticeListener = repository.observeNotices { notices = it }
        val settingsListener = repository.observeSettings { settings = it }
        onDispose {
            eventListener?.remove()
            noticeListener?.remove()
            settingsListener?.remove()
        }
    }

    fun toggleLanguage() {
        language = if (language == AppLanguage.SI) AppLanguage.EN else AppLanguage.SI
        prefs.edit().putString("language", if (language == AppLanguage.EN) "en" else "si").apply()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            MainBottomBar(page = page, language = language, onPage = { page = it })
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (page) {
                Page.HOME -> HomeScreen(
                    language = language,
                    events = events,
                    notices = notices,
                    settings = settings,
                    firebaseReady = repository.firebaseReady,
                    onLanguage = ::toggleLanguage,
                    onPage = { page = it }
                )
                Page.POOJA -> PoojaTimesScreen(language)
                Page.EVENTS -> EventsScreen(language, events)
                Page.FACEBOOK -> FacebookScreen(language, settings)
                Page.MORE -> MoreScreen(language, repository.firebaseReady, settings, ::toggleLanguage) { page = it }
                Page.ABOUT -> AboutScreen(language) { page = Page.MORE }
                Page.CONTACT -> ContactScreen(language, settings) { page = Page.MORE }
                Page.NOTICES -> NoticesScreen(language, notices) { page = Page.HOME }
                Page.ADMIN -> AdminScreen(
                    language = language,
                    repository = repository,
                    events = events,
                    notices = notices,
                    settings = settings,
                    onBack = { page = Page.MORE }
                )
            }
        }
    }
}

@Composable
private fun MainBottomBar(page: Page, language: AppLanguage, onPage: (Page) -> Unit) {
    val items = listOf(
        Triple(Page.HOME, tr(language, "මුල් පිටුව", "Home"), Icons.Default.Home),
        Triple(Page.POOJA, tr(language, "පූජා", "Pooja"), Icons.Default.CalendarMonth),
        Triple(Page.EVENTS, tr(language, "පිංකම්", "Events"), Icons.Default.Spa),
        Triple(Page.FACEBOOK, "Facebook", Icons.Default.Public),
        Triple(Page.MORE, tr(language, "තවත්", "More"), Icons.Default.MoreHoriz)
    )
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.navigationBarsPadding()
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = page == item.first,
                onClick = { onPage(item.first) },
                icon = { Icon(item.third, contentDescription = null) },
                label = { Text(item.second, fontSize = 10.sp, maxLines = 1) }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    language: AppLanguage,
    events: List<TempleEvent>,
    notices: List<TempleNotice>,
    settings: TempleSettings,
    firebaseReady: Boolean,
    onLanguage: () -> Unit,
    onPage: (Page) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { HomeTopHeader(language, firebaseReady, onLanguage) }
        item { HeroCard(language, settings) }
        item { QuickActionGrid(language, onPage) }
        item { AnnouncementPanel(language, notices, onSeeAll = { onPage(Page.NOTICES) }) }
        item { MonkProfileCard(language, settings) }
        item { QuotePanel(language) }
        if (events.isNotEmpty()) {
            item {
                SectionTitle(
                    tr(language, "ඉදිරි වැඩසටහන", "Next program"),
                    tr(language, "විස්තර බලන්න", "View details")
                )
            }
            item { EventCard(events.first(), language, Modifier.padding(horizontal = 18.dp)) }
        }
    }
}

@Composable
private fun HomeTopHeader(language: AppLanguage, firebaseReady: Boolean, onLanguage: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = CircleShape, color = TempleSoftGold, modifier = Modifier.size(56.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Spa, contentDescription = null, tint = TempleGold, modifier = Modifier.size(34.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Kolalagala Ancient Temple", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = TempleDeepGold)
            Text(tr(language, "සාදරයෙන් පිළිගනිමු", "Welcome"), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 15.sp)
        }
        TextButton(onClick = onLanguage) {
            Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text(if (language == AppLanguage.SI) "EN" else "සිං")
        }
        Box {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = TempleDeepGold, modifier = Modifier.size(28.dp))
            if (firebaseReady) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE53935)).align(Alignment.TopEnd))
            }
        }
    }
}

@Composable
private fun HeroCard(language: AppLanguage, settings: TempleSettings) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            if (settings.templeImageUrl.isBlank()) {
                Image(
                    painter = painterResource(R.drawable.temple_home),
                    contentDescription = "Kolalagala Ancient Temple",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = settings.templeImageUrl,
                    contentDescription = "Kolalagala Ancient Temple",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    Modifier.matchParentSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f)))
                    )
                )
                Column(Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                    Text(
                        tr(language, "සාමයේ නවාතැනක් • උසස් සිතකට මඟක්", "A PLACE OF PEACE • A PATH TO HIGHER MINDS"),
                        color = Color(0xFFFFC739), fontSize = 11.sp, fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        tr(language, "කොළලෑගල පුරාණ විහාරස්ථානය", "Kolalagala Ancient Temple"),
                        color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionGrid(language: AppLanguage, onPage: (Page) -> Unit) {
    val actions = listOf(
        ActionItem(tr(language, "පූජා වේලාවන්", "Pooja Times"), tr(language, "දෛනික පූජා", "Daily services"), Icons.Default.AccessTime, Page.POOJA),
        ActionItem(tr(language, "පිංකම්", "Events"), tr(language, "ඉදිරි වැඩසටහන්", "Upcoming programs"), Icons.Default.Event, Page.EVENTS),
        ActionItem("Facebook", tr(language, "ඡායාරූප හා වීඩියෝ", "Photos & videos"), Icons.Default.Public, Page.FACEBOOK),
        ActionItem(tr(language, "විහාරය ගැන", "About Temple"), tr(language, "ඉතිහාසය හා වැදගත්කම", "History & significance"), Icons.Default.AccountBalance, Page.ABOUT),
        ActionItem(tr(language, "සම්බන්ධ වන්න", "Contact"), tr(language, "දායක සභා සම්බන්ධතා", "Committee contacts"), Icons.Default.Phone, Page.CONTACT),
        ActionItem(tr(language, "දැනුම්දීම්", "Announcements"), tr(language, "අලුත්ම තොරතුරු", "Latest updates"), Icons.Default.Campaign, Page.NOTICES)
    )

    Column(Modifier.padding(horizontal = 18.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        actions.chunked(3).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { action -> HomeActionCard(action, Modifier.weight(1f)) { onPage(action.page) } }
            }
        }
    }
}

private data class ActionItem(val title: String, val subtitle: String, val icon: ImageVector, val page: Page)

@Composable
private fun HomeActionCard(action: ActionItem, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(150.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Surface(shape = CircleShape, color = TempleSoftGold, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(action.icon, contentDescription = null, tint = TempleGold, modifier = Modifier.size(26.dp))
                }
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(action.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp), tint = TempleDeepGold)
                }
                Text(action.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, lineHeight = 14.sp)
            }
        }
    }
}

@Composable
private fun AnnouncementPanel(language: AppLanguage, notices: List<TempleNotice>, onSeeAll: () -> Unit) {
    val latest = notices.firstOrNull()
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1D8))
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = TempleGold, modifier = Modifier.size(30.dp))
                Spacer(Modifier.width(10.dp))
                Text(tr(language, "දැනුම්දීම්", "Announcements"), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TempleDeepGold, modifier = Modifier.weight(1f))
                TextButton(onClick = onSeeAll) {
                    Text(tr(language, "සියල්ල", "See All"), color = TempleDeepGold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TempleDeepGold)
                }
            }
            if (latest != null) {
                Spacer(Modifier.height(8.dp))
                Text(tr(language, latest.titleSi, latest.titleEn), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(tr(language, latest.messageSi, latest.messageEn), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, maxLines = 3)
                if (latest.date.isNotBlank()) Text(latest.date, color = TempleDeepGold, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
            } else {
                Text(tr(language, "දැනුම්දීම් නොමැත", "No announcements yet"), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MonkProfileCard(language: AppLanguage, settings: TempleSettings) {
    val monkName = tr(language, settings.monkNameSi, settings.monkNameEn)
    val monkTitle = tr(language, settings.monkTitleSi, settings.monkTitleEn)
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 18.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = TempleSoftGold, modifier = Modifier.size(92.dp)) {
                if (settings.monkImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = settings.monkImageUrl,
                        contentDescription = monkName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = TempleGold, modifier = Modifier.size(46.dp))
                    }
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(tr(language, "අපගේ ස්වාමීන් වහන්සේ", "Our Temple Monk"), color = TempleGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(if (monkName.isBlank()) monkTitle else monkName, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = TempleDeepGold)
                if (monkName.isNotBlank() && monkTitle.isNotBlank()) {
                    Text(monkTitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun QuotePanel(language: AppLanguage) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 2.dp)
            .height(150.dp).clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFFFFAF1), Color(0xFFFFE6AE))))
    ) {
        Column(Modifier.align(Alignment.Center).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                tr(language, "“සියලු සත්වයෝ සුවපත් වෙත්වා,\nසියලු සත්වයෝ සාමයෙන් වෙසෙත්වා.”", "“May all beings be happy,\nMay all beings be at peace.”"),
                textAlign = TextAlign.Center, color = TempleDeepGold, fontSize = 18.sp, fontStyle = FontStyle.Italic, lineHeight = 25.sp
            )
            Spacer(Modifier.height(8.dp))
            Text("— Buddha", color = TempleDeepGold)
        }
    }
}

@Composable
private fun SectionTitle(title: String, trailing: String? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        trailing?.let { Text(it, color = TempleGold, fontSize = 12.sp) }
    }
}

@Composable
private fun PageHeader(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TempleDeepGold)
            subtitle?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp) }
        }
    }
}

@Composable
private fun PoojaTimesScreen(language: AppLanguage) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { PageHeader(tr(language, "පූජා වේලාවන්", "Pooja Times"), tr(language, "දෛනික ආගමික වැඩසටහන්", "Daily temple services")) }
        items(SampleData.poojaTimes) { item ->
            Card(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 7.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = TempleSoftGold, modifier = Modifier.size(50.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.AccessTime, null, tint = TempleGold) }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(item.first, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TempleDeepGold)
                        Text(item.second, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun EventsScreen(language: AppLanguage, events: List<TempleEvent>) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { PageHeader(tr(language, "පිංකම් හා වැඩසටහන්", "Events & Programs"), tr(language, "ඉදිරි විහාර වැඩසටහන්", "Upcoming temple programs")) }
        if (events.isEmpty()) item { EmptyState(tr(language, "පිංකම් තවම එක් කර නැත", "No events have been added yet")) }
        items(events) { event -> EventCard(event, language, Modifier.padding(horizontal = 18.dp, vertical = 7.dp)) }
    }
}

@Composable
private fun EventCard(event: TempleEvent, language: AppLanguage, modifier: Modifier = Modifier) {
    Card(modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = TempleSoftGold, modifier = Modifier.size(46.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Spa, null, tint = TempleGold) }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(tr(language, event.titleSi, event.titleEn), fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("${event.date}  •  ${event.time}", color = TempleGold, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
            val description = tr(language, event.descriptionSi, event.descriptionEn)
            if (description.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(18.dp), tint = TempleDeepGold)
                Spacer(Modifier.width(4.dp))
                Text(tr(language, event.locationSi, event.locationEn), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun FacebookScreen(language: AppLanguage, settings: TempleSettings) {
    val context = LocalContext.current
    val url = settings.facebookUrl.trim()
    Column(Modifier.fillMaxSize()) {
        PageHeader("Facebook", tr(language, "විහාරස්ථානයේ ඡායාරූප හා වීඩියෝ", "Temple photos and videos"))
        if (url.isBlank()) {
            EmptyState(tr(language, "Facebook පිටුව Admin මගින් එක් කරන්න.", "Add the temple Facebook page from Admin."))
        } else {
            OutlinedButton(
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(normalizedUrl(url)))) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Public, null)
                Spacer(Modifier.width(8.dp))
                Text(tr(language, "Facebook App / Browser එකෙන් විවෘත කරන්න", "Open in Facebook / Browser"))
            }
            AndroidView(
                modifier = Modifier.fillMaxSize().padding(top = 6.dp),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = WebViewClient()
                        loadUrl(normalizedUrl(url))
                    }
                },
                update = { webView ->
                    val target = normalizedUrl(url)
                    if (webView.url != target) webView.loadUrl(target)
                }
            )
        }
    }
}

@Composable
private fun MoreScreen(language: AppLanguage, firebaseReady: Boolean, settings: TempleSettings, onLanguage: () -> Unit, onPage: (Page) -> Unit) {
    val rows = listOf(
        MoreItem("Facebook", Icons.Default.Public, Page.FACEBOOK),
        MoreItem(tr(language, "විහාරස්ථානය පිළිබඳ", "About Temple"), Icons.Default.AccountBalance, Page.ABOUT),
        MoreItem(tr(language, "සම්බන්ධ වන්න", "Contact"), Icons.Default.Phone, Page.CONTACT),
        MoreItem(tr(language, "පරිපාලක පිවිසුම", "Admin Login"), Icons.Default.AdminPanelSettings, Page.ADMIN)
    )
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { PageHeader(tr(language, "තවත්", "More"), tr(language, "විහාරස්ථාන සේවා හා සැකසුම්", "Temple services and settings")) }
        item {
            Card(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, null, tint = TempleGold)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(tr(language, "භාෂාව", "Language"), fontWeight = FontWeight.Bold)
                        Text(if (language == AppLanguage.SI) "සිංහල" else "English", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(onClick = onLanguage) { Text(if (language == AppLanguage.SI) "English" else "සිංහල") }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (firebaseReady) Color(0xFFEFF8EC) else Color(0xFFFFF1E8))) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (firebaseReady) Icons.Default.CloudDone else Icons.Default.CloudOff, null, tint = if (firebaseReady) Color(0xFF3D7B37) else Color(0xFFB85C26))
                    Spacer(Modifier.width(12.dp))
                    Text(if (firebaseReady) tr(language, "Firebase සම්බන්ධයි", "Firebase connected") else tr(language, "Firebase සැකසුම අවශ්‍යයි", "Firebase setup required"), fontWeight = FontWeight.SemiBold)
                }
            }
        }
        if (settings.monkNameSi.isNotBlank() || settings.monkNameEn.isNotBlank()) {
            item { MonkProfileCard(language, settings) }
        }
        items(rows) { item ->
            Card(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp).clickable { onPage(item.page) }, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = TempleSoftGold, modifier = Modifier.size(44.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(item.icon, null, tint = TempleGold) }
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(item.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, null, tint = TempleDeepGold)
                }
            }
        }
    }
}

private data class MoreItem(val title: String, val icon: ImageVector, val page: Page)

@Composable
private fun AboutScreen(language: AppLanguage, onBack: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { PageHeader(tr(language, "විහාරස්ථානය පිළිබඳ", "About the Temple"), null, onBack) }
        item {
            Card(Modifier.fillMaxWidth().padding(18.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(22.dp)) {
                    Icon(Icons.Default.AccountBalance, null, tint = TempleGold, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Kolalagala Ancient Temple", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TempleDeepGold)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        tr(language, "කොළලෑගල පුරාණ විහාරස්ථානයේ නිල තොරතුරු, ඉතිහාසය සහ ආගමික වැඩසටහන් මෙම යෙදුම හරහා දැනගත හැක.", "Official temple information, history and religious programs are available through this app."),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactScreen(language: AppLanguage, settings: TempleSettings, onBack: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { PageHeader(tr(language, "සම්බන්ධ වන්න", "Contact"), tr(language, "දායක සභාවේ නිල සම්බන්ධතා", "Official committee contacts"), onBack) }
        item { ContactPersonCard(language, tr(language, "සභාපති", "President"), settings.sabhapathiNameSi, settings.sabhapathiNameEn, settings.sabhapathiPhone) }
        item { ContactPersonCard(language, tr(language, "භාණ්ඩාගාරික", "Treasurer"), settings.bandagarikaNameSi, settings.bandagarikaNameEn, settings.bandagarikaPhone) }
        item { ContactPersonCard(language, tr(language, "ලේකම්", "Secretary"), settings.lekamNameSi, settings.lekamNameEn, settings.lekamPhone) }
    }
}

@Composable
private fun ContactPersonCard(language: AppLanguage, role: String, nameSi: String, nameEn: String, phone: String) {
    val context = LocalContext.current
    val name = tr(language, nameSi, nameEn)
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 7.dp)
            .clickable(enabled = phone.isNotBlank()) {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = TempleSoftGold, modifier = Modifier.size(52.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Phone, null, tint = TempleGold) }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(role, color = TempleGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(if (name.isBlank()) tr(language, "නම එක් කර නැත", "Name not added") else name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(if (phone.isBlank()) tr(language, "දුරකථන අංකය එක් කර නැත", "Phone number not added") else phone, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (phone.isNotBlank()) Icon(Icons.Default.ChevronRight, null, tint = TempleDeepGold)
        }
    }
}

@Composable
private fun NoticesScreen(language: AppLanguage, notices: List<TempleNotice>, onBack: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { PageHeader(tr(language, "දැනුම්දීම්", "Announcements"), null, onBack) }
        if (notices.isEmpty()) item { EmptyState(tr(language, "දැනුම්දීම් නොමැත", "No announcements yet")) }
        items(notices) { notice ->
            Card(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 7.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, null, tint = TempleGold)
                        Spacer(Modifier.width(10.dp))
                        Text(tr(language, notice.titleSi, notice.titleEn), fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(tr(language, notice.messageSi, notice.messageEn), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (notice.date.isNotBlank()) Text(notice.date, color = TempleDeepGold, fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Spa, null, tint = TempleGold.copy(alpha = 0.45f), modifier = Modifier.size(54.dp))
        Spacer(Modifier.height(12.dp))
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AdminScreen(
    language: AppLanguage,
    repository: TempleRepository,
    events: List<TempleEvent>,
    notices: List<TempleNotice>,
    settings: TempleSettings,
    onBack: () -> Unit
) {
    var signedIn by remember { mutableStateOf(repository.isSignedIn()) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 30.dp)) {
        item { PageHeader(tr(language, "පරිපාලක පැනලය", "Temple Admin"), tr(language, "දැනුම්දීම්, පිංකම් සහ විහාර තොරතුරු පාලනය", "Manage announcements, events and temple information"), onBack) }

        if (!repository.firebaseReady) {
            item { EmptyState(tr(language, "Firebase සම්බන්ධ කර නැත", "Firebase is not configured")) }
        } else if (!signedIn) {
            item {
                Card(Modifier.fillMaxWidth().padding(18.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(22.dp)) {
                        Icon(Icons.Default.Lock, null, tint = TempleGold, modifier = Modifier.size(42.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Admin Login", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(tr(language, "මුරපදය", "Password")) }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                status = tr(language, "පිවිසෙමින්...", "Signing in...")
                                repository.signInAdmin(email, password) { result ->
                                    if (result.isSuccess) { signedIn = true; status = "" }
                                    else status = result.exceptionOrNull()?.message.orEmpty()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = email.isNotBlank() && password.isNotBlank()
                        ) { Text(tr(language, "පිවිසෙන්න", "Sign In")) }
                        if (status.isNotBlank()) Text(status, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 10.dp))
                    }
                }
            }
        } else {
            item {
                Card(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF8EC))) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF3D7B37))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(tr(language, "Admin ලෙස පිවිස ඇත", "Signed in as admin"), fontWeight = FontWeight.Bold)
                            Text(repository.signedInEmail(), fontSize = 12.sp)
                        }
                        IconButton(onClick = { repository.signOut(); signedIn = false }) { Icon(Icons.Default.Logout, null) }
                    }
                }
            }
            item {
                AdminManager(language, repository, events, notices, settings) { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
private fun AdminManager(
    language: AppLanguage,
    repository: TempleRepository,
    events: List<TempleEvent>,
    notices: List<TempleNotice>,
    settings: TempleSettings,
    toast: (String) -> Unit
) {
    var section by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = section == 0, onClick = { section = 0 }, label = { Text(tr(language, "දැනුම්දීම්", "Announcements")) })
            FilterChip(selected = section == 1, onClick = { section = 1 }, label = { Text(tr(language, "පිංකම්", "Events")) })
            FilterChip(selected = section == 2, onClick = { section = 2 }, label = { Text(tr(language, "විහාර තොරතුරු", "Temple Info")) })
        }
        Spacer(Modifier.height(12.dp))
        when (section) {
            0 -> AnnouncementManager(language, repository, notices, toast)
            1 -> EventManager(language, repository, events, toast)
            else -> TempleInfoManager(language, repository, settings, toast)
        }
    }
}

@Composable
private fun AnnouncementManager(language: AppLanguage, repository: TempleRepository, notices: List<TempleNotice>, toast: (String) -> Unit) {
    var titleSi by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var messageSi by remember { mutableStateOf("") }
    var messageEn by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var notifyUsers by remember { mutableStateOf(true) }

    AdminCard(title = tr(language, "නව දැනුම්දීම", "New Announcement"), icon = Icons.Default.Campaign) {
        AdminField(titleSi, { titleSi = it }, "සිංහල මාතෘකාව")
        AdminField(titleEn, { titleEn = it }, "English title")
        AdminField(messageSi, { messageSi = it }, "සිංහල පණිවිඩය", singleLine = false)
        AdminField(messageEn, { messageEn = it }, "English message", singleLine = false)
        AdminField(date, { date = it }, "Date (YYYY-MM-DD)")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = notifyUsers, onCheckedChange = { notifyUsers = it })
            Text(tr(language, "Push notification යවන්න", "Send push notification"))
        }
        Button(
            onClick = {
                repository.addNotice(TempleNotice(titleSi = titleSi, titleEn = titleEn, messageSi = messageSi, messageEn = messageEn, date = date, notifyUsers = notifyUsers)) { result ->
                    if (result.isSuccess) {
                        titleSi = ""; titleEn = ""; messageSi = ""; messageEn = ""; date = ""
                        toast(tr(language, "දැනුම්දීම පළ කළා", "Announcement published"))
                    } else toast(result.exceptionOrNull()?.message ?: "Error")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = titleSi.isNotBlank() || titleEn.isNotBlank()
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(6.dp))
            Text(tr(language, "පළ කරන්න", "Publish"))
        }
    }

    Spacer(Modifier.height(16.dp))
    AdminListTitle(tr(language, "පළ කළ දැනුම්දීම්", "Published announcements"))
    notices.filterNot { it.id.startsWith("sample-") }.forEach { notice ->
        AdminListRow(tr(language, notice.titleSi, notice.titleEn), notice.date) {
            repository.deleteNotice(notice.id) { result -> if (result.isFailure) toast(result.exceptionOrNull()?.message ?: "Error") }
        }
    }
}

@Composable
private fun EventManager(language: AppLanguage, repository: TempleRepository, events: List<TempleEvent>, toast: (String) -> Unit) {
    var titleSi by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var descriptionSi by remember { mutableStateOf("") }
    var descriptionEn by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var locationSi by remember { mutableStateOf("කොළලෑගල පුරාණ විහාරස්ථානය") }
    var locationEn by remember { mutableStateOf("Kolalagala Ancient Temple") }
    var notifyUsers by remember { mutableStateOf(true) }

    AdminCard(title = tr(language, "නව පිංකම / වැඩසටහන", "New Event / Program"), icon = Icons.Default.Event) {
        AdminField(titleSi, { titleSi = it }, "සිංහල මාතෘකාව")
        AdminField(titleEn, { titleEn = it }, "English title")
        AdminField(descriptionSi, { descriptionSi = it }, "සිංහල විස්තරය", singleLine = false)
        AdminField(descriptionEn, { descriptionEn = it }, "English description", singleLine = false)
        AdminField(date, { date = it }, "Date (YYYY-MM-DD)")
        AdminField(time, { time = it }, "Time (HH:MM)")
        AdminField(locationSi, { locationSi = it }, "සිංහල ස්ථානය")
        AdminField(locationEn, { locationEn = it }, "English location")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = notifyUsers, onCheckedChange = { notifyUsers = it })
            Text(tr(language, "Push notification යවන්න", "Send push notification"))
        }
        Button(
            onClick = {
                repository.addEvent(TempleEvent(titleSi = titleSi, titleEn = titleEn, date = date, time = time, descriptionSi = descriptionSi, descriptionEn = descriptionEn, locationSi = locationSi, locationEn = locationEn, notifyUsers = notifyUsers)) { result ->
                    if (result.isSuccess) {
                        titleSi = ""; titleEn = ""; descriptionSi = ""; descriptionEn = ""; date = ""; time = ""
                        toast(tr(language, "පිංකම එක් කළා", "Event added"))
                    } else toast(result.exceptionOrNull()?.message ?: "Error")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = (titleSi.isNotBlank() || titleEn.isNotBlank()) && date.isNotBlank()
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(6.dp))
            Text(tr(language, "එක් කරන්න", "Add Event"))
        }
    }

    Spacer(Modifier.height(16.dp))
    AdminListTitle(tr(language, "පවතින පිංකම්", "Existing events"))
    events.filterNot { it.id.startsWith("sample-") }.forEach { event ->
        AdminListRow(tr(language, event.titleSi, event.titleEn), "${event.date} ${event.time}") {
            repository.deleteEvent(event.id) { result -> if (result.isFailure) toast(result.exceptionOrNull()?.message ?: "Error") }
        }
    }
}

@Composable
private fun TempleInfoManager(language: AppLanguage, repository: TempleRepository, settings: TempleSettings, toast: (String) -> Unit) {
    var templeImageUrl by remember(settings.templeImageUrl) { mutableStateOf(settings.templeImageUrl) }
    var monkImageUrl by remember(settings.monkImageUrl) { mutableStateOf(settings.monkImageUrl) }
    var monkNameSi by remember(settings.monkNameSi) { mutableStateOf(settings.monkNameSi) }
    var monkNameEn by remember(settings.monkNameEn) { mutableStateOf(settings.monkNameEn) }
    var monkTitleSi by remember(settings.monkTitleSi) { mutableStateOf(settings.monkTitleSi) }
    var monkTitleEn by remember(settings.monkTitleEn) { mutableStateOf(settings.monkTitleEn) }
    var facebookUrl by remember(settings.facebookUrl) { mutableStateOf(settings.facebookUrl) }
    var sabhapathiNameSi by remember(settings.sabhapathiNameSi) { mutableStateOf(settings.sabhapathiNameSi) }
    var sabhapathiNameEn by remember(settings.sabhapathiNameEn) { mutableStateOf(settings.sabhapathiNameEn) }
    var sabhapathiPhone by remember(settings.sabhapathiPhone) { mutableStateOf(settings.sabhapathiPhone) }
    var bandagarikaNameSi by remember(settings.bandagarikaNameSi) { mutableStateOf(settings.bandagarikaNameSi) }
    var bandagarikaNameEn by remember(settings.bandagarikaNameEn) { mutableStateOf(settings.bandagarikaNameEn) }
    var bandagarikaPhone by remember(settings.bandagarikaPhone) { mutableStateOf(settings.bandagarikaPhone) }
    var lekamNameSi by remember(settings.lekamNameSi) { mutableStateOf(settings.lekamNameSi) }
    var lekamNameEn by remember(settings.lekamNameEn) { mutableStateOf(settings.lekamNameEn) }
    var lekamPhone by remember(settings.lekamPhone) { mutableStateOf(settings.lekamPhone) }
    var templeUri by remember { mutableStateOf<Uri?>(null) }
    var monkUri by remember { mutableStateOf<Uri?>(null) }
    var uploadingTemple by remember { mutableStateOf(false) }
    var uploadingMonk by remember { mutableStateOf(false) }

    val templePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> templeUri = uri }
    val monkPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> monkUri = uri }

    AdminCard(tr(language, "විහාර රූපය", "Temple Home Image"), Icons.Default.AddPhotoAlternate) {
        if (templeUri != null) {
            AsyncImage(model = templeUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(16.dp)))
        } else if (templeImageUrl.isNotBlank()) {
            AsyncImage(model = templeImageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(16.dp)))
        } else {
            Image(painterResource(R.drawable.temple_home), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(16.dp)))
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = { templePicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.AddPhotoAlternate, null)
            Spacer(Modifier.width(8.dp))
            Text(tr(language, "විහාර රූපය තෝරන්න", "Choose Temple Image"))
        }
        Button(
            onClick = {
                val uri = templeUri ?: return@Button
                uploadingTemple = true
                repository.uploadSettingsImage(uri, "temple_home") { result ->
                    uploadingTemple = false
                    result.onSuccess { url -> templeImageUrl = url; templeUri = null; toast(tr(language, "රූපය upload වුණා. දැන් Save කරන්න.", "Image uploaded. Now press Save.")) }
                        .onFailure { toast(it.message ?: "Upload failed") }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = templeUri != null && !uploadingTemple
        ) { Text(if (uploadingTemple) tr(language, "Upload වෙමින්...", "Uploading...") else tr(language, "විහාර රූපය Upload කරන්න", "Upload Temple Image")) }
    }

    Spacer(Modifier.height(14.dp))
    AdminCard(tr(language, "ස්වාමීන් වහන්සේ", "Temple Monk"), Icons.Default.Person) {
        if (monkUri != null) {
            AsyncImage(model = monkUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(16.dp)))
        } else if (monkImageUrl.isNotBlank()) {
            AsyncImage(model = monkImageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(16.dp)))
        }
        OutlinedButton(onClick = { monkPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Icon(Icons.Default.AddPhotoAlternate, null)
            Spacer(Modifier.width(8.dp))
            Text(tr(language, "ස්වාමීන් වහන්සේගේ රූපය තෝරන්න", "Choose Monk Photo"))
        }
        Button(
            onClick = {
                val uri = monkUri ?: return@Button
                uploadingMonk = true
                repository.uploadSettingsImage(uri, "monk") { result ->
                    uploadingMonk = false
                    result.onSuccess { url -> monkImageUrl = url; monkUri = null; toast(tr(language, "රූපය upload වුණා. දැන් Save කරන්න.", "Image uploaded. Now press Save.")) }
                        .onFailure { toast(it.message ?: "Upload failed") }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = monkUri != null && !uploadingMonk
        ) { Text(if (uploadingMonk) tr(language, "Upload වෙමින්...", "Uploading...") else tr(language, "ස්වාමීන් වහන්සේගේ රූපය Upload කරන්න", "Upload Monk Photo")) }
        AdminField(monkNameSi, { monkNameSi = it }, "ස්වාමීන් වහන්සේගේ නම - සිංහල")
        AdminField(monkNameEn, { monkNameEn = it }, "Monk name - English")
        AdminField(monkTitleSi, { monkTitleSi = it }, "තනතුර - සිංහල")
        AdminField(monkTitleEn, { monkTitleEn = it }, "Title - English")
    }

    Spacer(Modifier.height(14.dp))
    AdminCard("Facebook", Icons.Default.Public) {
        AdminField(facebookUrl, { facebookUrl = it }, "Facebook Page URL")
    }

    Spacer(Modifier.height(14.dp))
    AdminCard(tr(language, "දායක සභා සම්බන්ධතා", "Committee Contacts"), Icons.Default.Phone) {
        Text(tr(language, "සභාපති", "President"), fontWeight = FontWeight.Bold, color = TempleDeepGold)
        AdminField(sabhapathiNameSi, { sabhapathiNameSi = it }, "සභාපති නම - සිංහල")
        AdminField(sabhapathiNameEn, { sabhapathiNameEn = it }, "President name - English")
        AdminField(sabhapathiPhone, { sabhapathiPhone = it }, "President phone")

        Text(tr(language, "භාණ්ඩාගාරික", "Treasurer"), fontWeight = FontWeight.Bold, color = TempleDeepGold, modifier = Modifier.padding(top = 8.dp))
        AdminField(bandagarikaNameSi, { bandagarikaNameSi = it }, "භාණ්ඩාගාරික නම - සිංහල")
        AdminField(bandagarikaNameEn, { bandagarikaNameEn = it }, "Treasurer name - English")
        AdminField(bandagarikaPhone, { bandagarikaPhone = it }, "Treasurer phone")

        Text(tr(language, "ලේකම්", "Secretary"), fontWeight = FontWeight.Bold, color = TempleDeepGold, modifier = Modifier.padding(top = 8.dp))
        AdminField(lekamNameSi, { lekamNameSi = it }, "ලේකම් නම - සිංහල")
        AdminField(lekamNameEn, { lekamNameEn = it }, "Secretary name - English")
        AdminField(lekamPhone, { lekamPhone = it }, "Secretary phone")
    }

    Spacer(Modifier.height(14.dp))
    Button(
        onClick = {
            repository.saveSettings(
                TempleSettings(
                    templeImageUrl = templeImageUrl,
                    monkImageUrl = monkImageUrl,
                    monkNameSi = monkNameSi,
                    monkNameEn = monkNameEn,
                    monkTitleSi = monkTitleSi,
                    monkTitleEn = monkTitleEn,
                    facebookUrl = facebookUrl,
                    sabhapathiNameSi = sabhapathiNameSi,
                    sabhapathiNameEn = sabhapathiNameEn,
                    sabhapathiPhone = sabhapathiPhone,
                    bandagarikaNameSi = bandagarikaNameSi,
                    bandagarikaNameEn = bandagarikaNameEn,
                    bandagarikaPhone = bandagarikaPhone,
                    lekamNameSi = lekamNameSi,
                    lekamNameEn = lekamNameEn,
                    lekamPhone = lekamPhone
                )
            ) { result ->
                if (result.isSuccess) toast(tr(language, "විහාර තොරතුරු Save කළා", "Temple information saved"))
                else toast(result.exceptionOrNull()?.message ?: "Save failed")
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) { Text(tr(language, "සියලු තොරතුරු Save කරන්න", "Save All Temple Information")) }
}

@Composable
private fun AdminCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = TempleGold)
                Spacer(Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun AdminField(value: String, onChange: (String) -> Unit, label: String, singleLine: Boolean = true) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3
    )
}

@Composable
private fun AdminListTitle(title: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TempleDeepGold, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun AdminListRow(title: String, subtitle: String, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 5.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                if (subtitle.isNotBlank()) Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
        }
    }
}
