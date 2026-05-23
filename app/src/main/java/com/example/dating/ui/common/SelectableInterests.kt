package com.example.dating.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.White

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectableInterests(
    availableInterests: List<String>,
    selectedInterests: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        availableInterests.forEach { interest ->

            val isSelected =
                selectedInterests.contains(interest)

            FilterChip(
                selected = isSelected,

                onClick = {

                    val updatedSelection =
                        if (isSelected) {

                            selectedInterests - interest

                        } else {

                            selectedInterests + interest
                        }

                    onSelectionChange(updatedSelection)
                },

                label = {
                    Text(interest)
                },

                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandPink,
                    selectedLabelColor = White
                )
            )
        }
    }
}