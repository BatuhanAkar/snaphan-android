package com.batuscode.photoken.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CrudUtils {
    const val crudTAG = "CrudUtils"
    suspend fun saveImageToGallery(context: Context, bitmap: Bitmap, title: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$title.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: return@withContext false

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 95, outputStream)
                } ?: return@withContext false

                // Medya tarayıcıyı güncelle (Galeride anında görünmesi için)
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(uri.toString()),
                    arrayOf("image/png"),
                    null
                )

                Log.e(crudTAG, "Kaydedildi.")
                true // Başarılı
            } catch (e: Exception) {
                Log.e(crudTAG, "Kayıt hatası: ${e.message}")
                false // Başarısız
            }
        }
}