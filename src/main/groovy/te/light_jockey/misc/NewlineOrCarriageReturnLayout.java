package te.light_jockey.misc;

import org.apache.commons.lang3.StringUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;

public class NewlineOrCarriageReturnLayout extends LayoutBase<ILoggingEvent> {

    private static final int CARRIAGE_RETURN_MESSAGE_PADDING_LENGTH = 50;
    private static final String CARRIAGE_RETURN = "\r";
    private static final String ELLIPSIS = "...";

    private boolean lastLogMessageContainedCarriageReturn = false;
    /**
     * <p>This layout either simply returns the log message untouched, or it returns the log message
     * with a newline character appended to it.
     *
     * <p><b>1st Case:</b> If a log message <b>does not</b> start with a carriage return <b>or</b> end
     * with an ellipsis, then the log message is returned with a newline character appended to it.
     *
     * <p><b>2nd Case:</b> If a log message starts with a carriage return or ends with an ellipsis,
     * then the log message is returned from this method with a right-padding of 50 spaces to replace
     * any remnants of previous log messages that may have been longer in length than the current
     * log message.
     *
     * @param event the logging event
     * @return the message that will be passed to the logger
     */
    public String doLayout(ILoggingEvent event) {
        if(event.getFormattedMessage().startsWith(CARRIAGE_RETURN)) {
            return StringUtils.rightPad(event.getFormattedMessage(), CARRIAGE_RETURN_MESSAGE_PADDING_LENGTH, " ");
        } else if(event.getFormattedMessage().endsWith(ELLIPSIS)) {
            return event.getFormattedMessage();
        } else {
            String prefix = lastLogMessageContainedCarriageReturn ? " " : "";
            lastLogMessageContainedCarriageReturn = false;
            return prefix + event.getFormattedMessage() + CoreConstants.LINE_SEPARATOR;
        }
    }
}
