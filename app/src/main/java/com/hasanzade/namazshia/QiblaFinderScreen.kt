package com.hasanzade.namazshia

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun QiblaFinderScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    var deviceRotation by remember { mutableStateOf(0f) }
    var qiblaDirection by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        qiblaDirection = calculateQiblaDirection(40.4093, 49.8671)
    }

    DisposableEffect(sensorManager) {
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ORIENTATION) {
                    deviceRotation = event.values[0]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        magnetometer?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
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
                    text = "Qibla Finder",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier.size(280.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "N",
                    color = Color.Red,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(y = (-100).dp)
                        .rotate(-deviceRotation)
                )

                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "Qibla Direction",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier
                        .size(80.dp)
                        .rotate(qiblaDirection - deviceRotation)
                )

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.White, CircleShape)
                )

                Text(
                    text = "E",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .offset(x = 100.dp)
                        .rotate(-deviceRotation)
                )
                Text(
                    text = "S",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .offset(y = 100.dp)
                        .rotate(-deviceRotation)
                )
                Text(
                    text = "W",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .offset(x = (-100).dp)
                        .rotate(-deviceRotation)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "How to Use",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Hold your phone flat\n2. Turn your body until the GREEN ARROW points forward\n3. You are now facing Qibla (Kaaba direction)",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Qibla Direction",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${qiblaDirection.toInt()}° from North",
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "🕋",
                    fontSize = 24.sp
                )
            }
        }
    }
}

fun calculateQiblaDirection(latitude: Double, longitude: Double): Float {
    val kaabaLat = Math.toRadians(21.4225) // Mecca
    val kaabaLng = Math.toRadians(39.8262)

    val lat = Math.toRadians(latitude)
    val lng = Math.toRadians(longitude)

    val deltaLng = kaabaLng - lng

    val y = sin(deltaLng) * cos(kaabaLat)
    val x = cos(lat) * sin(kaabaLat) - sin(lat) * cos(kaabaLat) * cos(deltaLng)

    var bearing = Math.toDegrees(atan2(y, x))
    bearing = (bearing + 360) % 360

    return bearing.toFloat()
}