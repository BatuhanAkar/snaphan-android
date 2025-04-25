package com.batuscode.photoken.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.batuscode.photoken.model.Token

class StoreActivityViewModel : ViewModel() {
    private val _tokens = mutableStateListOf<Token>()
    val tokens: List<Token> = _tokens
    fun addToken(token : Token) {
        _tokens.add(token)
    }
}