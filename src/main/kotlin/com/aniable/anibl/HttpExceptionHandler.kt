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

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestControllerAdvice

@Suppress("unused")
@RestControllerAdvice
@Controller
class HttpExceptionHandler : ErrorController {

	@ExceptionHandler(HttpException::class)
	fun handleException(exception: HttpException): ResponseEntity<ErrorResponseBody> {
		return ResponseEntity.status(exception.status).body(
			ErrorResponseBody(exception.status.value(), exception.getReason())
		)
	}

	@RequestMapping("/error")
	fun handleError(request: HttpServletRequest): ResponseEntity<ErrorResponseBody> {
		val httpStatus = try {
			val statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString().toInt()
			HttpStatus.valueOf(statusCode)
		} catch (e: Exception) {
			throw HttpException.InternalServerError()
		}
		return ResponseEntity.status(httpStatus).body(
			ErrorResponseBody(httpStatus.value(), httpStatus.reasonPhrase)
		)
	}
}
