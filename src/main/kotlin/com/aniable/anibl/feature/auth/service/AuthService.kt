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

package com.aniable.anibl.feature.auth.service

import com.aniable.anibl.Result
import com.aniable.anibl.feature.auth.AuthError
import com.aniable.anibl.feature.auth.AuthPayload
import com.aniable.anibl.feature.auth.entity.UserEntity

interface AuthService {

	fun register(payload: AuthPayload.UsernamePassword): Result<UserEntity, AuthError>
	fun login(payload: AuthPayload.UsernamePassword): Result<UserEntity, AuthError>
}
