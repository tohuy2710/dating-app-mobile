package com.example.dating.ui.auth

import android.content.Context
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
import androidx.compose.ui.unit.dp
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dating.data.model.RegisterRequest
import com.example.dating.ui.theme.Black900
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.Gray700
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okio.source
import org.json.JSONObject

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
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier,
    sharedViewModel: RegisterSharedViewModel
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val fullName = remember { mutableStateOf("") }
    val dateDigits = remember { mutableStateOf("") }
    val dateError = remember { mutableStateOf<String?>(null) }
    val gender = remember { mutableStateOf("") }
    val genderExpanded = remember { mutableStateOf(false) }
    val genderOptions = listOf("male", "female", "other")
    val bio = remember { mutableStateOf("") }
    val error = remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val isUploading = remember { mutableStateOf(false) }
    val avatarUrl = remember { mutableStateOf<String?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri.value = uri

            scope.launch {
                try {
                    isUploading.value = true
                    val url = uploadToCloudinary(uri, context)
                    avatarUrl.value = url
                } catch (e: Exception) {
                    error.value = e.message
                } finally {
                    isUploading.value = false
                }
            }
        }
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
            value = email.value,
            onValueChange = { email.value = it },
            placeholder = "Email",
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
                        text = { Text(genderLabel(option)) },
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
            value = bio.value,
            onValueChange = { bio.value = it },
            placeholder = "Giới thiệu bản thân (bio)",
            leadingIcon = Icons.Outlined.Info
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5))
                .padding(24.dp)
                .clickable {
                    imagePicker.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {

            when {
                isUploading.value -> {
                    androidx.compose.material3.CircularProgressIndicator()
                }

                avatarUrl.value != null -> {
                    AsyncImage(
                        model = avatarUrl.value,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("⬆", style = MaterialTheme.typography.headlineLarge, color = Gray700)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Ảnh đại diện", color = Gray700)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("PNG, JPG tối đa 10MB", color = Gray700)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        RegisterButton(
            onClick = {
                when {
                    email.value.isBlank() -> error.value = "Email không được để trống"
                    password.value.length < 6 -> error.value = "Mật khẩu ít nhất 6 ký tự"
                    fullName.value.isBlank() -> error.value = "Tên không được để trống"
                    gender.value.isBlank() -> error.value = "Chưa chọn giới tính"
                    dateError.value != null -> error.value = "Ngày sinh không hợp lệ"
                    avatarUrl.value == null -> error.value = "Vui lòng chọn ảnh đại diện"
                    else -> {
                        error.value = null
                        sharedViewModel.setBasicInfo(
                            email = email.value,
                            password = password.value,
                            fullName = fullName.value,
                            birthDate = formatBirthDate(dateDigits.value),
                            gender = gender.value,
                            bio = bio.value,
                            imageUrl = avatarUrl.value
                        )
                        onNextPage()
                    }
                }
            },
            text = "Tiếp theo"
        )

        error.value?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

fun shouldBlockDigit(digits: String): Boolean {

    if (digits.length >= 1 && digits[0].digitToInt() > 3) return true

    if (digits.length >= 3 && digits[2].digitToInt() > 1) return true

    return false
}

fun formatBirthDate(digits: String): String {
    if (digits.length != 8) return digits
    return "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, 8)}"
}

fun genderLabel(value: String): String {
    return when (value) {
        "male" -> "Nam"
        "female" -> "Nữ"
        "other" -> "Khác"
        else -> ""
    }
}

suspend fun uploadToCloudinary(uri: Uri, context: Context): String =
    withContext(Dispatchers.IO) {

        val inputStream = context.contentResolver.openInputStream(uri)!!

        val requestBody = object : okhttp3.RequestBody() {
            override fun contentType() = "image/*".toMediaTypeOrNull()

            override fun writeTo(sink: okio.BufferedSink) {
                inputStream.source().use { source ->
                    sink.writeAll(source)
                }
            }
        }

        val request = okhttp3.MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "image.jpg", requestBody)
            .addFormDataPart("upload_preset", "dating_app")
            .build()

        val requestFinal = okhttp3.Request.Builder()
            .url("https://api.cloudinary.com/v1_1/df08peayk/image/upload")
            .post(request)
            .build()

        val response = OkHttpClient().newCall(requestFinal).execute()
        val bodyString = response.body?.string() ?: ""

        Log.d("CLOUDINARY", bodyString)

        val json = JSONObject(bodyString)

        if (json.has("error")) {
            throw Exception(json.getJSONObject("error").optString("message"))
        }

        json.getString("secure_url")
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