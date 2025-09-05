package com.hasanzade.namazshia

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch

data class TasbeehPhase(
    val arabic: String,
    val transliteration: String,
    val meaning: String,
    val targetCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbeehScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val dataRepository = remember { com.hasanzade.namazshia.data.QazaDataRepository(context) }

    val savedState = remember { dataRepository.getTasbeehState() }
    var currentPhase by remember { mutableStateOf(savedState.first) }
    var currentCount by remember { mutableStateOf(savedState.second) }
    var showInfo by remember { mutableStateOf(false) }
    var showCompletion by remember { mutableStateOf(savedState.third) }

    val phases = listOf(
        TasbeehPhase(
            arabic = "سُبْحَانَ اللَّهِ",
            transliteration = "Subhanallah",
            meaning = "Glory be to Allah",
            targetCount = 33
        ),
        TasbeehPhase(
            arabic = "الْحَمْدُ لِلَّهِ",
            transliteration = "Alhamdulillah",
            meaning = "All praise belongs to Allah",
            targetCount = 33
        ),
        TasbeehPhase(
            arabic = "اللَّهُ أَكْبَرُ",
            transliteration = "Allahu Akbar",
            meaning = "Allah is the Greatest",
            targetCount = 34
        )
    )

    var buttonPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.95f else 1f,
        animationSpec = tween(100)
    )

    fun resetTasbeeh() {
        currentPhase = 0
        currentCount = 0
        showCompletion = false
        dataRepository.resetTasbeehData()
    }

    fun incrementCount() {
        buttonPressed = true
        val currentPhaseData = phases[currentPhase]

        if (currentCount < currentPhaseData.targetCount) {
            currentCount++

            if (currentCount == currentPhaseData.targetCount) {
                if (currentPhase < phases.size - 1) {
                    currentPhase++
                    currentCount = 0
                } else {
                    showCompletion = true
                }
            }
        }

        dataRepository.saveTasbeehState(currentPhase, currentCount, showCompletion)

        kotlinx.coroutines.GlobalScope.launch {
            kotlinx.coroutines.delay(100)
            buttonPressed = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF1A1A1A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            resetTasbeeh()
                            onBackClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back and Reset",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tasbeeh of Syeda Fatima (sa)",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = { showInfo = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Information",
                            tint = Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (showCompletion) {
                    CompletionMessage(onReset = { resetTasbeeh() })
                } else {
                    val currentPhaseData = phases[currentPhase]

                    ProgressIndicator(currentPhase = currentPhase, totalPhases = phases.size)

                    Spacer(modifier = Modifier.height(40.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = currentPhaseData.arabic,
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = currentPhaseData.transliteration,
                                color = Color(0xFF4CAF50),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = currentPhaseData.meaning,
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    CounterDisplay(
                        currentCount = currentCount,
                        targetCount = currentPhaseData.targetCount,
                        phaseName = currentPhaseData.transliteration
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    FloatingActionButton(
                        onClick = { incrementCount() },
                        modifier = Modifier
                            .size(80.dp)
                            .scale(buttonScale),
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increment Count",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        if (showInfo) {
            InfoDialog(onDismiss = { showInfo = false })
        }
    }
}

@Composable
fun ProgressIndicator(currentPhase: Int, totalPhases: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPhases) { index ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (index <= currentPhase) Color(0xFF4CAF50) else Color.Gray,
                        shape = CircleShape
                    )
            )

            if (index < totalPhases - 1) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(
                            color = if (index < currentPhase) Color(0xFF4CAF50) else Color.Gray
                        )
                )
            }
        }
    }
}

@Composable
fun CounterDisplay(currentCount: Int, targetCount: Int, phaseName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$currentCount / $targetCount",
                color = Color(0xFF4CAF50),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = currentCount.toFloat() / targetCount.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF4CAF50),
                trackColor = Color.Gray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = phaseName,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CompletionMessage(onReset: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tasbeeh of Syeda Fatima (sa) Completed!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "May Allah accept your dhikr and grant you peace.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF2E7D32)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Again")
            }
        }
    }
}

@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tasbeeh of Syeda Fatima (sa)",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                InfoSection(
                    title = "Historical Background",
                    content = "The Tasbeeh of Syeda Fatima (sa) was taught by the Prophet Muhammad (saw) to his beloved daughter Fatima (sa) as a spiritual treasure more valuable than any worldly possession. When Fatima (sa) requested a servant to help with household duties, the Prophet (saw) instead gave her this blessed dhikr."
                )

                InfoSection(
                    title = "Structure",
                    content = "• Subhanallah - 33 times (Glory be to Allah)\n• Alhamdulillah - 33 times (All praise belongs to Allah)\n• Allahu Akbar - 34 times (Allah is the Greatest)\nTotal: 100 glorifications"
                )

                InfoSection(
                    title = "Spiritual Benefits",
                    content = "• Brings tranquility to the heart\n• Increases gratitude and mindfulness\n• Strengthens connection with Allah\n• Better than worldly possessions\n• Recommended after every obligatory prayer"
                )

                InfoSection(
                    title = "Hadith Reference",
                    content = "Reported in Sahih Muslim and other authentic sources. Imam Ali (as) said: 'Fatima (sa) used to recite this Tasbeeh after every prayer, and it was dearer to her than anything in the world.'"
                )

                InfoSection(
                    title = "How to Practice",
                    content = "• Start with Bismillah\n• Recite each phrase mindfully\n• Count using fingers or beads\n• Focus on the meaning while reciting\n• End with a short dua\n• Practice consistently after prayers"
                )

                InfoSection(
                    title = "Best Times",
                    content = "• After each of the five daily prayers\n• Before sleeping\n• During quiet moments of reflection\n• When feeling stressed or anxious\n• As part of morning and evening remembrance"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Close", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoSection(title: String, content: String) {
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF4CAF50),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = content,
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 18.sp
        )
    }
}