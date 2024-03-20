package cn.rollin.exception;

public class CodegenException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CodegenException(String message){
		super(message);
	}

	public CodegenException(Throwable cause)
	{
		super(cause);
	}

	public CodegenException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
