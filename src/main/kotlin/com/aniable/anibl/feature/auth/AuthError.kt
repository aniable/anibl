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

package com.aniable.anibl.feature.auth

import com.aniable.anibl.error.HttpException
import com.aniable.anibl.feature.auth.entity.UserConstraints

sealed class AuthError(val message: String) {

	class UserExists(message: String = "That username is taken.") : AuthError(message)

	class UserDoesNotExist(message: String = "User does not exist.") : AuthError(message)

	class InvalidLogin(message: String = "Your username or password was incorrect.") : AuthError(message)

	class InvalidUsernameLength(message: String = "Usernames must be at least ${UserConstraints.USERNAME_MIN_LENGTH} and at most ${UserConstraints.USERNAME_MAX_LENGTH} characters.") :
		AuthError(message)

	class InvalidPasswordLength(message: String = "Passwords must be at least ${UserConstraints.PASSWORD_MIN_LENGTH} characters.") :
		AuthError(message)

	class Unknown(message: String = "Unknown error.") : AuthError(message)
}

fun AuthError.handle(): Nothing = throw when (this) {
	is AuthError.InvalidLogin -> HttpException.BadRequest(this.message)
	is AuthError.InvalidPasswordLength -> HttpException.BadRequest(this.message)
	is AuthError.InvalidUsernameLength -> HttpException.BadRequest(this.message)
	is AuthError.Unknown -> HttpException.InternalServerError(this.message)
	is AuthError.UserDoesNotExist -> HttpException.NotFound(this.message)
	is AuthError.UserExists -> HttpException.Conflict(this.message)
}
