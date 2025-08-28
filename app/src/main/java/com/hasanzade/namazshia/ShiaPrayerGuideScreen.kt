package com.hasanzade.namazshia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShiaPrayerText(
    val arabic: String,
    val pronunciation: String,
    val meaning: String
)

data class ShiaPrayerStep(
    val step: String,
    val title: String,
    val description: String,
    val text: ShiaPrayerText? = null,
    val note: String? = null
)

@Composable
fun ShiaPrayerGuideScreen(
    prayerName: String,
    rakahCount: Int,
    onBackClick: () -> Unit
) {
    val steps = getShiaPrayerSteps(prayerName, rakahCount)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
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
                Column {
                    Text(
                        text = "$prayerName Prayer",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$rakahCount Rakah Prayer",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Steps
        steps.forEachIndexed { index, step ->
            ShiaPrayerStepCard(
                step = step,
                stepNumber = index + 1
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Bottom spacing
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun ShiaPrayerStepCard(
    step: ShiaPrayerStep,
    stepNumber: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Step header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF4CAF50),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = stepNumber.toString(),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = step.step,
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = step.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = step.description,
                color = Color.Gray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            // Prayer text if available
            step.text?.let { prayerText ->
                Spacer(modifier = Modifier.height(16.dp))

                // Arabic text
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Arabic:",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prayerText.arabic,
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pronunciation
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Pronunciation:",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prayerText.pronunciation,
                            color = Color(0xFFE0E0E0),
                            fontSize = 14.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // English meaning
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Meaning:",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prayerText.meaning,
                            color = Color(0xFFE0E0E0),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Note if available
            step.note?.let { note ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D47A1).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Note: $note",
                        color = Color(0xFF81C784),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

fun getShiaPrayerSteps(prayerName: String, rakahCount: Int): List<ShiaPrayerStep> {
    val steps = mutableListOf<ShiaPrayerStep>()

    // Takbir al-Ihram
    steps.add(
        ShiaPrayerStep(
            step = "STEP 1",
            title = "Takbir al-Ihram",
            description = "Face the Qibla, raise your hands to ear level and say the opening Takbir:",
            text = ShiaPrayerText(
                arabic = "اللهُ اَكبَر",
                pronunciation = "Allahu Əkbər",
                meaning = "Allah is the Greatest"
            ),
            note = "After this Takbir, fold your hands on your abdomen (right hand over left)."
        )
    )

    // For each Rakah
    for (rakah in 1..rakahCount) {
        // Surah Al-Fatiha
        steps.add(
            ShiaPrayerStep(
                step = "RAKAH $rakah",
                title = "Recite Surah Al-Fatiha",
                description = "Recite the opening chapter of the Quran:",
                text = ShiaPrayerText(
                    arabic = """بِسْمِ اللهِ الرَّحْمَنِ الرَّحِيمِ
الْحَمْدُ للهِ رَبِّ الْعَلَميِنَ
الرًّحْمَنِ الرَّحِيمِ
مَلِكِ يَوْمِ الدِّينِ
إيَّاكَ نَعْبُدُ وَإيَّاكَ نَسْتَعِينُ
اهْدِنَا الصِّرَطَ الْمُسْتَقِيمَ
صِرَطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وِلا الضَّالِّينَ""",
                    pronunciation = """Bismillahir-rəhmanir-rəhim
Əlhəmdü lillahi rəbbil aləmin
Ər-rəhmanir-rəhim
Maliki yaumiddin
İyyakə nəbudu və iyyakə nəstəin
İhdinəs-sıratəl müstəqim
Sıratəl-ləzinə ən-əmtə əleyhim ğayril məğzubi əleyhim vələz-zallin""",
                    meaning = """In the name of Allah, the Compassionate and Merciful.
All praise belongs to Allah, Lord of the worlds.
The Compassionate, the Merciful.
Master of the Day of Judgment.
You alone we worship, and You alone we ask for help.
Guide us on the straight path.
The path of those You have blessed, not of those who have incurred wrath, nor of those who have gone astray."""
                ),
                note = "This surah must be recited in every rakah."
            )
        )

        // Additional Surah (for first 2 rakahs)
        if (rakah <= 2) {
            steps.add(
                ShiaPrayerStep(
                    step = "RAKAH $rakah",
                    title = "Recite Additional Surah",
                    description = "After Fatiha, recite a short surah. For example, Surah Al-Ikhlas:",
                    text = ShiaPrayerText(
                        arabic = """بِسْمِ اللهِ الرَّحْمَنِ الرَّحِيمِ
قُلْ هُوَ اللهُ أَحَدٌ
اللهُ الصَّمَدُ
لَمْ يَلِدْ وَلَمْ يُولَدْ
وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ""",
                        pronunciation = """Bismillahir-rəhmanir-rəhim
Qul huvallahu əhəd
Allahus-saməd
Ləm yəlid və ləm yuləd
Və ləm yəkul-ləhu kufuvən əhəd""",
                        meaning = """In the name of Allah, the Compassionate and Merciful.
Say: He is Allah, the One.
Allah is independent of all.
He begets not, nor is He begotten.
And there is none like unto Him."""
                    ),
                    note = "In the first two rakahs, an additional surah is recited after Al-Fatiha."
                )
            )
        }

        // Qunut (in 2nd rakah of Fajr, or last rakah of other prayers)
        if ((prayerName == "Fajr" && rakah == 2) ||
            (prayerName != "Fajr" && rakah == rakahCount && rakah > 2)) {
            steps.add(
                ShiaPrayerStep(
                    step = "RAKAH $rakah",
                    title = "Qunut (Supplication)",
                    description = "Raise your hands and recite Qunut:",
                    text = ShiaPrayerText(
                        arabic = """اَللَّهُمَّ صَلَّ عَلىَ مُحَمَّدِ وَآلِ مُحَمَّد
سُبحَانَ اللهِ وَالحَمدُ للهِ وَلاَ اِلَهَ اِلاَّ اللهُ وَاللهُ اَكبَر""",
                        pronunciation = """Allahummə səlli əla Muhəmmədin və ali Muhəmməd
Subhanallahi vəl-həmdu lillahi və la ilahə illəllahu vallahu əkbər""",
                        meaning = """O Allah, send blessings upon Muhammad and the family of Muhammad.
Glory be to Allah, praise be to Allah, there is no god but Allah, and Allah is the Greatest."""
                    ),
                    note = "Recite the Tasbihati-Arbaa (Four Glorifications) 3 times during Qunut."
                )
            )
        }

        // Ruku
        steps.add(
            ShiaPrayerStep(
                step = "RAKAH $rakah",
                title = "Ruku (Bowing)",
                description = "Say Takbir, bow down placing your hands on your knees and recite:",
                text = ShiaPrayerText(
                    arabic = "سُبحان رَبِّىَ العَظيمِ وَ بِحَمدِهِ",
                    pronunciation = "Subhanə rəbbi-yəl Azımi və bi-həmdih",
                    meaning = "Glory be to my Lord, the Great, and praise be to Him"
                ),
                note = "Recite this at least 3 times. Keep your back straight and parallel to the ground."
            )
        )

        // Rising from Ruku
        steps.add(
            ShiaPrayerStep(
                step = "RAKAH $rakah",
                title = "Rising from Ruku",
                description = "Stand up straight and recite:",
                text = ShiaPrayerText(
                    arabic = "سَمِعَ اللهُ لِمَن حَمِدَهُ",
                    pronunciation = "Sami'allahu li man hamidah",
                    meaning = "Allah hears those who praise Him"
                ),
                note = "Stand completely upright before proceeding to Sujud."
            )
        )

        // Sujud (First)
        steps.add(
            ShiaPrayerStep(
                step = "RAKAH $rakah",
                title = "First Sujud (Prostration)",
                description = "Say Takbir, prostrate with seven points touching the ground and recite:",
                text = ShiaPrayerText(
                    arabic = "سُبحان رَبِّىَ الاَعلى وَ بِحَمدِهِ",
                    pronunciation = "Subhanə rəbbi-yəl Əla və bi-həmdih",
                    meaning = "Glory be to my Lord, the Most High, and praise be to Him"
                ),
                note = "Recite at least 3 times. Forehead, nose, palms, knees, and toes must touch the ground."
            )
        )

        // Sitting between Sujuds
        steps.add(
            ShiaPrayerStep(
                step = "RAKAH $rakah",
                title = "Sitting Between Prostrations",
                description = "Sit up from prostration briefly before the second prostration.",
                note = "Sit calmly for a moment before the second prostration."
            )
        )

        // Sujud (Second)
        steps.add(
            ShiaPrayerStep(
                step = "RAKAH $rakah",
                title = "Second Sujud (Prostration)",
                description = "Say Takbir and prostrate again:",
                text = ShiaPrayerText(
                    arabic = "سُبحان رَبِّىَ الاَعلى وَ بِحَمدِهِ",
                    pronunciation = "Subhanə rəbbi-yəl Əla və bi-həmdih",
                    meaning = "Glory be to my Lord, the Most High, and praise be to Him"
                ),
                note = "Recite at least 3 times. This completes one rakah."
            )
        )

        // Tashahhud (in middle and end)
        if (rakah == 2 && rakahCount > 2) {
            steps.add(
                ShiaPrayerStep(
                    step = "MIDDLE TASHAHHUD",
                    title = "First Tashahhud",
                    description = "Sit and recite the Tashahhud:",
                    text = ShiaPrayerText(
                        arabic = """الحَمدُ لِلّهِ
اَشْهَدُ اَن لا اِلََهَ الاَ اللهُ وَحدَهُ لاشَرِيكَ لَهُ
وَاَشهَدُ اَنَّ مُحَمَّداً عَبْدُهُ وَرَسُولُهُ
اَللَّهُمَّ صَلَّ عَلىَ مُحَمَّدِ وَآلِ مُحَمَّد""",
                        pronunciation = """Əlhəmdu lillah
Əşhədu ənla ilahə illəllah, vəhdəhu la şərikə ləh
Və əşhədu ənnə Muhəmmədən abduhu rəsuluh
Allahummə salli əla Muhəmmədin və ali Muhəmməd""",
                        meaning = """Praise be to Allah.
I bear witness that there is no god but Allah, He is One and has no partner.
And I bear witness that Muhammad is His servant and messenger.
O Allah, send blessings upon Muhammad and the family of Muhammad."""
                    ),
                    note = "Point your right index finger during the testimony. Then stand up for the remaining rakahs."
                )
            )
        }
    }

    // Final Tashahhud and Salawat
    steps.add(
        ShiaPrayerStep(
            step = "FINAL TASHAHHUD",
            title = "Final Tashahhud & Salawat",
            description = "After the last rakah, sit and recite the complete Tashahhud:",
            text = ShiaPrayerText(
                arabic = """الحَمدُ لِلّهِ
اَشْهَدُ اَن لا اِلََهَ الاَ اللهُ وَحدَهُ لاشَرِيكَ لَهُ
وَاَشهَدُ اَنَّ مُحَمَّداً عَبْدُهُ وَرَسُولُهُ
اَللَّهُمَّ صَلَّ عَلىَ مُحَمَّدِ وَآلِ مُحَمَّد""",
                pronunciation = """Əlhəmdu lillah
Əşhədu ənla ilahə illəllah, vəhdəhu la şərikə ləh
Və əşhədu ənnə Muhəmmədən abduhu rəsuluh
Allahummə salli əla Muhəmmədin və ali Muhəmməd""",
                meaning = """Praise be to Allah.
I bear witness that there is no god but Allah, He is One and has no partner.
And I bear witness that Muhammad is His servant and messenger.
O Allah, send blessings upon Muhammad and the family of Muhammad."""
            ),
            note = "This is recited in the final sitting of every prayer."
        )
    )

    // Salams (Greetings)
    steps.add(
        ShiaPrayerStep(
            step = "SALAMS",
            title = "Salams (Greetings)",
            description = "Recite the three Salams:",
            text = ShiaPrayerText(
                arabic = """اَلسَّلاَمُ عَلَيْكَ اَيُّهَا النَّبِىُّ وَرَحمَةُ اللهِ وَبَرَكاتُهُ
السَّلاَمُ عَلَيْنا وَعَلى عِبادِ اللهِ الصالِحِينَ
السَّلاَمُ عَلَيكُم وَرَحمَةُ اللهِ وَبَرَكاتُهُ""",
                pronunciation = """Əssəlamu əleykə əyyuhən-nəbiyyu və rəhmətullahi və bərəkatuh
Əssəlamu əleyna və əla ibadillahis-salihin
Əssəlamu əleykum və rəhmətullahi və bərəkatuh""",
                meaning = """Peace be upon you, O Prophet, and Allah's mercy and blessings.
Peace be upon us and upon Allah's righteous servants.
Peace be upon you and Allah's mercy and blessings."""
            ),
            note = "Turn your head to the right for the last Salam. This completes your prayer."
        )
    )

    return steps
}