package com.catools.templeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.catools.templeapp.data.SampleData
import com.catools.templeapp.data.TempleEvent
import com.catools.templeapp.data.TempleNotice
import com.catools.templeapp.ui.theme.TempleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { TempleTheme { TempleApp() } }
    }
}

data class NavItem(val label: String, val icon: ImageVector)

@Composable
fun TempleApp() {
    val navItems = listOf(
        NavItem("මුල් පිටුව", Icons.Default.Home),
        NavItem("පිංකම්", Icons.Default.Event),
        NavItem("දින දර්ශනය", Icons.Default.CalendarMonth),
        NavItem("දැනුම්දීම්", Icons.Default.Notifications),
        NavItem("තවත්", Icons.Default.Menu)
    )
    var selected by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 9.sp) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (selected) {
                0 -> HomeScreen()
                1 -> EventsScreen()
                2 -> CalendarScreen()
                3 -> NoticesScreen()
                else -> MoreScreen()
            }
        }
    }
}

@Composable
fun ScreenHeader(title: String, subtitle: String? = null) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        subtitle?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun HomeScreen() {
    val nextEvent = SampleData.events.first()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(24.dp)) {
                Column {
                    Text("නමෝ බුද්ධාය", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "කොළලෑගල පුරාණ විහාරස්ථානය",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Kolalagala Ancient Temple", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .85f))
                }
            }
        }
        item { ScreenHeader("ඉදිරි පිංකම", "Upcoming program") }
        item { EventCard(nextEvent, Modifier.padding(horizontal = 16.dp)) }
        item { ScreenHeader("ප්‍රධාන සේවා", "Temple services") }
        item {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickAction("පිංකම්", Icons.Default.Event, Modifier.weight(1f))
                    QuickAction("දැනුම්දීම්", Icons.Default.Notifications, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickAction("ධර්ම දේශනා", Icons.Default.Headphones, Modifier.weight(1f))
                    QuickAction("ඡායාරූප", Icons.Default.PhotoLibrary, Modifier.weight(1f))
                }
            }
        }
        item { ScreenHeader("අලුත්ම දැනුම්දීම") }
        item { NoticeCard(SampleData.notices.first(), Modifier.padding(horizontal = 16.dp)) }
    }
}

@Composable
fun QuickAction(label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun EventsScreen() {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { ScreenHeader("පිංකම් හා වැඩසටහන්", "Events & religious programs") }
        items(SampleData.events) { event -> EventCard(event, Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) }
    }
}

@Composable
fun EventCard(event: TempleEvent, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Spa, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(event.titleSi, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(event.titleEn, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("${event.date}  •  ${event.time}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(event.description)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(event.location, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun CalendarScreen() {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { ScreenHeader("විහාරස්ථාන දින දර්ශනය", "September 2026") }
        item {
            Card(Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("මෙම මාසයේ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    SampleData.events.forEach { event ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
                                Text(event.date.takeLast(2), Modifier.padding(horizontal = 14.dp, vertical = 10.dp), fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(event.titleSi, fontWeight = FontWeight.SemiBold)
                                Text(event.time, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoticesScreen() {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { ScreenHeader("දැනුම්දීම්", "Temple announcements") }
        items(SampleData.notices) { notice -> NoticeCard(notice, Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) }
    }
}

@Composable
fun NoticeCard(notice: TempleNotice, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(notice.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(notice.message)
            Spacer(Modifier.height(10.dp))
            Text(notice.date, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun MoreScreen() {
    val menuItems = listOf(
        Triple("ඡායාරූප ගැලරිය", "Gallery", Icons.Default.PhotoLibrary),
        Triple("ධර්ම දේශනා", "Dhamma", Icons.Default.Headphones),
        Triple("පරිත්‍යාග", "Donations", Icons.Default.VolunteerActivism),
        Triple("දායක සභාව", "Committee", Icons.Default.Groups),
        Triple("විහාරස්ථානය පිළිබඳ", "About", Icons.Default.AccountBalance),
        Triple("සම්බන්ධ වන්න", "Contact & Map", Icons.Default.LocationOn)
    )
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { ScreenHeader("තවත් සේවා", "More temple services") }
        items(menuItems) { item ->
            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(item.third, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.first, fontWeight = FontWeight.Bold)
                        Text(item.second, style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}
