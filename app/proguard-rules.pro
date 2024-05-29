# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Please add these rules to your existing keep rules in order to suppress warnings.
-dontwarn com.sun.msv.reader.GrammarReaderController
-dontwarn com.sun.msv.reader.util.IgnoreController
-dontwarn javax.xml.stream.XMLInputFactory
-dontwarn javax.xml.stream.XMLOutputFactory
-dontwarn javax.xml.stream.XMLResolver
-dontwarn org.joda.time.DateMidnight
-dontwarn org.joda.time.DateTime
-dontwarn org.joda.time.DateTimeFieldType
-dontwarn org.joda.time.DateTimeZone
-dontwarn org.joda.time.Duration
-dontwarn org.joda.time.Instant
-dontwarn org.joda.time.Interval
-dontwarn org.joda.time.LocalDate
-dontwarn org.joda.time.LocalDateTime
-dontwarn org.joda.time.LocalTime
-dontwarn org.joda.time.MonthDay
-dontwarn org.joda.time.Period
-dontwarn org.joda.time.ReadableDateTime
-dontwarn org.joda.time.ReadableInstant
-dontwarn org.joda.time.ReadablePeriod
-dontwarn org.joda.time.YearMonth
-dontwarn org.joda.time.format.DateTimeFormatter
-dontwarn org.joda.time.format.ISODateTimeFormat
-dontwarn org.joda.time.format.ISOPeriodFormat
-dontwarn org.joda.time.format.PeriodFormatter
-dontwarn org.slf4j.impl.StaticLoggerBinder