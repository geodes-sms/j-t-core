package tcore.messages.exceptions;

/**
 * The abstract ancestor of every T-Core exception.
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */

@SuppressWarnings("serial")
abstract class TCoreException extends Exception {

    public TCoreException() {
    }

    public TCoreException(String message) {
        super(message);
    }
}
