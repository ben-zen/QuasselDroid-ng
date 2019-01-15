/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.libquassel.protocol.primitive.serializer

import de.kuschku.libquassel.util.deserialize
import de.kuschku.libquassel.util.roundTrip
import org.junit.Assert.assertEquals
import org.junit.Test

class CharSerializerTest {
  @Test
  fun testAll() {
    assertEquals(' ', roundTrip(CharSerializer, ' '))
    assertEquals(' ', deserialize(CharSerializer, byteArrayOf(0, 32)))

    assertEquals('a', roundTrip(CharSerializer, 'a'))
    assertEquals('a', deserialize(CharSerializer, byteArrayOf(0, 97)))

    assertEquals('ä', roundTrip(CharSerializer, 'ä'))
    assertEquals('ä', deserialize(CharSerializer, byteArrayOf(0, -28)))

    assertEquals('\u0000', roundTrip(CharSerializer, '\u0000'))
    assertEquals('\u0000', deserialize(CharSerializer, byteArrayOf(0, 0)))

    assertEquals('\uFFFF', roundTrip(CharSerializer, '\uFFFF'))
    assertEquals('\uFFFF', deserialize(CharSerializer, byteArrayOf(-1, -1)))
  }
}
