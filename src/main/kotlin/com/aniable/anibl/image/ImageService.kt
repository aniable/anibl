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

package com.aniable.anibl.image

import com.aniable.anibl.ext.isValidContentType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Service
class ImageService(
	private val s3Client: S3Client,
	private val imageRepository: ImageRepository,
	@Value("\${AWS_BUCKET}") private val bucketName: String,
) {

	@Transactional
	fun uploadImage(file: MultipartFile): Image {
		if (file.isEmpty) throw RuntimeException("File is empty.")
		if (!file.isValidContentType) throw RuntimeException("File is not a valid media type.")

		val imageId = UUID.randomUUID().toString()
		val request = PutObjectRequest.builder().bucket(bucketName).key(imageId).build()

		s3Client.putObject(request, RequestBody.fromBytes(file.bytes))
		return imageRepository.save(Image(imageId = imageId, contentType = file.contentType))
	}

	fun getImage(imageId: String): Pair<Image?, ByteArray> {
		val request = GetObjectRequest.builder().bucket(bucketName).key(imageId).build()
		val image = imageRepository.findByImageId(imageId)
		val bytes = s3Client.getObject(request).readAllBytes()
		return Pair(image, bytes)
	}

	fun deleteImage(imageId: String) {
		val request = DeleteObjectRequest.builder().bucket(bucketName).key(imageId).build()
		s3Client.deleteObject(request)
	}
}
