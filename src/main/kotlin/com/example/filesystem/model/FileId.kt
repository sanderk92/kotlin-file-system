package com.example.filesystem.model

import java.util.UUID

internal data class FileId(val key: String) {
    companion object {
        fun of(id: UUID, fileName: String): FileId = FileId("$id-$fileName")
    }
}
