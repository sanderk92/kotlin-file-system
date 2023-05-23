package com.example.filesystem

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path

class S3FileSystem(
    private val s3: AmazonS3,
    private val props: S3FileSystemProperties,
) : FileSystem {

    override fun write(inputStream: InputStream, targetPath: Path): FileSystemResult {
        if (exists(targetPath)) return InputError("targetPath already exists")

        val request = putObjectRequest(inputStream, targetPath.toString())

        return runCatching { s3.putObject(request) }
            .map { Success(targetPath) }
            .getOrElse(::FileSystemError)
    }

    override fun read(sourcePath: Path, outputStream: OutputStream): FileSystemResult {
        if (!exists(sourcePath)) return InputError("sourcePath does not exist")

        val request = getObjectRequest(sourcePath.toString())

        return runCatching { s3.getObject(request).also { transfer(it, outputStream) } }
            .map { Success(sourcePath) }
            .getOrElse(::FileSystemError)
    }

    override fun delete(sourcePath: Path): FileSystemResult {
        if (!exists(sourcePath)) return InputError("sourcePath does not exist")

        val request = deleteObjectRequest(sourcePath.toString())

        return runCatching { s3.deleteObject(request) }
            .map { Success(sourcePath) }
            .getOrElse(::FileSystemError)
    }

    private fun exists(targetPath: Path) =
        s3.doesObjectExist(props.bucket, targetPath.toString())

    private fun putObjectRequest(inputStream: InputStream, fileKey: String): PutObjectRequest =
        PutObjectRequest(
            props.bucket,
            fileKey,
            inputStream,
            ObjectMetadata()
        )

    private fun getObjectRequest(fileKey: String): GetObjectRequest =
        GetObjectRequest(
            props.bucket,
            fileKey
        )

    private fun deleteObjectRequest(fileKey: String): DeleteObjectRequest =
        DeleteObjectRequest(
            props.bucket,
            fileKey
        )
}

private fun transfer(s3Object: S3Object, outputStream: OutputStream) {
    runCatching {
        s3Object.objectContent.use { inputStream ->
            outputStream.use { outputStream ->
                inputStream.transferTo(outputStream)
            }
        }
    }
}