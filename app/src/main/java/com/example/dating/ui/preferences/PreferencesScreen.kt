package com.example.dating.ui.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dating.ui.auth.PreferencesUiState
import com.example.dating.ui.theme.Black900
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.Gray700

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PreferencesScreen(
    preferencesUiState: PreferencesUiState,
    onComplete: (selectedInterests: List<String>, targetGender: String?, minAge: String, maxAge: String, maxDistance: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val predefinedInterests = listOf(
        "Chơi thể thao", "Du lịch", "Đọc sách", "Nghe nhạc", "Xem phim",
        "Nấu ăn", "Chụp ảnh", "Chơi game", "Vẽ tranh", "Khiêu vũ",
        "Thời trang", "Thiên nhiên", "Thú cưng", "Cà phê", "Lập trình",
        "Học ngoại ngữ", "Tình nguyện", "Yoga", "Leo núi", "Làm vườn"
    )

    val selectedInterests = remember { mutableStateListOf<String>() }
    var targetGender by remember { mutableStateOf<String?>(null) }
    var minAge by remember { mutableStateOf("") }
    var maxAge by remember { mutableStateOf("") }
    var maxDistance by remember { mutableStateOf("") }

    val gradientBrush = Brush.horizontalGradient(listOf(BrandPinkDark, BrandPink))

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White)
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
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Black900,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Sở thích",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Black900,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                maxLines = 1
            )
        }

        Text(
            text = "Chọn những sở thích mô tả đúng nhất về bạn",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            color = Gray700
        )

        Spacer(modifier = Modifier.height(24.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            predefinedInterests.forEach { interest ->
                val isSelected = selectedInterests.contains(interest)
                InterestChip(
                    text = interest,
                    isSelected = isSelected,
                    gradientBrush = gradientBrush,
                    onClick = {
                        if (isSelected) selectedInterests.remove(interest)
                        else selectedInterests.add(interest)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Cài đặt tìm kiếm",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Black900
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Tôi muốn tìm",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium,
            color = Gray700
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GenderChoiceButton(
                text = "Nam",
                selected = targetGender == "Nam",
                gradientBrush = gradientBrush,
                onClick = { targetGender = "Nam" },
                modifier = Modifier.weight(1f)
            )

            GenderChoiceButton(
                text = "Nữ",
                selected = targetGender == "Nữ",
                gradientBrush = gradientBrush,
                onClick = { targetGender = "Nữ" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = minAge,
                onValueChange = { newValue -> minAge = newValue.filter { it.isDigit() }.take(2) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tuổi tối thiểu") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(18.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black900),
                singleLine = true
            )

            OutlinedTextField(
                value = maxAge,
                onValueChange = { newValue -> maxAge = newValue.filter { it.isDigit() }.take(2) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tuổi tối đa") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(18.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black900),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = maxDistance,
            onValueChange = { newValue -> maxDistance = newValue.filter { it.isDigit() }.take(3) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Khoảng cách tối đa (km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(18.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black900),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(48.dp))

        when (val state = preferencesUiState) {
            is PreferencesUiState.Loading -> {
                CircularProgressIndicator(color = BrandPink)
                Spacer(modifier = Modifier.height(16.dp))
            }

            is PreferencesUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            else -> Unit
        }

        Button(
            onClick = {
                onComplete(selectedInterests.toList(), targetGender, minAge, maxAge, maxDistance)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(34.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            enabled = preferencesUiState !is PreferencesUiState.Loading
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(34.dp))
                    .background(gradientBrush),
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
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun InterestChip(
    text: String,
    isSelected: Boolean,
    gradientBrush: Brush,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isSelected) Modifier.background(gradientBrush)
                else Modifier
                    .background(Color.White)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Black900,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun GenderChoiceButton(
    text: String,
    selected: Boolean,
    gradientBrush: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(18.dp))
            .then(
                if (selected) Modifier.background(gradientBrush)
                else Modifier.background(Color(0xFFF5F5F5))
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Gray700,
            fontWeight = FontWeight.Bold
        )
    }
}
