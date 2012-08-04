package org.jenkinsci.plugins.karotz.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum Actions {
	EARS("EARS", EarAction.class), SPEAK("SPEAK", SpeakAction.class), LIGHTUP(
			"LIGHTUP", LedLightAction.class), LIGHTOFF("LIGHTOFF",
			LedOffAction.class), FADELIGHT("FADELIGHT", LedFadeAction.class), WAIT(
			"WAIT", WaitAction.class);

	private final String command;
	private Class<? extends KarotzAction> classs;

	Actions(String command, Class<? extends KarotzAction> classs) {
		this.command = command;
		this.classs = classs;
	}

	/**
	 * Return the Action that actionStr <i>starts</i> with, ignoring the case.
	 * 
	 * @param actionStr
	 */
	public static Actions getAction(String actionStr)
			throws UnknownActionException {
		return getAction(actionStr, 0);
	}

	/**
	 * Return the Action that actionStr <i>starts</i> with at the given offset,
	 * ignoring the case.
	 * 
	 * @param actionStr
	 */
	public static Actions getAction(String actionStr, int offset)
			throws UnknownActionException {
		String ucActionStr = actionStr.toUpperCase();
		for (Actions action : Actions.values()) {
			if (ucActionStr.startsWith(action.command, offset)) {
				return action;
			}
		}
		throw new UnknownActionException(actionStr.substring(offset));
	}

	public KarotzAction getActionObject(String[] params)
			throws InvalidActionParamtersException {
		Method m;
		try {
			m = classs.getDeclaredMethod("getAction", String[].class);
		} catch (NoSuchMethodException e) {
			throw new AssertionError(classs + " must implement getAction. "
					+ e.getMessage());
		} catch (SecurityException e) {
			throw new AssertionError("Method getAction must be public in "
					+ classs + ". " + e.getMessage());
		}

		try {
			return (KarotzAction) m.invoke(null, (Object) params);
		} catch (IllegalAccessException e) {
			throw new AssertionError("Method getAction must be public in "
					+ classs + ". " + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new AssertionError("getAction must take a String[] in "
					+ classs + ". " + e.getMessage());
		} catch (InvocationTargetException e) {
			throw (InvalidActionParamtersException) e.getCause();
		}
	}

	public int getCommandLength() {
		return command.length();
	}
}
