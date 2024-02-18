package com.example.filesystem

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "s3")
internal data class S3FileSystemProperties(
    val region: String,
    val bucket: String,
    val accessKey: String,
    val secretKey: String,
)
