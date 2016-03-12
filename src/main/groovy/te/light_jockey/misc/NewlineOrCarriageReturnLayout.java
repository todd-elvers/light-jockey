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
     * <p>This is a special logger useful for handling useful-but-noisy logging scenarios.  The "useful" features are: <br/>
     * <ul>
     *     <li>writing a new log message to the same line the previous log message was on</li>
     *     <li>replacing the previous log message with a new log message entirely</li>
     * </ul>
     *
     * <p><h1>How it works:</h1>
     * <p><b>1st Case:</b> If a log message ends with an ellipsis, then it is returned <b>without</b>
     * a newline character appended to it.  This case allows for appending multiple log messages to the same line. <br/>
     * The next log message encountered that does not end with an ellipsis terminates this case.
     *
     * <p><b>2st Case:</b> If a log message starts with an carriage return, then it is returned <b>without</b>
     * a newline character appended to it, but instead receives a right-padding of 50 spaces.  Since this case
     * is meant to handle useful-but-noisy messages and replace the previous log message in the console, a
     * right padding is required because the previous log message may have been longer than the incoming message. <br/>
     * The next log message encountered that does not end with a carriage return terminates this case.
     *
     * <p><b>3st Case:</b> All other log messages are simply printed <b>with</b> a newline character appended to them.
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
