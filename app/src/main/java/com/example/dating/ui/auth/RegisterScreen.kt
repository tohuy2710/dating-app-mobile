package com.example.dating.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dating.ui.theme.Black900
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.BorderGray
import com.example.dating.ui.theme.Gray700

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text

        val formatted = buildString {
            digits.forEachIndexed { index, char ->
                if (index == 2 || index == 4) append("/")
                append(char)
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 4 -> offset + 1
                    offset <= 8 -> offset + 2
                    else -> formatted.length
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset - 1
                    offset <= 10 -> offset - 2
                    else -> digits.length
                }
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}


fun validateDateInput(digits: String): String? {
    if (digits.length < 2) return null

    val day = digits.take(2).toIntOrNull() ?: return "Ngày không hợp lệ"

    if (day < 1 || day > 31) return "Ngày phải từ 01 đến 31"

    if (digits.length < 4) return null

    val month = digits.substring(2, 4).toIntOrNull() ?: return "Tháng không hợp lệ"

    if (month < 1 || month > 12) return "Tháng phải từ 01 đến 12"

    if (digits.length < 8) return null

    val year = digits.substring(4, 8).toIntOrNull() ?: return "Năm không hợp lệ"

    if (year < 1900 || year > 2100) return "Năm phải từ 1900 đến 2100"

    val maxDay = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 31
    }

    if (day > maxDay) return "Tháng $month năm $year chỉ có $maxDay ngày"

    return null
}

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val fullName = remember { mutableStateOf("") }
    val dateDigits = remember { mutableStateOf("") }
    val dateError = remember { mutableStateOf<String?>(null) }
    val gender = remember { mutableStateOf("") }
    val school = remember { mutableStateOf("") }
    val genderExpanded = remember { mutableStateOf(false) }
    val selectedImagePath = remember { mutableStateOf<String?>(null) }

    val genderOptions = listOf("Nam", "Nữ", "Khác")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                    tint = Black900
                )
            }
            Text(
                text = "Đăng ký tài khoản",
                style = MaterialTheme.typography.titleLarge,
                color = Black900,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                maxLines = 1
            )
        }

        RegisterField(
            value = username.value,
            onValueChange = { username.value = it },
            placeholder = "Tên đăng nhập",
            leadingIcon = Icons.Outlined.AccountCircle
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegisterField(
            value = password.value,
            onValueChange = { password.value = it },
            placeholder = "Mật khẩu",
            leadingIcon = Icons.Outlined.Lock,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegisterField(
            value = fullName.value,
            onValueChange = { fullName.value = it },
            placeholder = "Tên đầy đủ",
            leadingIcon = Icons.Outlined.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = dateDigits.value,
            onValueChange = { newValue ->
                val digits = newValue.filter { it.isDigit() }.take(8)

                val blocked = shouldBlockDigit(digits)
                if (!blocked) {
                    dateDigits.value = digits
                    dateError.value = validateDateInput(digits)
                }
            },
            placeholder = { Text("dd/mm/yyyy") },
            leadingIcon = {
                Icon(
                    Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = Gray700
                )
            },
            isError = dateError.value != null,
            supportingText = {
                dateError.value?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = DateVisualTransformation(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black900)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = gender.value,
                onValueChange = {},
                placeholder = { Text("Giới tính") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Gray700
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { genderExpanded.value = !genderExpanded.value },
                shape = RoundedCornerShape(18.dp),
                readOnly = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black900),
                enabled = false
            )

            DropdownMenu(
                expanded = genderExpanded.value,
                onDismissRequest = { genderExpanded.value = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender.value = option
                            genderExpanded.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        RegisterField(
            value = school.value,
            onValueChange = { school.value = it },
            placeholder = "Trường đang theo học",
            leadingIcon = Icons.Outlined.Info
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5))
                .padding(24.dp)
                .clickable { /* Handle image selection */ },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⬆",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Gray700,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tải ảnh thẻ sinh viên",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray700,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "PNG, JPG tối đa 10MB",
                    style = MaterialTheme.typography.labelLarge,
                    color = Gray700,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        RegisterButton(
            onClick = onRegisterSuccess,
            text = "Tiếp theo"
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
fun shouldBlockDigit(digits: String): Boolean {

    if (digits.length >= 1 && digits[0].digitToInt() > 3) return true

    if (digits.length >= 3 && digits[2].digitToInt() > 1) return true

    return false
}

@Composable
private fun RegisterField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = Gray700) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        visualTransformation = visualTransformation,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black900)
    )
}

@Composable
private fun RegisterButton(
    onClick: () -> Unit,
    text: String = "Tiếp theo"
) {
    Button(
        onClick = onClick,
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
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}