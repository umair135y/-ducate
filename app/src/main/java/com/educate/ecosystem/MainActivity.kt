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

// برانڈ کے رنگ
val PrimaryDark = Color(0xFF0F172A)
val SurfaceDark = Color(0xFF1E293B)
val BrandIndigo = Color(0xFF6366F1)
val NeonEmerald = Color(0xFF10B981)
val LiveRed = Color(0xFFEF4444)
val AmberGold = Color(0xFFF59E0B)
val SoftIce = Color(0xFFF8FAFC)
val CardBorder = Color(0xFFE2E8F0)
val TextMuted = Color(0xFF64748B)

enum class AppLanguage { ENGLISH, URDU }

enum class StudentGrade(val enLabel: String, val urLabel: String, val l
