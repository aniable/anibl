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

package com.aniable.anibl.error

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MultipartException
import software.amazon.awssdk.services.s3.model.NoSuchKeyException

@RestControllerAdvice
class GlobalExceptionHandler(private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)) {

	/**
	 * Catch all exceptions that are not caught by another handler.
	 */
	@ExceptionHandler(Exception::class)
	fun handleGenericException(req: HttpServletRequest, e: Exception): ResponseEntity<ExceptionResponse> {
		logger.warn(e.stackTraceToString())

		val httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
		val response = ExceptionResponse(
			statusCode = httpStatus.value(),
			statusReason = httpStatus.reasonPhrase,
			path = req.requestURI,
			message = e.message
		)
		return ResponseEntity.status(httpStatus).body(response)
	}

	@ExceptionHandler(NoSuchKeyException::class)
	fun handleNoSuchKeyException(
		req: HttpServletRequest,
		e: NoSuchKeyException,
	): ResponseEntity<ExceptionResponse> {
		val httpStatus = HttpStatus.NOT_FOUND
		val response = ExceptionResponse(
			statusCode = httpStatus.value(),
			statusReason = httpStatus.reasonPhrase,
			path = req.requestURI,
			message = e.message
		)
		return ResponseEntity.status(httpStatus).body(response)
	}

	@ExceptionHandler(MultipartException::class)
	fun handleMultipartException(
		req: HttpServletRequest,
		e: MultipartException,
	): ResponseEntity<ExceptionResponse> {
		val httpStatus = HttpStatus.PAYLOAD_TOO_LARGE
		val response = ExceptionResponse(
			statusCode = httpStatus.value(),
			statusReason = httpStatus.reasonPhrase,
			path = req.requestURI,
			message = e.message
		)
		return ResponseEntity.status(httpStatus).body(response)
	}
}
