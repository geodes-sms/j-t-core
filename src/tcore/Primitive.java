package tcore;

/**
 * Base class of every T-Core's primitive.
 *
 * @author Pierre-Olivier Talbot
 * @version 0.5
 * @since 2017-12-08
 */
public abstract class Primitive {
	protected boolean isSuccess = false;
	protected Exception exception;

	// TODO: 2017-12-08 Implement this.
	// public void cancelIn(Cancel c);

	/**
	 * @return
	 */
	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * @return
	 */
	public Exception getException() {
		return exception;
	}

}