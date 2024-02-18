package com.example.filesystem

import java.io.InputStream
import java.net.URL

internal interface FileSystem {
    /**
     * Writes the specified content to specified key.
     */
    fun write(key: String, content: InputStream): String

    /**
     * Deletes the file at the specified key
     */
    fun delete(key: String)

    /**
     * Create a link to access the file with the specified key. Valid for the specified amount of minutes.
     */
    fun createLink(key: String, minutes: Long): URL

    /**
     * Check if the specified key exists
     */
    fun exists(key: String): Boolean
}
