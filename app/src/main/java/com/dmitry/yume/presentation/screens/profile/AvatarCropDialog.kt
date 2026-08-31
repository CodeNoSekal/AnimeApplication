package com.dmitry.yume.presentation.screens.profile

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.canhub.cropper.CropImageView
import com.dmitry.yume.presentation.ui.theme.YumeTheme
import java.io.ByteArrayOutputStream

@Composable
fun AvatarCropDialog(
    uri: Uri,
    onDismiss: () -> Unit,
    onCropped: (ByteArray) -> Unit,
    onError: (String) -> Unit,
) {
    var cropView by remember { mutableStateOf<CropImageView?>(null) }
    var isImageReady by remember { mutableStateOf(false) }
    var isCropping by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { if (!isCropping) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(color = YumeTheme.colors.surfaceBg) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = YumeTheme.dimens.sp3),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss, enabled = !isCropping) { Text("Отмена") }
                    Text("Обрезать фото", color = YumeTheme.colors.textPrimary)
                    TextButton(
                        enabled = isImageReady && !isCropping,
                        onClick = {
                            isCropping = true
                            cropView?.croppedImageAsync(
                                saveCompressFormat = Bitmap.CompressFormat.JPEG,
                                saveCompressQuality = 90,
                                reqWidth = 512,
                                reqHeight = 512,
                                options = CropImageView.RequestSizeOptions.RESIZE_EXACT,
                            )
                        },
                    ) { Text("Готово") }
                }

                key(uri) {
                    AndroidView(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        factory = { context ->
                            CropImageView(context).apply {
                                cropShape = CropImageView.CropShape.RECTANGLE
                                guidelines = CropImageView.Guidelines.ON
                                setAspectRatio(1, 1)
                                setOnSetImageUriCompleteListener { _, _, error ->
                                    if (error == null) isImageReady = true
                                    else {
                                        isCropping = false
                                        onError(error.localizedMessage ?: "Не удалось открыть изображение")
                                    }
                                }
                                setOnCropImageCompleteListener { _, result ->
                                    isCropping = false
                                    val bitmap = result.bitmap
                                    if (result.error != null || bitmap == null) {
                                        onError(result.error?.localizedMessage ?: "Не удалось обрезать изображение")
                                    } else {
                                        val output = ByteArrayOutputStream()
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
                                        onCropped(output.toByteArray())
                                    }
                                }
                                setImageUriAsync(uri)
                                cropView = this
                            }
                        },
                    )
                }

                if (isCropping) {
                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}
