package org.jenkinsci.plugins.karotz.action;

public class InvalidActionParamtersException extends Exception {

	private static final long serialVersionUID = 6220377316973759584L;

	public InvalidActionParamtersException(String message, Actions action) {
		super("Invalid parameters for " + action + ": " + message);
	}
}
