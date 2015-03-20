package zx.soft.ann.core.exception;

public class HadoopException extends ProcessException {

	private static final long serialVersionUID = 2329376445753658197L;

	public HadoopException(String message) {
		super(message);
	}

	public HadoopException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
