package com.example.filesystem

sealed interface FileSystemResult
object Success : FileSystemResult
class InputError(val message: String) : FileSystemResult
class FileSystemError(val message: String) : FileSystemResult

fun fileSystemResult(fileSystemFn: () -> Unit) =
    try {
        fileSystemFn()
        Success
    } catch (e: Exception) {
        FileSystemError(e.message ?: "Unexpected exception")
    }
