package com.hasanzade.namazshia

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hasanzade.namazshia.data.QazaDataRepository

data class QazaPrayer(
    val name: String,
    val icon: ImageVector,
    val remaining: Int = 0
)

@Composable
fun QazaTrackerScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val dataRepository = remember { QazaDataRepository(context) }

    var selectedTab by remember { mutableStateOf(0) }
    var showAutoCalculation by remember { mutableStateOf(false) }
    var showCountDialog by remember { mutableStateOf<String?>(null) }

    // Load saved data on startup
    var qazaPrayers by remember {
        mutableStateOf(
            listOf(
                QazaPrayer("Fajr", Icons.Default.WbTwilight, dataRepository.getQazaPrayerCount("Fajr")),
                QazaPrayer("Dhuhr", Icons.Default.LightMode, dataRepository.getQazaPrayerCount("Dhuhr")),
                QazaPrayer("Asr", Icons.Default.Brightness6, dataRepository.getQazaPrayerCount("Asr")),
                QazaPrayer("Maghrib", Icons.Default.Brightness3, dataRepository.getQazaPrayerCount("Maghrib")),
                QazaPrayer("Isha", Icons.Default.NightsStay, dataRepository.getQazaPrayerCount("Isha")),
                QazaPrayer("Ramadan", Icons.Default.CalendarMonth, dataRepository.getQazaPrayerCount("Ramadan"))
            )
        )
    }

    // Load saved safar data
    var safarPrayers by remember {
        mutableStateOf(
            listOf(
                QazaPrayer("Safar Dhuhr", Icons.Default.LightMode, dataRepository.getSafarPrayerCount("Safar Dhuhr")),
                QazaPrayer("Safar Asr", Icons.Default.Brightness6, dataRepository.getSafarPrayerCount("Safar Asr")),
                QazaPrayer("Safar Isha", Icons.Default.NightsStay, dataRepository.getSafarPrayerCount("Safar Isha"))
            )
        )
    }

    fun resetAllData() {
        dataRepository.resetAllQazaData()
        qazaPrayers = qazaPrayers.map { it.copy(remaining = 0) }
        safarPrayers = safarPrayers.map { it.copy(remaining = 0) }
    }

    fun updateQazaCount(prayerName: String, newCount: Int) {
        dataRepository.saveQazaPrayerCount(prayerName, newCount)
        qazaPrayers = qazaPrayers.map {
            if (it.name == prayerName) it.copy(remaining = newCount) else it
        }
    }

    fun updateSafarCount(prayerName: String, newCount: Int) {
        dataRepository.saveSafarPrayerCount(prayerName, newCount)
        safarPrayers = safarPrayers.map {
            if (it.name == prayerName) it.copy(remaining = newCount) else it
        }
    }

    fun setAutoCalculatedCounts(
        fajr: Int, dhuhr: Int, asr: Int, maghrib: Int, isha: Int, ramadan: Int
    ) {
        // Save to repository
        dataRepository.saveQazaPrayerCount("Fajr", fajr)
        dataRepository.saveQazaPrayerCount("Dhuhr", dhuhr)
        dataRepository.saveQazaPrayerCount("Asr", asr)
        dataRepository.saveQazaPrayerCount("Maghrib", maghrib)
        dataRepository.saveQazaPrayerCount("Isha", isha)
        dataRepository.saveQazaPrayerCount("Ramadan", ramadan)

        // Update UI
        qazaPrayers = listOf(
            QazaPrayer("Fajr", Icons.Default.WbTwilight, fajr),
            QazaPrayer("Dhuhr", Icons.Default.LightMode, dhuhr),
            QazaPrayer("Asr", Icons.Default.Brightness6, asr),
            QazaPrayer("Maghrib", Icons.Default.Brightness3, maghrib),
            QazaPrayer("Isha", Icons.Default.NightsStay, isha),
            QazaPrayer("Ramadan", Icons.Default.CalendarMonth, ramadan)
        )
    }

    when {
        showAutoCalculation -> {
            QazaAutoCalculationScreen(
                onBackClick = { showAutoCalculation = false },
                onSave = { fajr, dhuhr, asr, maghrib, isha, ramadan ->
                    setAutoCalculatedCounts(fajr, dhuhr, asr, maghrib, isha, ramadan)
                    showAutoCalculation = false
                }
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Top App Bar
                QazaTopAppBar(
                    onBackClick = onBackClick,
                    onResetClick = { resetAllData() },
                    onAutoCalculationClick = { showAutoCalculation = true }
                )

                // Tab Layout
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF2D2D2D),
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF4CAF50)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Qaza",
                                color = if (selectedTab == 0) Color(0xFF4CAF50) else Color.Gray
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Qaza in Safar",
                                color = if (selectedTab == 1) Color(0xFF4CAF50) else Color.Gray
                            )
                        }
                    )
                }

                // Tab Content
                when (selectedTab) {
                    0 -> QazaTab(
                        prayers = qazaPrayers,
                        onPrayerClick = { showCountDialog = it }
                    )
                    1 -> SafarTab(
                        prayers = safarPrayers,
                        onPrayerClick = { showCountDialog = it }
                    )
                }
            }

            // Count Dialog
            showCountDialog?.let { prayerName ->
                val currentPrayer = if (selectedTab == 0) {
                    qazaPrayers.find { it.name == prayerName }
                } else {
                    safarPrayers.find { it.name == prayerName }
                }

                currentPrayer?.let { prayer ->
                    CountDialog(
                        prayerName = prayer.name,
                        currentCount = prayer.remaining,
                        onSave = { newCount ->
                            if (selectedTab == 0) {
                                updateQazaCount(prayerName, newCount)
                            } else {
                                updateSafarCount(prayerName, newCount)
                            }
                            showCountDialog = null
                        },
                        onDismiss = { showCountDialog = null }
                    )
                }
            }
        }
    }
}

@Composable
fun QazaTopAppBar(
    onBackClick: () -> Unit,
    onResetClick: () -> Unit,
    onAutoCalculationClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Qaza Tracker",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(onClick = onResetClick) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset All",
                        tint = Color.White
                    )
                }
                IconButton(onClick = onAutoCalculationClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Auto Calculate",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun QazaTab(
    prayers: List<QazaPrayer>,
    onPrayerClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(prayers) { prayer ->
            QazaPrayerCard(
                prayer = prayer,
                onClick = { onPrayerClick(prayer.name) }
            )
        }
    }
}

@Composable
fun SafarTab(
    prayers: List<QazaPrayer>,
    onPrayerClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(prayers) { prayer ->
            QazaPrayerCard(
                prayer = prayer,
                onClick = { onPrayerClick(prayer.name) }
            )
        }
    }
}

@Composable
fun QazaPrayerCard(
    prayer: QazaPrayer,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = prayer.icon,
                contentDescription = prayer.name,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = prayer.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Remaining",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Text(
                text = prayer.remaining.toString(),
                color = Color(0xFF4CAF50),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CountDialog(
    prayerName: String,
    currentCount: Int,
    onSave: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var count by remember { mutableStateOf(currentCount) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = prayerName,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Count: $count",
                    color = Color(0xFF4CAF50),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Minus Button
                    FloatingActionButton(
                        onClick = { if (count > 0) count-- },
                        containerColor = Color(0xFFE57373),
                        contentColor = Color.White,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.Remove, "Decrease")
                    }

                    // Plus Button
                    FloatingActionButton(
                        onClick = { count++ },
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.Add, "Increase")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onSave(count) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}