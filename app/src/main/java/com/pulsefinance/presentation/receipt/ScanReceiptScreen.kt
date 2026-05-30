package com.pulsefinance.presentation.receipt

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.model.PaymentMethod
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScanReceiptScreen(
    onBack: () -> Unit,
    viewModel: ScanReceiptViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    val cameraPermission = Manifest.permission.CAMERA
    val galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var pendingCameraLaunch by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        pendingCameraLaunch = false
        if (bitmap != null) {
            viewModel.onImageSelected(bitmap)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                if (bitmap != null) {
                    viewModel.onImageSelected(bitmap)
                }
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted && pendingCameraLaunch) {
            cameraLauncher.launch(null)
        } else if (!granted) {
            pendingCameraLaunch = false
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    fun launchCamera() {
        val granted = ContextCompat.checkSelfPermission(context, cameraPermission) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            pendingCameraLaunch = true
            cameraPermissionLauncher.launch(cameraPermission)
        }
    }

    fun launchGallery() {
        val granted = ContextCompat.checkSelfPermission(context, galleryPermission) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        if (granted) {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            galleryPermissionLauncher.launch(galleryPermission)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = when (state.step) {
                        ScanReceiptStep.Capture -> "Scan receipt"
                        ScanReceiptStep.Processing -> "Scan receipt"
                        ScanReceiptStep.Review -> "Review scanned expense"
                    },
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PulseColors.Background),
        )

        when (state.step) {
            ScanReceiptStep.Capture -> CaptureStep(
                errorMessage = state.errorMessage,
                onCameraClick = ::launchCamera,
                onGalleryClick = ::launchGallery,
            )
            ScanReceiptStep.Processing -> ProcessingStep(message = state.processingMessage)
            ScanReceiptStep.Review -> ReviewStep(
                state = state,
                viewModel = viewModel,
                onScanAgain = viewModel::onScanAgain,
            )
        }
    }
}

@Composable
private fun CaptureStep(
    errorMessage: String?,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PulseSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.lg),
    ) {
        Text(
            text = "Take a photo or choose a receipt from your gallery. Text is read on your phone only—nothing is uploaded.",
            color = PulseColors.TextSecondary,
            style = MaterialTheme.typography.bodyLarge,
        )

        Button(
            onClick = onCameraClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PulseColors.Primary),
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.size(PulseSpacing.sm))
            Text("Take photo")
        }

        OutlinedButton(
            onClick = onGalleryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = PulseColors.Primary)
            Spacer(modifier = Modifier.size(PulseSpacing.sm))
            Text("Choose from gallery", color = PulseColors.Primary)
        }

        if (errorMessage != null) {
            Text(text = errorMessage, color = PulseColors.Danger, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ProcessingStep(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PulseColors.Primary)
            Spacer(modifier = Modifier.height(PulseSpacing.lg))
            Text(text = message, color = PulseColors.TextSecondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ReviewStep(
    state: ScanReceiptUiState,
    viewModel: ScanReceiptViewModel,
    onScanAgain: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = PulseSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.lg),
    ) {
        Text(
            text = "Check scanned details before saving.",
            color = PulseColors.TextSecondary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PulseColors.SurfaceHigh)
                .padding(PulseSpacing.md),
        )

        OutlinedTextField(
            value = state.amountText,
            onValueChange = viewModel::onAmountChanged,
            label = { Text("Amount") },
            prefix = { Text("रू ") },
            placeholder = { Text("0.00") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.headlineMedium,
            colors = scanTextFieldColors(),
        )

        OutlinedTextField(
            value = state.title,
            onValueChange = viewModel::onTitleChanged,
            label = { Text("Title") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = scanTextFieldColors(),
        )

        OutlinedTextField(
            value = state.merchant,
            onValueChange = viewModel::onMerchantChanged,
            label = { Text("Merchant (optional)") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = scanTextFieldColors(),
        )

        if (state.suggestedCategory != null && state.selectedCategory != state.suggestedCategory) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PulseColors.SurfaceHigh)
                    .clickable { viewModel.onAcceptSuggestion() }
                    .padding(PulseSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CategoryDot(color = parseColor(state.suggestedCategory!!.colorHex))
                Text(
                    text = "Suggested: ${state.suggestedCategory!!.name}",
                    color = PulseColors.TextSecondary,
                )
            }
        }

        Text(text = "Category", color = PulseColors.TextSecondary)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
        ) {
            state.categories.forEach { category ->
                CategoryChip(
                    category = category,
                    selected = state.selectedCategory?.id == category.id,
                    onClick = { viewModel.onCategorySelected(category) },
                )
            }
        }

        Text(text = "Payment method", color = PulseColors.TextSecondary)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
            verticalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
        ) {
            PaymentMethod.entries.forEach { method ->
                val selected = state.selectedPaymentMethod == method
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.onPaymentMethodSelected(method) },
                    label = { Text(paymentMethodLabel(method)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PulseColors.Primary.copy(alpha = 0.2f),
                        selectedLabelColor = PulseColors.Primary,
                        containerColor = PulseColors.Surface,
                        labelColor = PulseColors.TextSecondary,
                    ),
                )
            }
        }

        var showDatePicker by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.selectedDateText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                colors = scanTextFieldColors(),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true },
            )
        }
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.onDateSelected(millisToEpochDay(millis))
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        OutlinedTextField(
            value = state.note,
            onValueChange = viewModel::onNoteChanged,
            label = { Text("Note (optional)") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth(),
            colors = scanTextFieldColors(),
        )

        if (state.errorMessage != null) {
            Text(text = state.errorMessage!!, color = PulseColors.Danger)
        }

        Button(
            onClick = viewModel::onSave,
            enabled = !state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PulseColors.Primary),
        ) {
            Text(if (state.isSaving) "Saving..." else "Save expense")
        }

        OutlinedButton(
            onClick = onScanAgain,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving,
        ) {
            Text("Scan again", color = PulseColors.Primary)
        }

        Spacer(modifier = Modifier.height(PulseSpacing.xl))
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(category.name) },
        leadingIcon = if (selected) {
            { CategoryDot(color = parseColor(category.colorHex)) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PulseColors.SurfaceHigh,
            selectedLabelColor = PulseColors.TextPrimary,
            containerColor = PulseColors.Surface,
            labelColor = PulseColors.TextSecondary,
        ),
    )
}

@Composable
private fun CategoryDot(color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun scanTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PulseColors.Primary,
    unfocusedBorderColor = PulseColors.SurfaceHigh,
    focusedLabelColor = PulseColors.Primary,
    unfocusedLabelColor = PulseColors.TextSecondary,
    cursorColor = PulseColors.Primary,
    focusedTextColor = PulseColors.TextPrimary,
    unfocusedTextColor = PulseColors.TextPrimary,
)

private fun paymentMethodLabel(method: PaymentMethod): String = when (method) {
    PaymentMethod.Cash -> "Cash"
    PaymentMethod.Esewa -> "eSewa"
    PaymentMethod.Khalti -> "Khalti"
    PaymentMethod.Fonepay -> "Fonepay"
    PaymentMethod.Bank -> "Bank"
    PaymentMethod.Card -> "Card"
}

private fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        PulseColors.Other
    }
}

private fun millisToEpochDay(utcMillis: Long): Long =
    java.time.Instant.ofEpochMilli(utcMillis)
        .atZone(java.time.ZoneOffset.UTC)
        .toLocalDate()
        .toEpochDay()
