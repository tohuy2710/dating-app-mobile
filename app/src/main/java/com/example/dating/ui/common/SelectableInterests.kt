package com.example.dating.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.DarkSecondaryText
import com.example.dating.ui.theme.LightSecondaryText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectableInterests(
    availableInterests: List<String>,
    selectedInterests: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val unselectedTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText
    val unselectedBorderColor = if (isDarkTheme) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.15f)
    val unselectedBgColor = Color.Transparent

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        availableInterests.forEach { interest ->

            val isSelected = selectedInterests.contains(interest)

            val backgroundColor = if (isSelected) BrandPink.copy(alpha = 0.12f) else unselectedBgColor
            val borderColor = if (isSelected) BrandPink.copy(alpha = 0.6f) else unselectedBorderColor
            val textColor = if (isSelected) BrandPinkDark else unselectedTextColor

            Surface(
                color = backgroundColor,
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(999.dp)
                    )
                    .clip(RoundedCornerShape(999.dp))
                    .clickable {
                        val updatedSelection =
                            if (isSelected) {
                                selectedInterests - interest
                            } else {
                                selectedInterests + interest
                            }
                        onSelectionChange(updatedSelection)
                    }
            ) {
                Text(
                    text = interest,
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    )
                )
            }
        }
    }
}