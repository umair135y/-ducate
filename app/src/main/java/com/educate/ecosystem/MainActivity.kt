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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
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
val TextMuted = Color(0xFF64748B)

enum class AppLanguage { ENGLISH, URDU }

enum class StudentGrade(val en: String, val ur: String) {
    G1("Class 1", "پہلی جماعت"),
    G5("Class 5", "پانچویں جماعت"),
    G8("Class 8", "آٹھویں جماعت"),
    G10("Class 10 (Matric)", "دسویں جماعت (میٹرک)"),
    G12("Class 12 (FSc)", "بارہویں جماعت (کالج)")
}

data class Course(val en: String, val ur: String, val progress: Float)

suspend fun fetchLiveQuote(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
    try {
        val conn = URL("https://api.adviceslip.com/advice").openConnection() as HttpURLConnection
        conn.connectTimeout = 4000
        conn.readTimeout = 4000
        if (conn.responseCode == 200) {
            val res = conn.inputStream.bufferedReader().use { it.readText() }
            val text = "\"advice\":\\s*\"(.*?)\"".toRegex().find(res)?.groupValues?.get(1)
            Pair(true, text ?: "Education is the key to unlock the golden door of freedom.")
        } else Pair(false, "Cloud Active")
    } catch (e: Exception) {
        Pair(false, "انٹرنیٹ دستیاب نہیں ہے (Offline Mode)")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                EducateApp()
            }
        }
    }
}

@Composable
fun EducateApp() {
    var loggedIn by remember { mutableStateOf(false) }
    var lang by remember { mutableStateOf(AppLanguage.URDU) }
    var grade by remember { mutableStateOf(StudentGrade.G10) }
    var tab by remember { mutableIntStateOf(0) }
    var online by remember { mutableStateOf(false) }
    var broadcast by remember { mutableStateOf("انٹرنیٹ سے لائیو ڈیٹا حاصل ہو رہا ہے...") }
    val scope = rememberCoroutineScope()

    fun refresh() {
        scope.launch {
            val (ok, msg) = fetchLiveQuote()
            online = ok
            broadcast = msg
        }
    }

    LaunchedEffect(Unit) { refresh() }
    BackHandler(enabled = loggedIn) { if (tab != 0) tab = 0 else loggedIn = false }

    val isUr = lang == AppLanguage.URDU

    if (!loggedIn) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(SoftIce).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(modifier = Modifier.size(70.dp), shape = CircleShape, color = BrandIndigo) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(40.dp)) }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(if (isUr) "ایجوکیٹ پورٹل" else "EDUCATE PRO", fontSize = 24.sp, fontWeight = FontWeight.Black, color = PrimaryDark)
                Text(if (online) "🟢 LIVE CLOUD ONLINE" else "🔴 CLOUD OFFLINE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (online) NeonEmerald else LiveRed)
                Spacer(modifier = Modifier.height(20.dp))

                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(if (isUr) "زبان منتخب کریں (Language):" else "Choose Language:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { lang = AppLanguage.URDU }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (isUr) BrandIndigo else Color.LightGray)) { Text("اردو") }
                            Button(onClick = { lang = AppLanguage.ENGLISH }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (!isUr) BrandIndigo else Color.LightGray)) { Text("English") }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(if (isUr) "اپنی کلاس منتخب کریں:" else "Select Your Class:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        StudentGrade.values().forEach { g ->
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp).clickable { grade = g },
                                shape = RoundedCornerShape(8.dp),
                                color = if (grade == g) BrandIndigo else SoftIce
                            ) {
                                Text(if (isUr) g.ur else g.en, modifier = Modifier.padding(12.dp), color = if (grade == g) Color.White else PrimaryDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { loggedIn = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo)
                ) {
                    Text(if (isUr) "کیمپس میں داخل ہوں ➔" else "Enter Campus ➔", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Architect: Muhammad Umair Sattar © 2026", fontSize = 11.sp, color = TextMuted)
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text(if (isUr) "ہوم" else "Home") })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.PlayCircle, null) }, label = { Text(if (isUr) "لیکچرز" else "Lectures") })
                    NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Star, null) }, label = { Text(if (isUr) "کوئز" else "Quiz") })
                    NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.Person, null) }, label = { Text(if (isUr) "پروفائل" else "Profile") })
                }
            }
        ) { p ->
            Box(modifier = Modifier.fillMaxSize().padding(p).background(SoftIce)) {
                when (tab) {
                    0 -> DashboardTab(grade, isUr, online, broadcast, { refresh() }, { lang = if (isUr) AppLanguage.ENGLISH else AppLanguage.URDU }, { loggedIn = false })
                    1 -> LecturesTab(grade, isUr, online)
                    2 -> QuizTab(isUr)
                    3 -> ProfileTab(isUr, online)
                }
            }
        }
    }
}

@Composable
fun DashboardTab(g: StudentGrade, isUr: Boolean, online: Boolean, quote: String, onRef: () -> Unit, onLang: () -> Unit, onOut: () -> Unit) {
    val courses = remember(g) {
        listOf(
            Course("Physics & Mechanics", "طبیعیات و حرکیات", 0.82f),
            Course("Chemistry & Organic Bonds", "کیمسٹری و نامیاتی کیمیا", 0.68f),
            Course("Advanced Mathematics", "اعلیٰ ریاضی و کلیہ جات", 0.75f),
            Course("English Language & Grammer", "انگریزی گرامر و ادب", 0.90f)
        )
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(PrimaryDark, SurfaceDark))).padding(20.dp)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(if (online) "🟢 LIVE CONNECTED" else "🔴 OFFLINE", color = if (online) NeonEmerald else LiveRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Muhammad Umair", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                        Row {
                            Button(onClick = onLang, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo)) {
                                Text(if (isUr) "English" else "اردو", fontSize = 11.sp)
                            }
                            IconButton(onClick = onOut) { Icon(Icons.Default.ExitToApp, null, tint = Color.White) }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(if (isUr) "فعال کلاس: ${g.ur}" else "Active: ${g.en}", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isUr) "🌐 لائیو کلاؤڈ تعلیمی نوٹس:" else "🌐 Live Cloud Feed:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryDark)
                        Icon(Icons.Default.Refresh, null, tint = BrandIndigo, modifier = Modifier.size(18.dp).clickable { onRef() })
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(quote, fontSize = 12.sp, color = Color.DarkGray)
                }
            }
        }
        item {
            Text(if (isUr) "کورسز اور سلیبس:" else "Registered Modules:", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(6.dp))
        }
        items(courses) { c ->
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, null, tint = BrandIndigo, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(if (isUr) c.ur else c.en, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text("${(c.progress * 100).toInt()}%", fontWeight = FontWeight.Black, color = BrandIndigo, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(progress = c.progress, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)), color = BrandIndigo, trackColor = SoftIce)
                }
            }
        }
    }
}

@Composable
fun LecturesTab(g: StudentGrade, isUr: Boolean, online: Boolean) {
    val items = listOf("Fundamental Laws & Equations", "Exercise Numerical Analysis", "Board Examination Key Notes")
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(if (isUr) "ویڈیو لیکچرز: ${g.ur}" else "Video Lectures: ${g.en}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(if (online) "🟢 Streaming CDN Ready" else "🔴 Offline Cached", fontSize = 11.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(10.dp))
        }
        items(items) { title ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayArrow, null, tint = LiveRed, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("35:00 min • Full HD", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
fun QuizTab(isUr: Boolean) {
    var pick by remember { mutableIntStateOf(-1) }
    var done by remember { mutableStateOf(false) }
    val opts = if (isUr) listOf("الف) توانائی کی بقاء کا قانون", "ب) قانون برودت", "ج) پاسکل کا کلیہ")
    else listOf("A) Law of Conservation of Energy", "B) Law of Cooling", "C) Pascal's Principle")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PrimaryDark)) {
            Text(if (isUr) "سوال: کائنات کے بند نظام میں توانائی کی کل مقدار کیسی رہتی ہے؟" else "Question: In an isolated closed system, total energy is?", color = Color.White, modifier = Modifier.padding(14.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        opts.forEachIndexed { i, txt ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp).clickable(enabled = !done) { pick = i },
                colors = CardDefaults.cardColors(containerColor = if (done && i == 0) NeonEmerald else if (pick == i) BrandIndigo else Color.White)
            ) {
                Text(txt, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (pick == i || (done && i == 0)) Color.White else PrimaryDark)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Button(onClick = { if (!done && pick != -1) done = true else if (done) { done = false; pick = -1 } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = BrandIndigo)) {
            Text(if (done) "Next ➔" else "Submit Answer")
        }
    }
}

@Composable
fun ProfileTab(isUr: Boolean, online: Boolean) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(70.dp), shape = CircleShape, color = BrandIndigo) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(40.dp)) }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("Muhammad Umair Sattar", fontWeight = FontWeight.Black, fontSize = 18.sp, color = PrimaryDark)
        Text(if (isUr) "چیف سوفٹ ویئر آرکیٹیکٹ" else "Chief Software Architect", color = NeonEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(if (online) "🟢 Cloud Connected" else "🔴 Offline Cache Active", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (online) NeonEmerald else LiveRed)
                Spacer(modifier = Modifier.height(6.dp))
                Text(if (isUr) "ایجوکیٹ پرو: کلاس 1 تا 12، مکمل انٹرنیٹ لائیو ڈیٹا اور دو زبانی نظام پر مشتمل ہے۔" else "EDUCATE PRO: Multi-Grade (1-12), Live Cloud Synced and Bilingual Architecture.", fontSize = 11.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Copyright © 2026 Muhammad Umair Sattar. All Rights Reserved.", fontSize = 9.sp, color = TextMuted)
            }
        }
    }
}
