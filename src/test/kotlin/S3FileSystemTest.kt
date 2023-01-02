package com.example.filesytem

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import com.example.filesystem.S3FileSystem
import com.example.filesystem.S3FileSystemProperties
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.nio.file.Path

private const val ACCESS_KEY = "access"
private const val SECRET_KEY = "secret"
private const val REGION = "region"
private const val BUCKET = "bucket"

// TODO Test input validation
class S3FileSystemTest {

    @MockK
    private lateinit var mockS3: AmazonS3

    private lateinit var instance: S3FileSystem

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        val properties = S3FileSystemProperties(ACCESS_KEY, SECRET_KEY, REGION, BUCKET)
        instance = S3FileSystem(mockS3, properties)
    }

    @Test
    fun `A single file can be written to the file system`() {
        val file = Path.of("filename.txt")
        val content = "content"
        val inputStream = content.byteInputStream()

        val putObjectSlot = slot<PutObjectRequest>()
        every { mockS3.doesObjectExist(BUCKET, file.toString()) } returns false
        every { mockS3.putObject(capture(putObjectSlot)) } returns PutObjectResult()

        val result = instance.write(inputStream, file).getOrFail()

        assertThat(result).isEqualTo(file)
        val captured = putObjectSlot.captured
        assertThat(captured.bucketName).isEqualTo(BUCKET)
        assertThat(captured.key).isEqualTo(file.toString())
        assertThat(captured.inputStream).isEqualTo(inputStream)
    }

    @Test
    fun `A single file can be read from the file system`() {
        val file = Path.of("filename.txt")
        val content = "content"

        val s3Object = S3Object()
        s3Object.setObjectContent(content.byteInputStream())

        val getObjectSlot = slot<GetObjectRequest>()
        every { mockS3.doesObjectExist(BUCKET, file.toString()) } returns true
        every { mockS3.getObject(capture(getObjectSlot)) } returns s3Object

        val outputStream = ByteArrayOutputStream()
        val result = instance.read(file, outputStream).getOrFail()

        assertThat(result).isEqualTo(file)
        val captured = getObjectSlot.captured
        assertThat(captured.bucketName).isEqualTo(BUCKET)
        assertThat(captured.key).isEqualTo(file.toString())
        assertThat(outputStream.toByteArray()).isEqualTo(content.toByteArray())
    }

    @Test
    fun `A single file can be deleted from the file system`() {
        val file = Path.of("filename.txt")

        val deleteObjectSlot = slot<DeleteObjectRequest>()
        every { mockS3.doesObjectExist(BUCKET, file.toString()) } returns true
        every { mockS3.deleteObject(capture(deleteObjectSlot)) } returns Unit

        val result = instance.delete(file).getOrFail()

        assertThat(result).isEqualTo(file)
        val captured = deleteObjectSlot.captured
        assertThat(captured.bucketName).isEqualTo(BUCKET)
        assertThat(captured.key).isEqualTo(file.toString())
    }
}