package com.example.filesystem

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path

interface FileSystem {

    /**
     * Writes the specified [InputStream] to specified [Path].
     *
     * @param inputStream to read from
     * @param targetPath to write to
     */
    fun write(inputStream: InputStream, targetPath: Path): FileSystemResult

    /**
     * Read the specified [Path] to the specified [OutputStream].
     *
     * @param sourcePath to read from
     * @param outputStream to write to
     */
    fun read(sourcePath: Path, outputStream: OutputStream): FileSystemResult

    /**
     * Deletes the file at the specified [Path]
     *
     * @param sourcePath Path of the file to delete
     */
    fun delete(sourcePath: Path): FileSystemResult
}
