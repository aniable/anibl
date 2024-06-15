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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.aniable.anibl

import org.springframework.http.HttpStatus

sealed class HttpException(val status: HttpStatus, override val message: String? = null) : Exception(message) {

	/**
	 * See: [400 Bad Request on MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/400)
	 */
	class BadRequest(message: String? = null) : HttpException(HttpStatus.BAD_REQUEST, message)

	/**
	 * See: [401 Unauthorized on MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/401)
	 */
	class Unauthorized(message: String? = null) : HttpException(HttpStatus.UNAUTHORIZED, message)

	/**
	 * See: [403 Forbidden on MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/403)
	 */
	class Forbidden(message: String? = null) : HttpException(HttpStatus.FORBIDDEN, message)

	/**
	 * See: [404 Not Found on MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/404)
	 */
	class NotFound(message: String? = null) : HttpException(HttpStatus.NOT_FOUND, message)

	/**
	 * See: [409 Conflict](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/409)
	 */
	class Conflict(message: String? = null) : HttpException(HttpStatus.CONFLICT, message)

	/**
	 * See: [429 Too Many Requests on MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/429)
	 */
	class TooManyRequests(message: String? = null) : HttpException(HttpStatus.TOO_MANY_REQUESTS, message)

	/**
	 * See: [500 Internal Server Error on MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/500)
	 */
	class InternalServerError(message: String? = null) : HttpException(HttpStatus.INTERNAL_SERVER_ERROR, message)

	fun getReason(): String {
		return message ?: status.reasonPhrase
	}

	override fun toString(): String {
		return "${status.value()} ${message ?: status.reasonPhrase}"
	}
}
