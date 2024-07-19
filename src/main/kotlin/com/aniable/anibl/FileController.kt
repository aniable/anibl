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

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class FileController(private val imageService: ImageService) {

	@PostMapping(
		"/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE]
	)
	fun uploadImage(@RequestParam file: MultipartFile): ResponseEntity<String> {
		val fileId = imageService.uploadImage(file)
		return ResponseEntity.ok(fileId)
	}

	@GetMapping("/{name}", produces = [MediaType.IMAGE_JPEG_VALUE])
	fun getImage(@PathVariable name: String): ResponseEntity<ByteArray> {
		return ResponseEntity.ok(imageService.getImage(name))
	}
}
