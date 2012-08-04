/*
 * The MIT License
 *
 * Copyright (c) 2011, Seiji Sogabe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.karotz.action;

import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jenkinsci.plugins.karotz.KarotzException;
import org.jenkinsci.plugins.tokenmacro.MacroEvaluationException;
import org.jenkinsci.plugins.tokenmacro.TokenMacro;

/**
 * SpeakAction.
 * 
 * @author Seiji Sogabe
 */
public class SpeakAction extends KarotzAction {

	/**
	 * Number of milliseconds it takes the Karotz to pronounce one letter.
	 * Naturally, this is approximate, and is just meant to ensure the Karotz
	 * doesn't exit interactive mode mid-sentence. Note that numbers take much
	 * longer.
	 */
	private static final int LETTER_DURATION = 600;

	private String textToSpeak;

	private final String language;

	private final int duration;

	// TODO understand placeholders for funny remarks
	public static KarotzAction getAction(String[] parameter)
			throws InvalidActionParamtersException {
		if (parameter.length < 1 || parameter.length > 2) {
			throw new InvalidActionParamtersException(
					"This action takes a text parameter and an optional language.",
					Actions.SPEAK);
		}
		String textToSpeak = parameter[0];
		String language = parameter.length == 2 ? parameter[1] : "EN";
		return new SpeakAction(textToSpeak, language);
	}

	public SpeakAction(String textToSpeak) {
		this(textToSpeak, "EN");
	}

	public SpeakAction(String textToSpeak, String language) {
		this.textToSpeak = textToSpeak;
		this.language = language;
		this.duration = textToSpeak.length() * LETTER_DURATION;
	}

	@Override
	public String getBaseUrl() {
		return "http://api.karotz.com/api/karotz/tts";
	}

	@Override
	public Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "speak");
		params.put("lang", language);
		params.put("text", textToSpeak);
		return params;
	}

	@Override
	public void execute(AbstractBuild<?, ?> build, BuildListener listener)
			throws KarotzException {
		try {
			textToSpeak = TokenMacro.expandAll(build, listener, textToSpeak);
		} catch (MacroEvaluationException ex) {
			LOGGER.log(Level.WARNING, "Build variables seem to be invalid", ex);
		} catch (IOException ex) {
			LOGGER.log(Level.WARNING, "IO Error", ex);
		} catch (InterruptedException ex) {
			LOGGER.log(Level.WARNING, "Interrupted", ex);
		}
		super.execute(build, listener);
	}

	/**
	 * Logger
	 */
	protected static final Logger LOGGER = Logger.getLogger(SpeakAction.class
			.getName());

	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + duration;
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result
				+ ((textToSpeak == null) ? 0 : textToSpeak.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpeakAction other = (SpeakAction) obj;
		if (duration != other.duration)
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (textToSpeak == null) {
			if (other.textToSpeak != null)
				return false;
		} else if (!textToSpeak.equals(other.textToSpeak))
			return false;
		return true;
	}
}
