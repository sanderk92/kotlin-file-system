package com.example.filesystem

import com.example.filesystem.model.FileId
import java.io.InputStream
import java.net.URL
import java.nio.file.AccessDeniedException
import java.nio.file.Path
import java.util.UUID
import org.springframework.stereotype.Service

// Only honour alphanumerical, underscores, dashes and dots
private const val FILENAME_REPLACE_PATTERN = "[^a-zA-Z0-9_\\-\\.]"

@Service
internal class FileSystemService(
    private val fileSystem: FileSystem,
) {
    /**
     * Write a new file to the filesystem. The name is checked for any suspicious elements. The identifier
     * consists of the filename prefixed with a random uuid.
     */
    fun write(name: String, content: InputStream): FileId {
        val validFileName = validateFilename(name)
        val fileId = FileId.of(UUID.randomUUID(), validFileName)
        fileSystem.write(fileId.key, content)
        return fileId
    }

    /**
     * Delete the specified file from the filesystem. Versioning of deletions is not regulated client side but by
     * the backing file system.
     */
    fun delete(fileId: FileId) {
        fileSystem.delete(fileId.key)
    }

    /**
     * Create a link to the specified file valid for the specified time. This is a client side operation, thus:
     * - does not check for existence of the file, and a link to a non-existing file can be created
     * - does not perform IO, and can be used without runtime overhead
     */
    fun createLink(fileId: FileId, minutes: Long): URL {
        return fileSystem.createLink(fileId.key, minutes)
    }

    /**
     * [createLink] valid for 30 minutes, our default time for sensitive files.
     */
    fun createShortLivedLink(fileId: FileId): URL {
        return createLink(fileId, 30)
    }

    /**
     * [createLink] valid for 8 hours, our default time for non-sensitive files.
     */
    fun createLongLivedLink(fileId: FileId): URL {
        return createLink(fileId, 480)
    }
}

private fun validateFilename(fileName: String): String {
    if (Path.of(fileName).normalize() != Path.of(fileName)) {
        throw AccessDeniedException("Illegal characters found in filename")
    }
    return fileName.replace(Regex(FILENAME_REPLACE_PATTERN), "-")
}
