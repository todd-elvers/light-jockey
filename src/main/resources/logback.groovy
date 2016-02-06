import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.status.OnConsoleStatusListener

import static ch.qos.logback.classic.Level.*

statusListener(OnConsoleStatusListener)

String filename = 'light-jockey'
String logDir = "./data/logs"

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{MM-dd-yyyy HH:mm:ss} [%-5level] %logger{36}:%L - %msg%n"
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

logger("com.carfax.rabbit.producer", ERROR)
logger("org.springframework.core", ERROR)
logger("org.springframework.beans", ERROR)
logger("org.springframework.context", ERROR)
logger("org.springframework.web", ERROR)
logger("org.apache.xerces.parsers", ERROR)
logger("org.mortbay.log", ERROR)

root(ERROR, ["FILE", "CONSOLE"])