package org.jenkinsci.plugins.karotz.dance;

public class MalformedDanceException extends Exception {
	private static final long serialVersionUID = -2949162661286440234L;

	public MalformedDanceException(String message, String dance, int pos) {
		super(message + " (near character " + pos + " in '"
				+ dance.substring(0, pos) + "^" + dance.substring(pos) + "')");
	}
}
