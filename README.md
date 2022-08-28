# Kotlin File System

A generic collection of tools for local, s3 storage.

```kotlin
class MyClass(private val fileSystem: FileSystem) {

    fun myFun() {
        val path = Path.of("/myFolder/myFile.txt")

        // Writing InputStream to a specific path
        val inputStream = "content".byteInputStream()
        fileSystem.write(inputStream, path)

        // Reading content at a path to OutputStream
        val outputStream = FileOutputStream(File("otherFolder/myFile.txt"))
        fileSystem.read(path, outputStream)

        // Deleting a path
        fileSystem.delete(path)
    }
}
```
Depending on the implementation of FileSystem, the Paths are interpreted accordingly:
- LocalFileSystem stores files at the specified path in its configured base directory
- S3FileSystem uses these paths as the key for file storage