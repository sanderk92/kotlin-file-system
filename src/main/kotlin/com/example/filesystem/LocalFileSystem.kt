package com.example.filesystem

import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolute
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.FileUrlResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val FILES_ENDPOINT = "files"

/**
 * This local file system is purely intended for development/test purposes. Files are stored in a temporary
 * directory which is removed after system restart, exposed under a public endpoint and links generated
 * to this public endpoint. It is a simple and free alternative to s3 signed urls.
 */
@Service
@ConditionalOnProperty("feature.dev.local-file-system", havingValue = "true")
internal class LocalFileSystem : FileSystem, WebMvcConfigurer {
    private val baseStoragePath: Path = Files.createTempDirectory("files-").absolute()

    override fun write(key: String, content: InputStream): String {
        copy(content, baseStoragePath.resolve(key))
        return key
    }

    override fun delete(key: String) {
        deleteAll(baseStoragePath.resolve(key))
    }

    override fun createLink(key: String, minutes: Long): URL {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .pathSegment(FILES_ENDPOINT)
            .pathSegment(key)
            .build()
            .toUri()
            .toURL()
    }

    override fun exists(key: String): Boolean {
        return Files.exists(baseStoragePath.resolve(key))
    }

//  Required when using spring security
//    @Bean
//    fun webSecurityCustomizer(): WebSecurityCustomizer {
//        return WebSecurityCustomizer { web ->
//            web.ignoring().requestMatchers("/$FILES_ENDPOINT/**")
//        }
//    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/$FILES_ENDPOINT/**")
            .addResourceLocations("file:$baseStoragePath")
            .setCachePeriod(0)
            .resourceChain(true)
            .addResolver(customPathResolver())
    }

    private fun customPathResolver() =
        object : PathResourceResolver() {
            override fun getResource(resourcePath: String, location: Resource): Resource {
                return FileUrlResource("${location.file.path}/$resourcePath")
            }
        }
}

private fun copy(inputStream: InputStream, path: Path) {
    inputStream.use {
        Files.createDirectories(path.parent)
        Files.copy(it, path)
    }
}

private fun deleteAll(path: Path) {
    Files.walk(path).use { fileWalk ->
        fileWalk
            .sorted(Comparator.reverseOrder())
            .forEach(Files::delete)
    }
}
