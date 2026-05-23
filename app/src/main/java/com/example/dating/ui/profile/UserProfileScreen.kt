package com.example.dating.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dating.R
import com.example.dating.data.model.User
import com.example.dating.ui.theme.Black900
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.Gray700
import com.example.dating.ui.theme.SecondaryPurple
import com.example.dating.ui.theme.White

@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
    onSettingsClick: () -> Unit = {},
    onEditInterestsClick: (
        List<String>
    ) -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {

        is ProfileUiState.Loading -> {

            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ProfileUiState.Error -> {

            val message = (uiState as ProfileUiState.Error).message

            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    color = Color.Red
                )
            }
        }

        is ProfileUiState.Success -> {

            val user = (uiState as ProfileUiState.Success).user

            UserProfileContent(
                user = user,
                modifier = modifier,
                onSettingsClick = onSettingsClick,
                onEditInterestsClick = {

                    val interests =
                        user.preferences
                            ?.anonymousInterests
                            ?.split(",")
                            ?.map { it.trim() }
                            ?.filter { it.isNotEmpty() }
                            ?: emptyList()

                    onEditInterestsClick(interests)
                }
            )
        }
    }
}

@Composable
private fun UserProfileContent(
    user: User,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onEditInterestsClick: () -> Unit = {}
) {

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    var isEditing by remember {
        mutableStateOf(false)
    }

    var name by remember {
        mutableStateOf(user.fullName)
    }

    var age by remember {
        mutableStateOf(user.birthDate ?: "")
    }

    var bio by remember {
        mutableStateOf(user.bio ?: "")
    }

    var question by remember {
        mutableStateOf("Gu của em là gì?")
    }

    var answer by remember {
        mutableStateOf("Người tử tế")
    }

    var showPhotoDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteConfirm by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {

            UserHeroImage(
                avatarUrl = user.avatarUrl,
                onSettingsClick = onSettingsClick
            )

            UserActionButtons(
                isEditing = isEditing,
                onEditToggle = {
                    isEditing = !isEditing
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 24.dp,
                        bottom = 12.dp
                    )
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
            onBioChange = { bio = it },
            gender = user.gender ?: "Unknown"
        )

        Spacer(modifier = Modifier.height(16.dp))

        UserInterestChips(
            interests = user.preferences
                ?.anonymousInterests
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList(),
            onEditInterestsClick = onEditInterestsClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        UserGalleryGrid(
            photos = user.photos.map { it.imageUrl },
            onAddPhotoClick = {
                showPhotoDialog = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DeleteAccountButton(
            onDeleteClick = {
                showDeleteConfirm = true
            }
        )
    }

    if (showPhotoDialog) {

        AlertDialog(
            onDismissRequest = {
                showPhotoDialog = false
            },
            title = {
                Text("Add Photo")
            },
            text = {
                Text("Choose a photo from gallery or camera")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPhotoDialog = false
                    }
                ) {
                    Text(
                        "Gallery",
                        color = BrandPink
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPhotoDialog = false
                    }
                ) {
                    Text(
                        "Camera",
                        color = BrandPink
                    )
                }
            }
        )
    }

    if (showDeleteConfirm) {

        AlertDialog(
            onDismissRequest = {
                showDeleteConfirm = false
            },
            title = {
                Text("Delete Account?")
            },
            text = {
                Text("This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                    }
                ) {
                    Text(
                        "Delete",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun UserHeroImage(
    avatarUrl: String,
    onSettingsClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(
                RoundedCornerShape(
                    bottomStart = 32.dp,
                    bottomEnd = 32.dp
                )
            )
    ) {

        if (avatarUrl.isNotEmpty()) {

            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

        } else {

            Image(
                painter = painterResource(id = R.drawable.thl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {

            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = White
            )
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
        containerColor = if (isEditing) {
            Color(0xFF4CAF50)
        } else {
            BrandPink
        },
        contentColor = White,
        shape = CircleShape,
        modifier = modifier.size(56.dp)
    ) {

        Icon(
            if (isEditing) {
                Icons.Default.Check
            } else {
                Icons.Default.Edit
            },
            contentDescription = null
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
    onBioChange: (String) -> Unit,
    gender: String
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
                label = {
                    Text("Name")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                label = {
                    Text("Birth date")
                },
                modifier = Modifier.fillMaxWidth()
            )

        } else {

            Text(
                text = name,
                color = Black900,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = age,
                color = Gray700,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = SecondaryPurple,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = gender,
                color = Gray700,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Giới thiệu",
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
private fun UserInterestChips(
    interests: List<String>,
    onEditInterestsClick: () -> Unit
) {

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Sở thích",
                color = Black900,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = onEditInterestsClick
            ) {
                Text(
                    "Chỉnh sửa",
                    color = BrandPink
                )
            }
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            interests.forEach { label ->

                Surface(
                    color = Color(0xFFF1F1F1),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.border(
                        1.dp,
                        Color(0xFFE5E5E5),
                        RoundedCornerShape(999.dp)
                    )
                ) {

                    Text(
                        text = label,
                        color = Gray700,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 10.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun UserGalleryGrid(
    photos: List<String>,
    onAddPhotoClick: () -> Unit
) {

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Ảnh của tôi",
                color = Black900,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = onAddPhotoClick
            ) {
                Text(
                    "Thêm ảnh",
                    color = BrandPink
                )
            }
        }

        photos.chunked(3).forEach { rowPhotos ->

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                rowPhotos.forEach { photo ->

                    UserGalleryTile(
                        imageUrl = photo,
                        modifier = Modifier.weight(1f)
                    )
                }

                repeat(3 - rowPhotos.size) {

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun UserGalleryTile(
    imageUrl: String,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2D0D8))
    ) {

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun DeleteAccountButton(
    onDeleteClick: () -> Unit
) {

    TextButton(
        onClick = onDeleteClick,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Xóa tài khoản",
            color = Color.Red,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {

}