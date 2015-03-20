package zx.soft.ann.core.exception;

public class StopMapperException extends RuntimeException {

	private static final long serialVersionUID = 4293832836012331391L;

	public StopMapperException(String message) {
		super(message);
	}

	public StopMapperException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
