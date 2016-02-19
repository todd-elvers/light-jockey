import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.status.NopStatusListener
import te.light_jockey.misc.NewlineOrCarriageReturnLayout

import static ch.qos.logback.classic.Level.*

statusListener(NopStatusListener)

String filename = 'light-jockey'
String logDir = "./data/logs"

appender("CONSOLE", ConsoleAppender) {
    encoder(LayoutWrappingEncoder) {
        layout(NewlineOrCarriageReturnLayout)
    }
}

root(INFO, ["CONSOLE"])