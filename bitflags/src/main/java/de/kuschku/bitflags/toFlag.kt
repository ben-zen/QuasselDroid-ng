/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.bitflags

import java.util.*
import kotlin.experimental.and

inline fun <reified T> Flags<Byte, T>.toFlag(value: Byte?): EnumSet<T> where T: Flag<Byte>, T: Enum<T> {
  if (value == null) return emptyList<T>().toEnumSet()
  return this.all().filter { (value and it.value) != 0.toByte() }.toEnumSet()
}

inline fun <reified T> Flags<UByte, T>.toFlag(value: UByte?): EnumSet<T> where T: Flag<UByte>, T: Enum<T> {
  if (value == null) return emptyList<T>().toEnumSet()
  return this.all().filter { (value and it.value) != 0.toUByte() }.toEnumSet()
}

inline fun <reified T> Flags<Short, T>.toFlag(value: Short?): EnumSet<T> where T: Flag<Short>, T: Enum<T> {
  if (value == null) return emptyList<T>().toEnumSet()
  return this.all().filter { (value and it.value) != 0.toShort() }.toEnumSet()
}

inline fun <reified T> Flags<UShort, T>.toFlag(value: UShort?): EnumSet<T> where T: Flag<UShort>, T: Enum<T> {
  if (value == null) return emptyList<T>().toEnumSet()
  return this.all().filter { (value and it.value) != 0.toUShort() }.toEnumSet()
}

inline fun <reified T> Flags<Int, T>.toFlag(value: Int?): EnumSet<T> where T: Flag<Int>, T: Enum<T> {
  if (value == null) return emptyList<T>().toEnumSet()
  return this.all().filter { (value and it.value) != 0 }.toEnumSet()
}

inline fun <reified T> Flags<UInt, T>.toFlag(value: UInt?): EnumSet<T> where T: Flag<UInt>, T: Enum<T> {
  if (value == null) return emptyList<T>().toEnumSet()
  return this.all().filter { (value and it.value) != 0u }.toEnumSet()
}

inline fun <reified T> Flags<Long, T>.toFlag(value: Long?): EnumSet<T> where T: Flag<Long>, T: Enum<T> {
  if (value == null) return emptyList<T>().toEnumSet()
  return this.all().filter { (value and it.value) != 0L }.toEnumSet()
}

inline fun <reified T> Flags<ULong, T>.toFlag(value: ULong?): EnumSet<T> where T: Flag<ULong>, T: Enum<T> {
  if (value == null) return emptyList<T>().toEnumSet()
  return this.all().filter { (value and it.value) != 0uL }.toEnumSet()
}
