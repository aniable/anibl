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

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import java.util.*

object UserConstraints {

	const val USERNAME_MIN_LENGTH = 2
	const val USERNAME_MAX_LENGTH = 20
	const val PASSWORD_MIN_LENGTH = 10
}

object Users : UUIDTable() {

	val username: Column<String> =
		varchar(name = "username", length = UserConstraints.USERNAME_MAX_LENGTH).uniqueIndex()
	val passwordSalt: Column<String> = varchar(name = "password_salt", length = 255)
	val passwordHash: Column<String> = varchar(name = "password_hash", length = 255)
	val apiKey: Column<String> = varchar(name = "api_key", length = 32).uniqueIndex()
	val createdAt: Column<Instant> = timestamp(name = "created_at").defaultExpression(CurrentTimestamp)
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {

	companion object : UUIDEntityClass<User>(Users)

	var username by Users.username
	var passwordSalt by Users.passwordSalt
	var passwordHash by Users.passwordHash
	var apiKey by Users.apiKey
	val createdAt by Users.createdAt
}
