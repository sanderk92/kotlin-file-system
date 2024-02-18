package com.example.filesystem

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(S3FileSystem::class)
@EnableConfigurationProperties(S3FileSystemProperties::class)
internal class S3FileSystemConfig(private val props: S3FileSystemProperties) {
    @Bean
    fun amazonS3(): AmazonS3 =
        AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(props.accessKey, props.secretKey)))
            .withRegion(props.region)
            .build()
}
