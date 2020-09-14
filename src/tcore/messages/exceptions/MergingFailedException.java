package tcore.messages.exceptions;

/**
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-09
 */

@SuppressWarnings("serial")
public class MergingFailedException extends TCoreException {
    /**
     * @param message
     */
    public MergingFailedException(String message) {
        super(message);
    }
}
