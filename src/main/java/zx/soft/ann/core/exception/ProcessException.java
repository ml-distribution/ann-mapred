package zx.soft.ann.core.exception;

public abstract class ProcessException extends Exception {

	private static final long serialVersionUID = -5273792590995208487L;

	public ProcessException(String message) {
		super(message);
	}

	public ProcessException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
