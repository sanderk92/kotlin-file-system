package com.example.filesystem

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import java.io.InputStream
import java.net.URL
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.Date
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Primary
@Service
@ConditionalOnMissingBean(FileSystem::class)
internal class S3FileSystem(
    private val s3: AmazonS3,
    private val props: S3FileSystemProperties,
    private val clock: Clock,
) : FileSystem {
    override fun write(key: String, content: InputStream): String {
        val request = PutObjectRequest(props.bucket, key, content, ObjectMetadata())
        s3.putObject(request)
        return key
    }

    override fun delete(key: String) {
        val request = DeleteObjectRequest(props.bucket, key)
        s3.deleteObject(request)
    }

    override fun createLink(key: String, minutes: Long): URL {
        val expiry = clock.instant().plus(minutes, ChronoUnit.MINUTES)
        return s3.generatePresignedUrl(props.bucket, key, Date.from(expiry))
    }

    override fun exists(key: String): Boolean {
        return s3.doesObjectExist(props.bucket, key)
    }
}
