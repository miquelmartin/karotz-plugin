package org.jenkinsci.plugins.karotz.dance;

import java.util.LinkedList;
import java.util.List;

import org.jenkinsci.plugins.karotz.action.Actions;
import org.jenkinsci.plugins.karotz.action.InvalidActionParamtersException;
import org.jenkinsci.plugins.karotz.action.KarotzAction;
import org.jenkinsci.plugins.karotz.action.UnknownActionException;

public class DanceParser {

	// Sample dance: EARS(10,10), SPEAK("Hi there"), FADE(004344,32)

	private static final char QUOTE = '"';
	private static final char SEPARATOR = ',';

	public static List<KarotzAction> parse(String dance)
			throws MalformedDanceException {
		int cursor = 0;
		List<KarotzAction> actionList = new LinkedList<KarotzAction>();

		// Process the dance from the end to the beginning
		while (cursor < dance.length()) {
			// Trim and remove separators
			while (dance.charAt(cursor) == ' ' || dance.charAt(cursor) == ','
					|| dance.charAt(cursor) == '\n') {
				cursor++;
			}

			Actions action;
			try {
				action = Actions.getAction(dance, cursor);
				cursor += action.getCommandLength();
			} catch (UnknownActionException e) {
				throw new MalformedDanceException(e.getMessage(), dance, cursor);
			}

			if (dance.charAt(cursor) == '(') {
				cursor++;
			} else {
				throw new MalformedDanceException("Action " + action
						+ " must be followeded by an opening parenthesis.",
						dance, cursor);
			}
			int parameterEnd = dance.indexOf(')', cursor);
			if (parameterEnd == -1) {
				throw new MalformedDanceException(
						"Missing closing parenthesis.", dance, cursor);
			}

			String[] actionParameters = getActionParameters(dance, cursor,
					parameterEnd);
			try {
				actionList.add(action.getActionObject(actionParameters));
			} catch (InvalidActionParamtersException e) {
				throw new MalformedDanceException(e.getMessage(), dance, cursor);
			}

			cursor = parameterEnd + 1;
		}
		return actionList;
	}

	protected static String[] getActionParameters(String danceStr, int start,
			int end) throws MalformedDanceException {
		List<String> parameters = new LinkedList<String>();
		int paramStart = 0;
		boolean betweenQuotes = false;

		String dance = danceStr.substring(start, end).trim();
		// Add a comma to facilitate parsing the last parameter
		if (!dance.isEmpty()) {
			dance += ",";
		}

		for (int i = 0; i < dance.length(); i++) {
			if (!betweenQuotes && dance.charAt(i) == SEPARATOR) {
				String parameter = dance.substring(paramStart, i).trim();
				if (parameter.length() > 1 && parameter.charAt(0) == '"') {
					// Remove quotation marks
					parameter = parameter.substring(1, parameter.length() - 1);
				}
				parameters.add(parameter);
				paramStart = i + 1;
			} else if (dance.charAt(i) == QUOTE) {
				betweenQuotes = !betweenQuotes;
			}
		}
		// Check closing of quotes
		if (betweenQuotes) {
			throw new MalformedDanceException(
					"Non terminated quotation mark found", dance, start);
		}
		return parameters.toArray(new String[parameters.size()]);
	}
}
