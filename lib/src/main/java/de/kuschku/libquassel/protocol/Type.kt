package de.kuschku.libquassel.protocol

import de.kuschku.libquassel.protocol.primitive.serializer.*
import java.util.*


enum class Type(val id: kotlin.Int, val serializer: Serializer<*>? = null) {
  Void(0, VoidSerializer),
  Bool(1, BoolSerializer),
  Int(2, IntSerializer),
  UInt(3, IntSerializer),
  LongLong(4),
  ULongLong(5),

  Double(6),
  QChar(7, CharSerializer),
  QVariantMap(8, VariantMapSerializer),
  QVariantList(9, VariantListSerializer),

  QString(10, StringSerializer.UTF16),
  QStringList(11, StringListSerializer),
  QByteArray(12, ByteArraySerializer),

  QBitArray(13),
  QDate(14),
  QTime(15, TimeSerializer),
  QDateTime(16, DateTimeSerializer),
  QUrl(17),

  QLocale(18),
  QRect(19),
  QRectF(20),
  QSize(21),
  QSizeF(22),

  QLine(23),
  QLineF(24),
  QPoint(25),
  QPointF(26),
  QRegExp(27),

  QVariantHash(28),
  QEasingCurve(29),

  FirstGuiType(63),

  QFont(64),
  QPixmap(65),
  QBrush(66),
  QColor(67),
  QPalette(68),

  QIcon(69),
  QImage(70),
  QPolygon(71),
  QRegion(72),
  QBitmap(73),

  QCursor(74),
  QSizePolicy(75),
  QKeySequence(76),
  QPen(77),

  QTextLength(78),
  QTextFormat(79),
  QMatrix(80),
  QTransform(81),

  QMatrix4x4(82),
  QVector2D(83),
  QVector3D(84),
  QVector4D(85),

  QQuaternion(86),

  VoidStar(128),
  Long(129, LongSerializer),
  Short(130, ShortSerializer),
  Char(131, ByteSerializer),
  ULong(132, LongSerializer),

  UShort(133, ShortSerializer),
  UChar(134, ByteSerializer),
  Float(135),
  QObjectStar(136),
  QWidgetStar(137),

  QVariant(138, VariantSerializer),

  User(256),
  UserType(127),
  LastType(-1);

  val serializableName
    get() = if (name.startsWith("Q")) {
      name
    } else {
      name.toLowerCase(Locale.ENGLISH)
    }

  companion object {
    private val byId = Type.values().associateBy(Type::id)
    fun of(type: kotlin.Int) = byId[type]
  }
}
