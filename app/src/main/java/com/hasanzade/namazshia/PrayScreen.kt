package com.hasanzade.namazshia

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrayScreen() {
    var selectedPrayer by remember { mutableStateOf<Pair<String, Int>?>(null) }
    var showQiblaFinder by remember { mutableStateOf(false) }
    var showTasbeeh by remember { mutableStateOf(false) }
    var showQazaTracker by remember { mutableStateOf(false) }

    when {
        selectedPrayer != null -> {
            ShiaPrayerGuideScreen(
                prayerName = selectedPrayer!!.first,
                rakahCount = selectedPrayer!!.second,
                onBackClick = { selectedPrayer = null }
            )
        }

        showQiblaFinder -> {
            QiblaFinderScreen(
                onBackClick = { showQiblaFinder = false }
            )
        }

        showTasbeeh -> {
            TasbeehScreen(
                onBackClick = { showTasbeeh = false }
            )
        }

        showQazaTracker -> {
            QazaTrackerScreen(
                onBackClick = { showQazaTracker = false }
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header
                Text(
                    text = "Pray",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Prayer List Section
                PrayerListCard { prayer, rakah ->
                    selectedPrayer = Pair(prayer, rakah)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Extra Menu Section
                ExtraMenuCard(
                    onQiblaFinderClick = { showQiblaFinder = true },
                    onTasbeehClick = { showTasbeeh = true },
                    onQazaTrackerClick = { showQazaTracker = true }
                )

                // Bottom spacing for navigation bar
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}


@Composable
fun PrayerListCard(onPrayerClick: (String, Int) -> Unit) {
    val prayers = listOf(
        PrayerItem("Fajr", "2 rakah", 2),
        PrayerItem("Dhuhr", "4 rakah", 4),
        PrayerItem("Asr", "4 rakah", 4),
        PrayerItem("Maghrib", "3 rakah", 3),
        PrayerItem("Isha", "4 rakah", 4)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            prayers.forEachIndexed { index, prayer ->
                PrayerListItem(
                    title = prayer.name,
                    subtitle = prayer.rakah,
                    onClick = { onPrayerClick(prayer.name, prayer.rakahCount) }
                )

                // Add divider between items except for the last one
                if (index < prayers.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color.Gray.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun PrayerListItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate to $title",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ExtraMenuCard(
    onQiblaFinderClick: () -> Unit = {},
    onTasbeehClick: () -> Unit = {},
    onQazaTrackerClick: () -> Unit = {}
) {
    val menuItems = listOf(
        ExtraMenuItem(
            title = "Qibla Finder",
            subtitle = "Easily find Kaaba direction",
            icon = Icons.Default.Explore,
            onClick = onQiblaFinderClick
        ),
        ExtraMenuItem(
            title = "Tasbih of Lady Fatima (a)",
            subtitle = "Special kind of Dhikr",
            icon = Icons.Default.RadioButtonChecked,
            onClick = onTasbeehClick
        ),
        ExtraMenuItem(
            title = "Qaza Tracker",
            subtitle = "Complete your missed prayers",
            icon = Icons.Default.Schedule,
            onClick = onQazaTrackerClick
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            menuItems.forEachIndexed { index, item ->
                ExtraMenuListItem(
                    title = item.title,
                    subtitle = item.subtitle,
                    icon = item.icon,
                    onClick = item.onClick
                )

                // Add divider between items except for the last one
                if (index < menuItems.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color.Gray.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun ExtraMenuListItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

// Data classes
data class PrayerItem(
    val name: String,
    val rakah: String,
    val rakahCount: Int
)

data class ExtraMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)