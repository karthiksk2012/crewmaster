package com.crewmaster.challenge.exceptions;

public class InvalidDataException extends RuntimeException {

	private static final long serialVersionUID = -3592286308624001571L;

	public InvalidDataException(Exception e) {
        super(e);
    }

    public InvalidDataException(String message) {
        super(message);
    }
}
