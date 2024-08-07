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

package com.aniable.anibl.ext

import org.springframework.web.multipart.MultipartFile

val MultipartFile.isValidContentType: Boolean
	get() = arrayOf(
		"image/jpeg", "image/png", "image/webp", "image/svg+xml", "image/gif", "video/mp4", "video/mpeg", "video/webm"
	).contains(this.contentType?.lowercase())
