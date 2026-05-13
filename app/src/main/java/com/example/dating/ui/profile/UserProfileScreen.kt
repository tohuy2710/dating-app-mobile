package com.example.dating.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dating.R
import com.example.dating.ui.theme.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onEditInterestsClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    
    // States for Editing
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Trần Hà Linh") }
    var age by remember { mutableStateOf("2006") }
    var bio by remember { mutableStateOf("Kích thước cơ bản cho Android thường là 360x576, 360x640 hoặc 360x720 (tỉ lệ 16:10, 16:9 hoặc 2:1).") }
    var question by remember { mutableStateOf("Gu cua em la j?") }
    var answer by remember { mutableStateOf("ng thanh hoa") }
    
    var showPhotoDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            UserHeroImage(onSettingsClick)
            UserActionButtons(
                isEditing = isEditing,
                onEditToggle = { isEditing = !isEditing },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        UserPersonalInfoCard(
            isEditing = isEditing,
            name = name,
            onNameChange = { name = it },
            age = age,
            onAgeChange = { age = it },
            bio = bio,
            onBioChange = { bio = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        UserInterestChips(onEditInterestsClick)

        Spacer(modifier = Modifier.height(16.dp))

        UserGalleryGrid(onAddPhotoClick = { showPhotoDialog = true })

        Spacer(modifier = Modifier.height(16.dp))

        UserQACards(
            isEditing = isEditing,
            question = question,
            onQuestionChange = { question = it },
            answer = answer,
            onAnswerChange = { answer = it }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        DeleteAccountButton(onDeleteClick = { showDeleteConfirm = true })
    }

    // Dialogs
    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Add Photo") },
            text = { Text("Choose a photo from your gallery or take a new one.") },
            confirmButton = {
                TextButton(onClick = { showPhotoDialog = false }) { Text("Gallery", color = BrandPink) }
            },
            dismissButton = {
                TextButton(onClick = { showPhotoDialog = false }) { Text("Camera", color = BrandPink) }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account?") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun UserHeroImage(onSettingsClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.thl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = White)
        }
    }
}

@Composable
private fun UserActionButtons(
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onEditToggle,
        containerColor = if (isEditing) Color(0xFF4CAF50) else BrandPink,
        contentColor = White,
        shape = CircleShape,
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            if (isEditing) Icons.Default.Check else Icons.Default.Edit, 
            contentDescription = if (isEditing) "Save" else "Edit Profile"
        )
    }
}

@Composable
private fun UserPersonalInfoCard(
    isEditing: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(White)
            .padding(20.dp)
    ) {
        if (isEditing) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                label = { Text("Year of birth") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = "$name, $age",
                color = Black900,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Software Developer",
                color = Gray700,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = SecondaryPurple, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Hanoi, Vietnam",
                color = Gray700,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "About Me",
            color = Black900,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (isEditing) {
            OutlinedTextField(
                value = bio,
                onValueChange = onBioChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        } else {
            Text(
                text = bio,
                color = Gray700,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UserInterestChips(onEditInterestsClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Interests",
                color = Black900,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onEditInterestsClick) {
                Text("Edit", color = BrandPink)
            }
        }
        
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val labels = listOf("Coding", "Travel", "Music", "Photography")
            labels.forEach { label ->
                Surface(
                    color = Color(0xFFF1F1F1),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(999.dp))
                ) {
                    Text(
                        text = label,
                        color = Gray700,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UserGalleryGrid(onAddPhotoClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Gallery",
                color = Black900,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onAddPhotoClick) {
                Text("Add Photo", color = BrandPink)
            }
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            UserGalleryTile(modifier = Modifier.weight(1f))
            UserGalleryTile(modifier = Modifier.weight(1f))
            UserGalleryTile(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun UserGalleryTile(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2D0D8))
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.loading_img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun UserQACards(
    isEditing: Boolean,
    question: String,
    onQuestionChange: (String) -> Unit,
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Q&A",
            color = Black900,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Surface(
            color = White,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (isEditing) {
                    OutlinedTextField(
                        value = question,
                        onValueChange = onQuestionChange,
                        label = { Text("Question") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = answer,
                        onValueChange = onAnswerChange,
                        label = { Text("Answer") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = question,
                        color = BrandPink,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = answer,
                        color = Black900,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteAccountButton(onDeleteClick: () -> Unit) {
    TextButton(
        onClick = onDeleteClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Delete Account", color = Color.Red, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    UserProfileScreen()
}
