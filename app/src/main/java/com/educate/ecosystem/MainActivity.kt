package com.educate.ecosystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val RoyalBlue = Color(0xFF2457D6)
val MidnightNavy = Color(0xFF14213D)
val AccentTeal = Color(0xFF19B89A)
val BackgroundIce = Color(0xFFF5F7FC)
val TextMuted = Color(0xFF6B7280)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                EducateAppMaster()
            }
        }
    }
}

@Composable
fun EducateAppMaster() {
    var currentScreen by remember { mutableStateOf("SPLASH") }
    var selectedRole by remember { mutableStateOf("STUDENT") }

    BackHandler(enabled = currentScreen != "LOGIN") {
        when (currentScreen) {
            "ABOUT" -> currentScreen = "DASHBOARD"
            "DASHBOARD" -> currentScreen = "LOGIN"
        }
    }

    when (currentScreen) {
        "SPLASH" -> SplashScreenUI(onFinish = { currentScreen = "LOGIN" })
        "LOGIN" -> LoginScreenUI(
            onLogin = { role ->
                selectedRole = role
                currentScreen = "DASHBOARD"
            },
            onAbout = { currentScreen = "ABOUT" }
        )
        "DASHBOARD" -> UnifiedDashboardUI(
            role = selectedRole,
            onLogout = { currentScreen = "LOGIN" },
            onAbout = { currentScreen = "ABOUT" }
        )
        "ABOUT" -> AboutScreenUI(onBack = { currentScreen = "DASHBOARD" })
    }
}

@Composable
fun SplashScreenUI(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(90.dp),
                shape = CircleShape,
                color = AccentTeal
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.School, 
                        contentDescription = null, 
                        tint = Color.White, 
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "EDUCATE",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
            Text(
                text = "Digital Education Ecosystem",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            EducateAttributionWatermark()
        }
    }
}

@Composable
fun LoginScreenUI(onLogin: (String) -> Unit, onAbout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundIce)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.School, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(60.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Welcome to EDUCATE", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MidnightNavy)
            Text("Select your portal to test instantly", color = TextMuted, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Select Portal Role:", fontWeight = FontWeight.Bold, color = MidnightNavy)
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onLogin("STUDENT") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                    ) {
                        Text("Student Dashboard (طالب علم پورٹل)")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onLogin("TEACHER") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
                    ) {
                        Text("Teacher Dashboard (اساتذہ پورٹل)")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onLogin("ADMIN") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MidnightNavy)
                    ) {
                        Text("School Admin (اسکول ایڈمن)")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = onAbout) {
                Text("About Developer & App Info", color = RoyalBlue, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
            EducateAttributionWatermark()
        }
    }
}

@Composable
fun UnifiedDashboardUI(role: String, onLogout: () -> Unit, onAbout: () -> Unit) {
    val presentCount by remember { mutableIntStateOf(24) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundIce)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MidnightNavy)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "$role PORTAL", color = AccentTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Hello, Muhammad Umair", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Active Academic Session 2026", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                    Row {
                        IconButton(onClick = onAbout) {
                            Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.White)
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Attendance", fontSize = 11.sp, color = TextMuted)
                        Text("$presentCount / 25 Days", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("GPA / Grade", fontSize = 11.sp, color = TextMuted)
                        Text("3.94 (A+)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AccentTeal)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Fee Status", fontSize = 11.sp, color = TextMuted)
                        Text("Clear", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalBlue)
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Registered Courses & Modules", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MidnightNavy)
                Spacer(modifier = Modifier.height(8.dp))

                val courses = listOf(
                    "CS101: Advanced Algorithmic Thinking",
                    "BIO201: Molecular Genetics & CRISPR",
                    "PHY301: Modern Quantum Mechanics"
                )

                courses.forEach { course ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = RoyalBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(course, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
            EducateAttributionWatermark()
        }
    }
}

@Composable
fun AboutScreenUI(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundIce)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MidnightNavy)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("About EDUCATE", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(modifier = Modifier.size(70.dp), shape = CircleShape, color = RoyalBlue) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(38.dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("EDUCATE ECOSYSTEM", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MidnightNavy)
            Text("Version 1.0.0 (Native Android Production)", fontSize = 12.sp, color = TextMuted)

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Official Developer Credit", fontWeight = FontWeight.Bold, color = RoyalBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Developed & Designed by Muhammad Umair Sattar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Copyright © 2026 Muhammad Umair Sattar. All Rights Reserved.", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "An enterprise all-in-one educational ecosystem combining LMS, school administration, dynamic dashboards, and academic analytics.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            EducateAttributionWatermark()
        }
    }
}

@Composable
fun EducateAttributionWatermark() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("EDUCATE", fontWeight = FontWeight.Bold, color = RoyalBlue, letterSpacing = 2.sp, fontSize = 12.sp)
        Text("Developed & Designed by Muhammad Umair Sattar", fontSize = 11.sp, color = TextMuted)
        Text("Copyright © 2026 Muhammad Umair Sattar. All Rights Reserved.", fontSize = 9.sp, color = Color.Gray)
    }
}
