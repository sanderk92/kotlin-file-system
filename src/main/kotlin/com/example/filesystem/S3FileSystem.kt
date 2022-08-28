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
        val request = putObjectRequest(inputStream, targetPath.toString())

        if (exists(targetPath)) return InputError("targetPath already exists")

        return fileSystemResult { s3.putObject(request) }
    }

    override fun read(sourcePath: Path, outputStream: OutputStream): FileSystemResult {
        val request = getObjectRequest(sourcePath.toString())

        if (!exists(sourcePath)) return InputError("sourcePath does not exist")

        return fileSystemResult { s3.getObject(request).also { transfer(it, outputStream) } }
    }

    override fun delete(sourcePath: Path): FileSystemResult {
        val request = deleteObjectRequest(sourcePath.toString())

        if (!exists(sourcePath)) return InputError("sourcePath does not exist")

        return fileSystemResult { s3.deleteObject(request) }
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