package com.example.dating.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.DarkBackground
import com.example.dating.ui.theme.DarkSecondaryText
import com.example.dating.ui.theme.DarkText
import com.example.dating.ui.theme.LightBackground
import com.example.dating.ui.theme.LightSecondaryText
import com.example.dating.ui.theme.LightText
import com.example.dating.ui.theme.SecondaryPurple

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val textColor = if (isDarkTheme) DarkText else LightText
    val secondaryTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText

    // Làm mới trạng thái khi vào màn hình đăng nhập
    LaunchedEffect(Unit) {
        authViewModel.resetState()
    }

    LaunchedEffect(authViewModel.loginUiState) {
        if (authViewModel.loginUiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(BrandPinkDark, BrandPink))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Student Connect",
            style = MaterialTheme.typography.headlineLarge,
            color = textColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Kết nối sinh viên",
            style = MaterialTheme.typography.titleLarge,
            color = secondaryTextColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        LoginField(
            value = authViewModel.emailInput,
            onValueChange = authViewModel::updateEmail,
            placeholder = "Email",
            leadingIcon = Icons.Outlined.AccountCircle,
            enabled = authViewModel.loginUiState !is LoginUiState.Loading
        )

        Spacer(modifier = Modifier.height(18.dp))

        LoginField(
            value = authViewModel.passwordInput,
            onValueChange = authViewModel::updatePassword,
            placeholder = "Mật khẩu",
            leadingIcon = Icons.Outlined.Lock,
            visualTransformation = PasswordVisualTransformation(),
            enabled = authViewModel.loginUiState !is LoginUiState.Loading
        )

        Spacer(modifier = Modifier.height(28.dp))

        when (val state = authViewModel.loginUiState) {
            is LoginUiState.Loading -> {
                CircularProgressIndicator(color = BrandPink)
            }

            is LoginUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                LoginButton(onClick = authViewModel::login)
            }

            else -> {
                LoginButton(onClick = authViewModel::login)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Chưa có tài khoản? ",
                style = MaterialTheme.typography.bodyLarge,
                color = secondaryTextColor
            )
            Text(
                text = "Đăng ký ngay",
                style = MaterialTheme.typography.bodyLarge,
                color = SecondaryPurple,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    onRegisterClick()
                }
            )
        }
    }
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    enabled: Boolean = true
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) DarkText else LightText
    val secondaryTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        enabled = enabled,
        visualTransformation = visualTransformation,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedPlaceholderColor = secondaryTextColor,
            unfocusedPlaceholderColor = secondaryTextColor,
            focusedLeadingIconColor = secondaryTextColor,
            unfocusedLeadingIconColor = secondaryTextColor,
            focusedBorderColor = BrandPink, // Thêm tuỳ chọn viền hồng khi focus
        )
    )
}

@Composable
private fun LoginButton(onClick: () -> Unit) {
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
                .background(Brush.linearGradient(listOf(BrandPinkDark, BrandPink))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Đăng nhập",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}