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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QazaAutoCalculationScreen(
    onBackClick: () -> Unit,
    onSave: (Int, Int, Int, Int, Int, Int) -> Unit
) {
    val context = LocalContext.current
    val dataRepository = remember { com.hasanzade.namazshia.data.QazaDataRepository(context) }

    val savedData = remember { dataRepository.getAutoCalculationData() }
    var selectedGender by remember { mutableStateOf(savedData.first) }
    var birthDate by remember { mutableStateOf(
        try {
            savedData.second?.let { LocalDate.parse(it) }
        } catch (e: Exception) {
            null
        }
    ) }
    var prayerStartDate by remember { mutableStateOf(
        try {
            savedData.third?.let { LocalDate.parse(it) }
        } catch (e: Exception) {
            null
        }
    ) }
    var showBirthDatePicker by remember { mutableStateOf(false) }
    var showPrayerDatePicker by remember { mutableStateOf(false) }
    var calculatedResults by remember { mutableStateOf<Map<String, Int>?>(null) }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun calculateQazaPrayers() {
        val startDate = prayerStartDate
        val today = LocalDate.now()

        when {
            startDate == null -> {
                errorMessage = "Please select a prayer start date"
                showErrorDialog = true
                return
            }
            startDate.isAfter(today) -> {
                errorMessage = "Prayer start date cannot be in the future"
                showErrorDialog = true
                return
            }
            startDate.isEqual(today) -> {
                errorMessage = "Prayer start date cannot be today"
                showErrorDialog = true
                return
            }
        }

        try {
            val daysDifference = ChronoUnit.DAYS.between(startDate, today)
            val totalPrayers = (daysDifference * 5).toInt()

            val eachPrayerCount = totalPrayers / 5
            val remainingPrayers = totalPrayers % 5

            val fajr = eachPrayerCount + if (remainingPrayers > 0) 1 else 0
            val dhuhr = eachPrayerCount + if (remainingPrayers > 1) 1 else 0
            val asr = eachPrayerCount + if (remainingPrayers > 2) 1 else 0
            val maghrib = eachPrayerCount + if (remainingPrayers > 3) 1 else 0
            val isha = eachPrayerCount + if (remainingPrayers > 4) 1 else 0
            val ramadan = 0

            calculatedResults = mapOf(
                "Fajr" to fajr,
                "Dhuhr" to dhuhr,
                "Asr" to asr,
                "Maghrib" to maghrib,
                "Isha" to isha,
                "Ramadan" to ramadan,
                "Total Days" to daysDifference.toInt(),
                "Total Prayers" to totalPrayers
            )

            println("Debug: Successfully calculated - Days: $daysDifference, Total Prayers: $totalPrayers")
        } catch (e: Exception) {
            errorMessage = "Error calculating prayers: ${e.message}"
            showErrorDialog = true
            println("Debug: Error in calculation: ${e.message}")
        }
    }

    fun calculatePrayerStartDate() {
        val birth = birthDate
        if (birth != null) {
            val ageToAdd = if (selectedGender == "Male") 15 else 9
            val calculatedStart = birth.plusYears(ageToAdd.toLong())
            prayerStartDate = calculatedStart


            dataRepository.saveAutoCalculationData(
                selectedGender,
                birth.toString(),
                calculatedStart.toString()
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Qaza Auto Calculation",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Instructions",
                        color = Color(0xFF4CAF50),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• If you don't know your exact prayer start age, we use:\n• 15 years for men (Hijri calendar)\n• 9 years for women (Hijri calendar)\n• Enter your birth date and we'll calculate when prayers became obligatory for you.",
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Gender",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Male", "Female").forEach { gender ->
                    FilterChip(
                        selected = selectedGender == gender,
                        onClick = {
                            selectedGender = gender
                            dataRepository.saveAutoCalculationData(
                                gender,
                                birthDate?.toString(),
                                prayerStartDate?.toString()
                            )
                            calculatePrayerStartDate()
                        },
                        label = { Text(gender) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF4CAF50),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF2D2D2D),
                            labelColor = Color.Gray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            DatePickerField(
                label = "Birth Date",
                selectedDate = birthDate,
                onDateClick = { showBirthDatePicker = true },
                onDateChange = { date ->
                    birthDate = date
                    calculatePrayerStartDate()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DatePickerField(
                label = "Prayer Start Date",
                selectedDate = prayerStartDate,
                onDateClick = { showPrayerDatePicker = true },
                onDateChange = { prayerStartDate = it }
            )

            if (birthDate != null && prayerStartDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Prayer start date calculated based on ${if (selectedGender == "Male") "15" else "9"} years from birth",
                    color = Color(0xFF4CAF50),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    calculateQazaPrayers()
                    println("Debug: Birth date: $birthDate")
                    println("Debug: Prayer start date: $prayerStartDate")
                    println("Debug: Calculated results: $calculatedResults")
                },
                enabled = prayerStartDate != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color.Gray
                )
            ) {
                Icon(Icons.Default.Calculate, "Calculate")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calculate Qaza Prayers", fontSize = 16.sp)
            }


            calculatedResults?.let { results ->
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Calculation Results",
                            color = Color(0xFF4CAF50),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Days:", color = Color.White, fontSize = 14.sp)
                            Text("${results["Total Days"]}", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Prayers:", color = Color.White, fontSize = 14.sp)
                            Text("${results["Total Prayers"]}", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))

                        listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha").forEach { prayer ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("$prayer:", color = Color.White, fontSize = 14.sp)
                                Text("${results[prayer]}", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val results = calculatedResults!!
                                onSave(
                                    results["Fajr"] ?: 0,
                                    results["Dhuhr"] ?: 0,
                                    results["Asr"] ?: 0,
                                    results["Maghrib"] ?: 0,
                                    results["Isha"] ?: 0,
                                    results["Ramadan"] ?: 0
                                )
                                onBackClick()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(Icons.Default.Save, "Save")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save & Go Back to Tracker")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showBirthDatePicker) {
        SimpleDatePickerDialog(
            onDateSelected = { date ->
                birthDate = date
                calculatePrayerStartDate()
                showBirthDatePicker = false
            },
            onDismiss = { showBirthDatePicker = false }
        )
    }

    if (showPrayerDatePicker) {
        SimpleDatePickerDialog(
            onDateSelected = { date ->
                prayerStartDate = date
                dataRepository.saveAutoCalculationData(
                    selectedGender,
                    birthDate?.toString(),
                    date.toString()
                )
                showPrayerDatePicker = false
            },
            onDismiss = { showPrayerDatePicker = false }
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Calculation Error", color = Color.White) },
            text = { Text(errorMessage, color = Color.White) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK", color = Color(0xFF4CAF50))
                }
            },
            containerColor = Color(0xFF2D2D2D)
        )
    }
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateClick: () -> Unit,
    onDateChange: (LocalDate) -> Unit
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDateClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedDate?.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault()))
                        ?: "Select $label",
                    color = if (selectedDate != null) Color.White else Color.Gray,
                    fontSize = 16.sp
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->

                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text("OK", color = Color(0xFF4CAF50))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color(0xFF2D2D2D),
                titleContentColor = Color.White,
                headlineContentColor = Color.White,
                weekdayContentColor = Color.White,
                subheadContentColor = Color.White,
                yearContentColor = Color.White,
                currentYearContentColor = Color(0xFF4CAF50),
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = Color(0xFF4CAF50),
                dayContentColor = Color.White,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = Color(0xFF4CAF50),
                todayContentColor = Color(0xFF4CAF50),
                todayDateBorderColor = Color(0xFF4CAF50)
            )
        )
    }
}