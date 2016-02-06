import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.status.OnConsoleStatusListener
import te.light_jockey.logging.NewlineOrCarriageReturnLayout

import static ch.qos.logback.classic.Level.*

statusListener(OnConsoleStatusListener)

String filename = 'light-jockey'
String logDir = "./data/logs"

appender("CONSOLE", ConsoleAppender) {
    encoder(LayoutWrappingEncoder) {
        layout(NewlineOrCarriageReturnLayout)
//        pattern = "%d{MM-dd-yyyy HH:mm:ss} [%-5level] %logger{36}:%L - %msg%n"
    }
}

//appender("FILE", RollingFileAppender) {
//    file = "${logDir}/${filename}.log"
//    append = true
//    encoder(PatternLayoutEncoder) {
//        pattern = "%d{MM-dd-yyyy HH:mm:ss} [%-5level] %logger{36}:%L - %msg%n"
//    }
//    rollingPolicy(TimeBasedRollingPolicy) {
//        FileNamePattern = "${logDir}/${filename}-%d.log.gz"
//    }
//}

//root(ERROR, ["FILE", "CONSOLE"])
root(INFO, ["CONSOLE"])