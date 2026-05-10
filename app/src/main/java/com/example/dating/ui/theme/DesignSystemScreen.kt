package com.example.dating.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DesignSystemScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(24.dp)
    ) {
        Text(
            text = "Component",
            style = MaterialTheme.typography.headlineLarge,
            color = Black900
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Label", style = MaterialTheme.typography.titleLarge, color = Gray700)
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = White,
                    shadowElevation = 0.dp,
                    modifier = Modifier.border(1.dp, Color(0xFFE6E0FF), RoundedCornerShape(18.dp))
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(12.dp)) {
                        DemoInput("First Name", Icons.Default.Person)
                        DemoInput("Last Name", Icons.Default.Person)
                        DemoInput("Email", Icons.Default.Email)
                        DemoPasswordInput()
                        DemoDropdownInput("Choose Gender")
                        DemoInput("Date of Birth", Icons.Default.Search)
                    }
                }
            }
            Column(modifier = Modifier.weight(1.6f)) {
                Text(text = "Navbar", style = MaterialTheme.typography.titleLarge, color = Gray700)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Button", style = MaterialTheme.typography.titleLarge, color = Gray700)
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = White,
                    modifier = Modifier.border(1.dp, Color(0xFFE6E0FF), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            GradientButton("Button", Modifier.weight(1f))
                            GradientButton("Login", Modifier.weight(1f))
                            GradientButton("Continue >", Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SmallPillButton("Button")
                            CircleIconButton(Icons.Default.Add)
                            CircleIconButton(Icons.Default.Favorite)
                            CircleIconButton(Icons.Default.Close)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
                Text(text = "Search bar", style = MaterialTheme.typography.titleLarge, color = Gray700)
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = White,
                    modifier = Modifier.border(1.dp, Color(0xFFE6E0FF), RoundedCornerShape(18.dp))
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SearchField(modifier = Modifier.weight(1f))
                        SearchField(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Colors", style = MaterialTheme.typography.headlineLarge, color = Black900)
        Spacer(modifier = Modifier.height(20.dp))
        ColorSection()
    }
}

@Composable
private fun DemoInput(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )
}

@Composable
private fun DemoPasswordInput() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )
}

@Composable
private fun DemoDropdownInput(label: String) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )
}

@Composable
private fun GradientButton(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Brush.horizontalGradient(listOf(BrandPink, BrandPinkDark))),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SmallPillButton(text: String) {
    Box(
        modifier = Modifier
            .height(24.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(SecondaryPurple)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = White, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun CircleIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(SecondaryPurple),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = White)
    }
}

@Composable
private fun SearchField(modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Search Pancake") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )
}

@Composable
private fun ColorSection() {
    Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
        ColorRow("Brand Colors") {
            ColorSwatch(BrandPinkDark, "#F80934")
            Spacer(modifier = Modifier.width(12.dp))
            ColorSwatch(BrandPink, "#FA4EBE")
        }
        ColorRow("Secondary Color") {
            ColorSwatch(SecondaryPurple, "#8B5CF6", size = 88.dp)
        }
        ColorRow("Black Color") {
            ColorSwatch(Black900, "#1D1617", size = 60.dp)
            Spacer(modifier = Modifier.width(12.dp))
            ColorSwatch(White, "#FFFFFF", size = 60.dp, border = true)
        }
        ColorRow("Gray Color") {
            ColorSwatch(Gray700, "#7B6F72", size = 60.dp)
            Spacer(modifier = Modifier.width(12.dp))
            ColorSwatch(Gray500, "#ADA4A5", size = 60.dp)
            Spacer(modifier = Modifier.width(12.dp))
            ColorSwatch(Gray300, "#DDDADA", size = 60.dp)
        }
        ColorRow("Border Color") {
            ColorSwatch(BorderGray, "#F7F8F8", size = 60.dp, label = "Border-color")
        }
    }
}

@Composable
private fun ColorRow(title: String, content: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = Gray700, modifier = Modifier.width(160.dp))
        content()
    }
}

@Composable
private fun ColorSwatch(color: Color, hex: String, size: androidx.compose.ui.unit.Dp = 140.dp, border: Boolean = false, label: String? = null) {
    Column {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(12.dp))
                .background(color)
                .then(if (border) Modifier.border(1.dp, Color(0xFFEAEAEA), RoundedCornerShape(12.dp)) else Modifier)
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (label != null) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Black900)
        }
        Text(text = hex, style = MaterialTheme.typography.bodyLarge, color = Black900)
    }
}