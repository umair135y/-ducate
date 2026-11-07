package com.educate.ecosystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

val PrimaryDark = Color(0xFF0F172A)
val SurfaceDark = Color(0xFF1E293B)
val BrandIndigo = Color(0xFF6366F1)
val NeonEmerald = Color(0xFF10B981)
val LiveRed = Color(0xFFEF4444)
val AmberGold = Color(0xFFF59E0B)
val SoftIce = Color(0xFFF8FAFC)
val CardBorder = Color(0xFFE2E8F0)
val TextMuted = Color(0xFF64748B)

enum class AppLanguage { 
    ENGLISH, 
    URDU 
}

enum class StudentGrade(
    val enLabel: String,
    val urLabel: String,
    val levelEn: String,
    val levelUr: String
) {
    GRADE_1("Class 1", "Pehli Class (پہلی جماعت)", "Primary Wing", "پرائمری ونگ"),
    GRADE_5("Class 5", "5th Class (پانچویں جماعت)", "Primary Wing", "پرائمری ونگ"),
    GRADE_8("Class 8", "8th Class (آٹھویں جماعت)", "Middle Wing", "مڈل ونگ"),
    GRADE_10("Class 10", "Matric (دسویں جماعت)", "Secondary Wing", "ثانوی ونگ"),
    GRADE_12("Class 12", "FSc / College (بارہویں جماعت)", "College Wing", "کالج ونگ")
}

data class SubjectItem(
    val titleEn: String,
    val titleUr: String,
    val chapters: Int,
    val progress: Float
)

data class VideoItem(
    val titleEn: String,
    val titleUr: String,
    val teacher: String,
    val duration: String,
    val isLive: Boolean
)

suspend fun fetchLiveBroadcast(): Pair<Boolean, String> {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.adviceslip.com/advice")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.requestMethod = "GET"

            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val regex = "\"advice\":\\s*\"(.*?)\"".toRegex()
                val match = regex.find(response)
                val text = match?.groupValues?.get(1) ?: "Knowledge is power. Keep learning every day!"
                Pair(true, text)
            } else {
                Pair(false, "Server Status: Connected")
            }
        } catch (e: Exception) {
            Pair(false, "انٹرنیٹ منسلک نہیں ہے (Check Connection)")
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                EducateAppController()
            }
        }
    }
}

@Composable
fun EducateAppController() {
    var isSplash by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var appLanguage by remember { mutableStateOf(AppLanguage.URDU) }
    var currentGrade by remember { mutableStateOf(StudentGrade.GRADE_10) }
    var currentTab by remember { mutableIntStateOf(0) }

    var isOnline by remember { mutableStateOf(false) }
    var liveBroadcastText by remember { mutableStateOf("انٹرنیٹ سے منسلک ہو رہا ہے...") }
    var isSyncing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    fun syncWithInternet() {
        coroutineScope.launch {
            isSyncing = true
            val (online, msg) = fetchLiveBroadcast()
            isOnline = online
            liveBroadcastText = msg
            isSyncing = false
        }
    }

    LaunchedEffect(Unit) {
        syncWithInternet()
    }

    BackHandler(enabled = isLoggedIn) {
        if (currentTab != 0) currentTab = 0 else isLoggedIn = false
    }

    if (isSplash) {
        SplashScreen { isSplash = false }
    } else if (!isLoggedIn) {
        OnboardingLoginScreen(
            selectedLang = appLanguage,
            selectedGrade = currentGrade,
            isOnline = isOnline,
            onLangChanged = { appLanguage = it },
            onGradeChanged = { currentGrade = it },
            onEnter = { isLoggedIn = true }
        )
    } else {
        Scaffold(
            bottomBar = {
                BottomNav(
                    currentTab = currentTab,
                    lang = appLanguage,
                    onSelect = { currentTab = it }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(SoftIce)
            ) {
                when (currentTab) {
                    0 -> DashboardView(
                        grade = currentGrade,
                        lang = appLanguage,
                        isOnline = isOnline,
                        liveBroadcast = liveBroadcastText,
                        isSyncing = isSyncing,
                        onRefreshLive = { syncWithInternet() },
                        onToggleLang = {
                            appLanguage = if (appLanguage == AppLanguage.URDU) AppLanguage.ENGLISH else AppLanguage.URDU
                        },
                        onLogout = { isLoggedIn = false }
                    )
                    1 -> LecturesView(
                        grade = currentGrade,
                        lang = appLanguage,
                        isOnline = isOnline
                    )
                    2 -> QuizView(
                        grade = currentGrade,
                        lang = appLanguage
                    )
                    3 -> ProfileView(
                        grade = currentGrade,
                        lang = appLanguage,
                        isOnline = isOnline
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onDone()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PrimaryDark, Color(0xFF1E1B4B)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(22.dp),
                color = BrandIndigo,
                shadowElevation = 10.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text("EDUCATE PRO", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
            Text("Live Cloud Connected Ecosystem", color = NeonEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(30.dp))
            CircularProgressIndicator(color = NeonEmerald, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Architect: Muhammad Umair Sattar", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        }
    }
}

@Composable
fun OnboardingLoginScreen(
    selectedLang: AppLanguage,
    selectedGrade: StudentGrade,
    isOnline: Boolean,
    onLangChanged: (AppLanguage) -> Unit,
    onGradeChanged: (StudentGrade) -> Unit,
    onEnter: () -> Unit
) {
    val isUrdu = selectedLang == AppLanguage.URDU

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftIce)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isOnline) NeonEmerald.copy(alpha = 0.15f) else LiveRed.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = CircleShape, color = if (isOnline) NeonEmerald else LiveRed) {
                        Box(modifier = Modifier.size(8.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (isOnline) "Cloud Server: Online (منسلک)" else "Cloud Server: Offline",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOnline) NeonEmerald else LiveRed
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(if (isUrdu) "ایجوکیٹ پورٹل میں خوش آمدید" else "Welcome to EDUCATE Portal", fontSize = 22.sp, fontWeight = FontWeight.Black, color = PrimaryDark)
            Text(if (isUrdu) "اپنی زبان اور کلاس منتخب کریں" else "Select your language & target class", fontSize = 13.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(if (isUrdu) "1. زبان کا انتخاب کریں:" else "1. Choose Language:", fontWeight = FontWeight.Bold, color = PrimaryDark, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        LanguageOptionCard("اردو (Urdu)", selectedLang == AppLanguage.URDU, Modifier.weight(1f)) { onLangChanged(AppLanguage.URDU) }
                        LanguageOptionCard("English", selectedLang == AppLanguage.ENGLISH, Modifier.weight(1f)) { onLangChanged(AppLanguage.ENGLISH) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(if (isUrdu) "2. اپنی کلاس منتخب کریں:" else "2. Select Your Enrolled Class:", fontWeight = FontWeight.Bold, color = PrimaryDark, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    StudentGrade.values().forEach { grade ->
                        val isSelected = grade == selectedGrade
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onGradeChanged(grade) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) BrandIndigo else SoftIce
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    if (isUrdu) grade.urLabel else grade.enLabel,
                                    color = if (isSelected) Color.White else PrimaryDark,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    if (isUrdu) grade.levelUr else grade.levelEn,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Button(
                onClick = onEnter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo)
            ) {
                Text(if (isUrdu) "پورٹل میں داخل ہوں" else "Enter Smart Campus", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Architected by Muhammad Umair Sattar © 2026", fontSize = 11.sp, color = TextMuted)
        }
    }
}

@Composable
fun DashboardView(
    grade: StudentGrade,
    lang: AppLanguage,
    isOnline: Boolean,
    liveBroadcast: String,
    isSyncing: Boolean,
    onRefreshLive: () -> Unit,
    onToggleLang: () -> Unit,
    onLogout: () -> Unit
) {
    val isUrdu = lang == AppLanguage.URDU

    val subjects = remember(grade) {
        when (grade) {
            StudentGrade.GRADE_1 -> listOf(
                SubjectItem("Basic Mathematics", "بنیادی ریاضی و گنتی", 8, 0.85f),
                SubjectItem("English Phonics & Rhymes", "انگریزی حروف اور نظمیں", 10, 0.70f),
                SubjectItem("Urdu Qaida", "اردو قاعدہ و الفاظ", 6, 0.90f)
            )
            StudentGrade.GRADE_5 -> listOf(
                SubjectItem("General Science", "جنرل سائنس اور تجربات", 12, 0.65f),
                SubjectItem("Mathematics & Fractions", "ریاضی، کسر اور جیومیٹری", 14, 0.55f),
                SubjectItem("Urdu Literature", "اردو ادب و قواعد", 10, 0.80f)
            )
            StudentGrade.GRADE_8 -> listOf(
                SubjectItem("General Science & Chemistry", "سائنس اور کیمیاوی عوامل", 16, 0.50f),
                SubjectItem("Algebra & Geometry", "الجبرہ اور اقلیدس", 18, 0.75f),
                SubjectItem("Computer Studies", "کمپیوٹر اور کوڈنگ کی بنیاد", 12, 0.85f)
            )
            StudentGrade.GRADE_10 -> listOf(
                SubjectItem("Physics: Mechanics & Waves", "طبیعیات: میکانیات اور لہریں", 22, 0.82f),
                SubjectItem("Chemistry: Organic Compounds", "کیمسٹری: نامیاتی مرکبات", 20, 0.68f),
                SubjectItem("Biology: Genetics & DNA", "حیاتیات: جینیات اور خلیات", 18, 0.74f),
                SubjectItem("Mathematics: Advanced Calculus", "اعلیٰ ریاضی اور فارمولے", 24, 0.60f)
            )
            StudentGrade.GRADE_12 -> listOf(
                SubjectItem("Physics: Quantum & Modern", "جدید طبیعیات اور ایٹمی ماڈل", 26, 0.72f),
                SubjectItem("Higher Mathematics & Vectors", "ویکٹرز اور انٹیگریشن", 28, 0.64f),
                SubjectItem("Computer Architecture & OOP", "او او پی اور الگورتھمز", 22, 0.88f)
            )
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(PrimaryDark, SurfaceDark)))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = CircleShape, color = if (isOnline) NeonEmerald else LiveRed) {
                                    Box(modifier = Modifier.size(8.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isOnline) "LIVE SYNCED" else "OFFLINE", color = if (isOnline) NeonEmerald else LiveRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Muhammad Umair", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                        Row {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandIndigo,
                                modifier = Modifier.clickable { onToggleLang() }
                            ) {
                                Text(
                                    if (isUrdu) "English" else "اردو",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(onClick = onLogout) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(if (isUrdu) "فعال کلاس" else "ACTIVE ENROLLMENT", color = AmberGold, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Text(if (isUrdu) grade.urLabel else grade.enLabel, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(if (isUrdu) grade.levelUr else grade.levelEn, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            }
                            Surface(shape = CircleShape, color = NeonEmerald.copy(alpha = 0.2f)) {
                                Text("GPA 3.96", color = NeonEmerald, modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Black, fontSize = 12.sp)
    
