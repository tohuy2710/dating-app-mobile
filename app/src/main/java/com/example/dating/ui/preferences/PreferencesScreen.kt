package com.example.dating.ui.preferences

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dating.data.local.DatingDatabase
import com.example.dating.data.local.UserPreferencesEntity
import com.example.dating.ui.theme.Black900
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import kotlinx.coroutines.launch

@Composable
fun PreferencesScreen(
    userId: Int = 0,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dao = remember(context) { DatingDatabase.getDatabase(context).preferencesDao() }
    val scope = rememberCoroutineScope()

    val interests = remember { mutableStateListOf<String>() }
    var interestInput by remember { mutableStateOf("") }
    var targetGender by remember { mutableStateOf<String?>(null) }
    var minAge by remember { mutableStateOf("") }
    var maxAge by remember { mutableStateOf("") }
    var maxDistance by remember { mutableStateOf("") }

    fun addInterest() {
        val value = interestInput.trim()
        if (value.isEmpty()) return
        if (interests.any { it.equals(value, ignoreCase = true) }) return
        interests.add(value)
        interestInput = ""
    }

    fun removeInterest(value: String) {
        interests.remove(value)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Sở thích",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sở thích",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = interestInput,
                onValueChange = { interestInput = it },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                placeholder = { Text("Thêm sở thích") },
                singleLine = true
            )

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { addInterest() },
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(BrandPinkDark, BrandPink)))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Thêm", color = Color.White)
                }
            }
        }

        if (interests.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                interests.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item)
                            Button(
                                onClick = { removeInterest(item) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(text = "Xóa")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Giới tính",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GenderChoiceButton(
                text = "Nam",
                selected = targetGender == "Nam",
                onClick = { targetGender = "Nam" },
                modifier = Modifier.weight(1f)
            )

            GenderChoiceButton(
                text = "Nữ",
                selected = targetGender == "Nữ",
                onClick = { targetGender = "Nữ" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = minAge,
            onValueChange = { newValue -> minAge = newValue.filter { it.isDigit() }.take(3) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Độ tuổi tối thiểu") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = maxAge,
            onValueChange = { newValue -> maxAge = newValue.filter { it.isDigit() }.take(3) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Độ tuổi tối đa") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = maxDistance,
            onValueChange = { newValue -> maxDistance = newValue.filter { it.isDigit() }.take(4) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Khoảng cách tối đa (km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                scope.launch {
//                    val entity = UserPreferencesEntity(
//                        userId = userId,
//                        targetGender = targetGender,
//                        minAge = minAge.toIntOrNull(),
//                        maxAge = maxAge.toIntOrNull(),
//                        maxDistanceKm = maxDistance.toIntOrNull(),
//                        anonymousInterests = if (interests.isEmpty()) null else interests.joinToString(", ")
//                    )
//                    dao.insertPreferences(entity)
//                    Toast.makeText(context, "Lưu preferences thành công", Toast.LENGTH_SHORT).show()
//                    onComplete()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(34.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(34.dp))
                    .background(Brush.horizontalGradient(listOf(BrandPinkDark, BrandPink))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hoàn tất",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun GenderChoiceButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier.height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(BrandPinkDark, BrandPink))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = text, color = Color.White)
            }
        }
    } else {
        val background = MaterialTheme.colorScheme.surfaceVariant
        val contentColor = MaterialTheme.colorScheme.onSurface

        Button(
            onClick = onClick,
            modifier = modifier.height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = background,
                contentColor = contentColor
            )
        ) {
            Text(text = text)
        }
    }
}
