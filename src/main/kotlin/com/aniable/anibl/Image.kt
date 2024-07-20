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

import jakarta.persistence.*
import jakarta.persistence.GenerationType.IDENTITY
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "abl_images")
@EntityListeners(AuditingEntityListener::class)
class Image(
	@Id @GeneratedValue(strategy = IDENTITY) val id: Int? = null,
	@Column(name = "image_id", nullable = false, unique = true) val imageId: String,
	@Column(name = "content_type") val contentType: String?,
	@Column(name = "uploaded_at") @CreationTimestamp val uploadedAt: Instant? = null,
)
