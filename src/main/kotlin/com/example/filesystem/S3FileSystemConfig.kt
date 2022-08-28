package com.example.filesystem

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3ClientBuilder

class S3FileSystemConfig(private val props: S3FileSystemProperties) {

    fun amazonS3() = AmazonS3ClientBuilder.standard()
        .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(props.accessKey, props.secretKey)))
        .withRegion(props.region)
        .build()
}
