package org.jenkinsci.plugins.karotz.action;

public class UnknownActionException extends Exception {

	private static final long serialVersionUID = 6220377316973759584L;

	public UnknownActionException(String action) {
		super("Unknown action: '" + action + "'");
	}
}
