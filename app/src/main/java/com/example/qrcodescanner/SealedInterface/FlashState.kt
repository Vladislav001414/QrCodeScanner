package com.example.qrcodescanner.SealedInterface

sealed interface FlashState{
    object Disabled: FlashState
    object OnForward: FlashState
    object OnBackward: FlashState
}