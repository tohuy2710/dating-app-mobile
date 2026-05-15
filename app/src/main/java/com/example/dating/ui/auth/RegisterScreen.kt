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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

// Convert dateDigits from dd/mm/yyyy format to YYYY-MM-DD format
fun convertDateDigitsToISO(digits: String): String? {
    if (digits.length != 8) return null
    val day = digits.substring(0, 2)
    val month = digits.substring(2, 4)
    val year = digits.substring(4, 8)
    return "$year-$month-$day"
}

fun shouldBlockDigit(digits: String): Boolean {
    if (digits.length >= 1 && digits[0].digitToInt() > 3) return true
    if (digits.length >= 3 && digits[2].digitToInt() > 1) return true
    return false
}

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onRegisterSuccess: (email: String, password: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateDigits = remember { mutableStateOf("") }
    val dateError = remember { mutableStateOf<String?>(null) }
    val genderExpanded = remember { mutableStateOf(false) }
    
    val genderOptions = listOf("male", "female", "other")
    val genderDisplayMap = mapOf("male" to "Nam", "female" to "Nữ", "other" to "Khác")

    // Handle successful registration
    LaunchedEffect(authViewModel.registerUiState) {
        if (authViewModel.registerUiState is RegisterUiState.Success) {
            // Pass credentials to navigate back to login and pre-fill
            onRegisterSuccess(authViewModel.registerEmail, authViewModel.registerPassword)
        }
    }

    // Reset state when entering screen
    LaunchedEffect(Unit) {
        authViewModel.resetRegisterState()
    }

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
            value = authViewModel.registerEmail,
            onValueChange = authViewModel::updateRegisterEmail,
            placeholder = "Email",
            leadingIcon = Icons.Outlined.AccountCircle,
            enabled = authViewModel.registerUiState !is RegisterUiState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegisterField(
            value = authViewModel.registerPassword,
            onValueChange = authViewModel::updateRegisterPassword,
            placeholder = "Mật khẩu (tối thiểu 6 ký tự)",
            leadingIcon = Icons.Outlined.Lock,
            visualTransformation = PasswordVisualTransformation(),
            enabled = authViewModel.registerUiState !is RegisterUiState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegisterField(
            value = authViewModel.registerFullName,
            onValueChange = authViewModel::updateRegisterFullName,
            placeholder = "Tên đầy đủ",
            leadingIcon = Icons.Outlined.Person,
            enabled = authViewModel.registerUiState !is RegisterUiState.Loading
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
                    if (digits.length == 8) {
                        convertDateDigitsToISO(digits)?.let {
                            authViewModel.updateRegisterBirthDate(it)
                        }
                    }
                }
            },
            placeholder = { Text("dd/mm/yyyy (tùy chọn)") },
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
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black900),
            enabled = authViewModel.registerUiState !is RegisterUiState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = authViewModel.registerUiState !is RegisterUiState.Loading
                ) { genderExpanded.value = !genderExpanded.value }
        ) {
            OutlinedTextField(
                value = genderDisplayMap[authViewModel.registerGender] ?: "Giới tính",
                onValueChange = {},
                placeholder = { Text("Giới tính (tùy chọn)") },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Gray700
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black900),
                enabled = false
            )

            DropdownMenu(
                expanded = genderExpanded.value,
                onDismissRequest = { genderExpanded.value = false },
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.TopStart)
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(genderDisplayMap[option] ?: option) },
                        onClick = {
                            authViewModel.updateRegisterGender(option)
                            genderExpanded.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        RegisterField(
            value = authViewModel.registerBio,
            onValueChange = authViewModel::updateRegisterBio,
            placeholder = "Bio (tùy chọn)",
            leadingIcon = Icons.Outlined.Info,
            enabled = authViewModel.registerUiState !is RegisterUiState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (val state = authViewModel.registerUiState) {
            is RegisterUiState.Loading -> {
                CircularProgressIndicator(color = BrandPink)
            }

            is RegisterUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                RegisterButton(
                    onClick = { authViewModel.register() },
                    text = "Đăng ký",
                    enabled = true
                )
            }

            else -> {
                RegisterButton(
                    onClick = { authViewModel.register() },
                    text = "Đăng ký",
                    enabled = authViewModel.registerUiState !is RegisterUiState.Loading
                )
            }
        }
    }
}

@Composable
private fun RegisterField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true
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
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black900),
        enabled = enabled
    )
}

@Composable
private fun RegisterButton(
    onClick: () -> Unit,
    text: String = "Đăng ký",
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        shape = RoundedCornerShape(34.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        enabled = enabled
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
