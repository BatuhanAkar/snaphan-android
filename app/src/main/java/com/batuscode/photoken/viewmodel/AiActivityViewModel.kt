package com.batuscode.photoken.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AiActivityViewModel : ViewModel() {
    private val _selectedMod = mutableStateOf<String>("Generate")
    val selectedMod : MutableState<String> = _selectedMod

    fun updateSelectedMod(mod : String){
        _selectedMod.value = mod
    }
    private val _imageUrls = mutableStateListOf<String>()
    val imageUrls: List<String> = _imageUrls

    fun addImage(url: String) {
        _imageUrls.add(url)
    }

    fun removeImage(url: String) {
        _imageUrls.remove(url)
    }

    fun clearImages() {
        _imageUrls.clear()
    }
}