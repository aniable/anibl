/*
 * Anibl
 * Copyright (C) 2024 Aniable LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aniable.anibl

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Service
class ImageService(
	private val s3Client: S3Client,
	@Value("\${AWS_BUCKET}") private val bucketName: String,
) {

	private fun isImage(file: MultipartFile) =
		arrayOf("image/jpeg", "image/png", "image/webp").contains(file.contentType)

	fun uploadImage(file: MultipartFile): String? {
		if (!isImage(file)) throw RuntimeException("File is not an image.")

		val fileId = UUID.randomUUID().toString()
		val request = PutObjectRequest.builder().bucket(bucketName).key(fileId).build()
		s3Client.putObject(request, RequestBody.fromBytes(file.bytes))
		return fileId
	}

	fun getImage(imageName: String): ByteArray? {
		val request = GetObjectRequest.builder().bucket(bucketName).key(imageName).build()
		return s3Client.getObject(request).readAllBytes()
	}
}
