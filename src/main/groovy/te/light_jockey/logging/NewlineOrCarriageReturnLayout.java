package te.light_jockey.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;

public class NewlineOrCarriageReturnLayout extends LayoutBase<ILoggingEvent> {

    private static final String CARRIAGE_RETURN = "\r";
    private static final String ELLIPSIS = "...";

    /**
     * <p>This layout either simply returns the log message untouched, or it returns the log message
     * with a newline character appended to it.
     *
     * <p><b>1st Case:</b> If a log message <b>does not</b> start with a carriage return <b>or</b> end
     * with an ellipsis, then the log message is returned with a newline character appended to it.
     *
     * <p><b>2nd Case:</b> If a log message starts with a carriage return or ends with an ellipsis,
     * then the log message is returned from this message unmodified.
     *
     * @param event the logging event
     * @return the message that will be passed to the logger
     */
    public String doLayout(ILoggingEvent event) {
        if(event.getFormattedMessage().startsWith(CARRIAGE_RETURN) || event.getFormattedMessage().endsWith(ELLIPSIS)) {
            return event.getFormattedMessage();
        } else {
            return event.getFormattedMessage() + CoreConstants.LINE_SEPARATOR;
        }
    }
}
