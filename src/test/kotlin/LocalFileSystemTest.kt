package com.example.filesytem

import com.example.filesystem.LocalFileSystem
import com.example.filesystem.LocalFileSystemProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path

// TODO Test input validation
class LocalFileSystemTest {

    @TempDir
    private lateinit var tempDir: Path

    private lateinit var instance: LocalFileSystem

    @BeforeEach
    fun setUp() {
        val properties = LocalFileSystemProperties(tempDir.toString())
        instance = LocalFileSystem(properties)
    }

    @Test
    fun `A single file can be written to the file system`() {
        // Arrange
        val file = Path.of("file.txt")
        val content = "content"
        val inputStream = content.byteInputStream()

        // Act
        instance.write(inputStream, file).getOrFail()

        // Assert
        assertThat(Files.readAllBytes(tempDir.resolve(file))).isEqualTo(content.toByteArray())
    }

    @Test
    fun `A single file can be read from the file system`() {
        // Arrange
        val file = Path.of("file.txt")
        val path = tempDir.resolve(file)
        val content = "content"

        Files.createFile(path)
        Files.write(path, content.toByteArray())

        // Act
        val outputStream = ByteArrayOutputStream()
        instance.read(path, outputStream).getOrFail()

        // Assert
        assertThat(outputStream.toByteArray()).isEqualTo(content.toByteArray())
    }

    @Test
    fun `A single file can be deleted from the file system`() {
        // Arrange
        val file = Path.of("file.txt")
        val path = tempDir.resolve(file)
        val content = "content"

        Files.createFile(path)
        Files.write(path, content.toByteArray())

        // Act
        instance.delete(file).getOrFail()

        // Assert
        assertThat(file).doesNotExist()
    }
}
