package com.example.filesytem

import com.example.filesystem.FileSystemError
import com.example.filesystem.FileSystemResult
import com.example.filesystem.InputError
import com.example.filesystem.Success

fun FileSystemResult.getOrFail() = when (this) {
    is Success -> path
    is InputError -> throw AssertionError("Expected a success result, but was a InputError. Cause: $message")
    is FileSystemError -> throw AssertionError("Expected a success result, but was a FileSystemError. Cause: $exception")
}