package com.example.filesystem

import java.nio.file.Path

sealed interface FileSystemResult
class Success(val path: Path) : FileSystemResult
class InputError(val message: String) : FileSystemResult
class FileSystemError(val exception: Throwable) : FileSystemResult

fun fileSystemResult(path: Path, fileSystemFn: () -> Unit): FileSystemResult =
    runCatching { fileSystemFn() }
        .map { Success(path) }
        .getOrElse(::FileSystemError)
