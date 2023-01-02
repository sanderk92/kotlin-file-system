package com.example.filesystem

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path

class LocalFileSystem(props: LocalFileSystemProperties) : FileSystem {

    private val baseStoragePath: Path = Path.of(props.baseStoragePath)

    override fun write(inputStream: InputStream, targetPath: Path): FileSystemResult {
        val target = baseStoragePath.resolve(targetPath)

        if (Files.exists(target)) return InputError("The target path already exists")

        return fileSystemResult(targetPath) { copy(inputStream, target) }
    }

    override fun read(sourcePath: Path, outputStream: OutputStream): FileSystemResult {
        val source = baseStoragePath.resolve(sourcePath)

        if (!Files.exists(source)) return InputError("The source path does not exist")

        return fileSystemResult(sourcePath) { copy(source, outputStream) }
    }

    override fun delete(sourcePath: Path): FileSystemResult {
        val source = baseStoragePath.resolve(sourcePath)

        if (!Files.exists(source)) return InputError("The source path does not exist")

        return fileSystemResult(sourcePath) { deleteAll(source) }
    }
}

private fun copy(inputStream: InputStream, path: Path) {
    inputStream.use {
        Files.createDirectories(path.parent)
        Files.copy(it, path)
    }
}

private fun copy(path: Path, outputStream: OutputStream) {
    outputStream.use {
        Files.copy(path, outputStream)
    }
}

private fun deleteAll(path: Path) {
    Files.walk(path).use { fileWalk ->
        fileWalk
            .sorted(Comparator.reverseOrder())
            .forEach(Files::delete)
    }
}
