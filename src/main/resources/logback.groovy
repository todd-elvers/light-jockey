import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.status.NopStatusListener

import static ch.qos.logback.classic.Level.DEBUG

statusListener(NopStatusListener)

String filename = 'light-jockey'
String logDir = "./data/logs"

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%msg%n"
    }
}

root(DEBUG, ["CONSOLE"])