package com.example.filesystem

data class S3FileSystemProperties(
    val accessKey: String,
    val secretKey: String,
    val region: String,
    val bucket: String,
)