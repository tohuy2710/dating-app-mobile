package com.example.dating.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dating.R
import com.example.dating.data.model.User
import com.example.dating.ui.auth.uploadToCloudinary
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.DarkBackground
import com.example.dating.ui.theme.DarkSecondaryText
import com.example.dating.ui.theme.DarkSurface
import com.example.dating.ui.theme.DarkText
import com.example.dating.ui.theme.LightBackground
import com.example.dating.ui.theme.LightSecondaryText
import com.example.dating.ui.theme.LightSurface
import com.example.dating.ui.theme.LightText
import com.example.dating.ui.theme.White
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                viewModel = viewModel,
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
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onEditInterestsClick: () -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user.fullName) }
    var age by remember { mutableStateOf(user.birthDate ?: "") }
    var bio by remember { mutableStateOf(user.bio ?: "") }

    var showPhotoDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
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
    }

    if (showPhotoDialog) {
        AddPhotoDialog(
            onDismiss = { showPhotoDialog = false },
            onComplete = { imageUrl ->
                viewModel.addPhoto(imageUrl = imageUrl)
                showPhotoDialog = false
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
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
        containerColor = if (isEditing) Color(0xFF4CAF50) else BrandPink,
        contentColor = White,
        shape = CircleShape,
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            if (isEditing) Icons.Default.Check else Icons.Default.Edit,
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    val isDarkTheme = isSystemInDarkTheme()
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface
    val textColor = if (isDarkTheme) DarkText else LightText
    val secondaryTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        onAgeChange(formatter.format(Date(millis)))
                    }
                    showDatePicker = false
                }) {
                    Text("Hoàn tất", color = BrandPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Huỷ", color = textColor)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(surfaceColor)
            .padding(20.dp)
    ) {
        if (isEditing) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Tên") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Birthdate Field with DatePicker
            OutlinedTextField(
                value = age,
                onValueChange = { },
                label = { Text("Ngày sinh") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Chọn ngày sinh"
                        )
                    }
                },
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    showDatePicker = true
                                }
                            }
                        }
                    }
            )

        } else {
            Text(
                text = name,
                color = BrandPink,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = age.ifEmpty { "Chưa cập nhật ngày sinh" },
                color = secondaryTextColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Giới thiệu",
            color = textColor,
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
                text = bio.ifEmpty { "Thêm vài dòng giới thiệu về bạn nhé." },
                color = secondaryTextColor,
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
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) DarkText else LightText

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
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onEditInterestsClick) {
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
                    color = BrandPink.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.border(
                        1.dp,
                        BrandPink.copy(alpha = 0.6f),
                        RoundedCornerShape(999.dp)
                    )
                ) {
                    Text(
                        text = label,
                        color = BrandPinkDark,
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
}

@Composable
private fun UserGalleryGrid(
    photos: List<String>,
    onAddPhotoClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) DarkText else LightText

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
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onAddPhotoClick) {
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
                    Spacer(modifier = Modifier.weight(1f))
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
private fun AddPhotoDialog(
    onDismiss: () -> Unit,
    onComplete: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isDarkTheme = isSystemInDarkTheme()
    val secondaryTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText
    val boxBackground = if (isDarkTheme) Color(0xFF2C2C2E) else Color(0xFFF5F5F5)

    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    isUploading = true
                    val url = uploadToCloudinary(uri, context)
                    imageUrl = url
                } catch (e: Exception) {
                    error = e.message
                } finally {
                    isUploading = false
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm ảnh") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(boxBackground)
                        .clickable {
                            imagePicker.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isUploading -> CircularProgressIndicator()
                        imageUrl != null -> AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        else -> Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "⬆", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Chọn ảnh")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "PNG, JPG tối đa 10MB",
                                color = secondaryTextColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                error?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = it, color = Color.Red)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { imageUrl?.let { onComplete(it) } },
                enabled = imageUrl != null
            ) {
                Text(text = "Hoàn tất", color = BrandPink)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {

}