package zx.soft.ann.core.exception;

public class RepositoryException extends DataspaceException {

	private static final long serialVersionUID = 7547386405340454597L;

	public RepositoryException(String message) {
		super(message);
	}

	public RepositoryException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
