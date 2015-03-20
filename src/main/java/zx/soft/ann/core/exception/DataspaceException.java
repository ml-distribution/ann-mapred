package zx.soft.ann.core.exception;

public abstract class DataspaceException extends ProcessException {

	private static final long serialVersionUID = 2336933304983333809L;

	public DataspaceException(String message) {
		super(message);
	}

	public DataspaceException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
