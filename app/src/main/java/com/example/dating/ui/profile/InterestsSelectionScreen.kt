package com.example.dating.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dating.data.InterestsData
import com.example.dating.ui.common.SelectableInterests
import com.example.dating.ui.theme.BrandPink

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun InterestsSelectionScreen(
    initialSelectedInterests: Set<String> = emptySet(),
    onBackClick: () -> Unit,
    onSelectionDone: (Set<String>) -> Unit
) {
    // Tạo state với các sở thích đã chọn từ profile
    var selectedInterests by remember { mutableStateOf(initialSelectedInterests) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sở thích") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { onSelectionDone(selectedInterests) }) {
                        Text("Xong", color = BrandPink)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Chọn những sở thích phù hợp với bạn",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Combine availableInterests với các sở thích đang chọn nhưng không có trong list mặc định
            val allInterests = (InterestsData.availableInterests + initialSelectedInterests)
                .distinct()

            SelectableInterests(
                availableInterests = allInterests,
                selectedInterests = selectedInterests,
                onSelectionChange = { selectedInterests = it }
            )
        }
    }
}