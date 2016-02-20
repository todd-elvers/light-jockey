package te.light_jockey.misc;

import org.apache.commons.lang3.StringUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;

public class NewlineOrCarriageReturnLayout extends LayoutBase<ILoggingEvent> {

    private static final int CARRIAGE_RETURN_MESSAGE_PADDING_LENGTH = 50;
    private static final String CARRIAGE_RETURN = "\r";
    private static final String ELLIPSIS = "...";

    private boolean prevLogMessageStartedWithCarriageReturn = false;

    /**
     * <p>This layout either simply returns the log message untouched, or it returns the log message
     * with a newline character appended to it.
     *
     * <p><b>1st Case:</b> If a log message <b>does not</b> start with a carriage return <b>or</b>
     * end with an ellipsis, then the log message is returned with a newline character appended to
     * it.
     *
     * <p><b>2nd Case:</b> If a log message starts with a carriage return or ends with an ellipsis,
     * then the log message is returned from this method with a right-padding of 50 spaces to
     * replace any remnants of previous log messages that may have been longer in length than the
     * current log message.
     *
     * @param event the logging event
     * @return the message that will be passed to the logger
     */
    public String doLayout(ILoggingEvent event) {
        String logMessage = event.getFormattedMessage();

        if (logMessage.startsWith(CARRIAGE_RETURN)) {
            prevLogMessageStartedWithCarriageReturn = true;
            return StringUtils.rightPad(logMessage, CARRIAGE_RETURN_MESSAGE_PADDING_LENGTH, " ");
        } else {
            String prefix = determineLogMessagePrefix();
            prevLogMessageStartedWithCarriageReturn = false;
            if (logMessage.endsWith(ELLIPSIS)) {
                return prefix + logMessage;
            } else {
                return prefix + logMessage + CoreConstants.LINE_SEPARATOR;
            }
        }
    }

    /**
     * This exists because if the previous log message started with a /r then the next
     * log message needs to start with a newline character, otherwise the previous
     * log message's carriage return will 'consume' the first character of the next message.
     *
     * @return either a newline character or the empty string
     */
    private String determineLogMessagePrefix() {
        return prevLogMessageStartedWithCarriageReturn ? "\n" : "";
    }
}
