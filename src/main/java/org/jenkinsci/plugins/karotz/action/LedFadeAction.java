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

import java.util.HashMap;
import java.util.Map;

/**
 * Led Light Action.
 * 
 * @author Seiji Sogabe
 */
public class LedFadeAction extends KarotzAction {

	private final String color;

	private final long period;

	public static KarotzAction getAction(String[] parameter)
			throws InvalidActionParamtersException {
		if (parameter.length != 2) {
			throw new InvalidActionParamtersException("Need two parameters",
					Actions.FADELIGHT);
		}

		String color = parameter[0];
		Long period;
		try {
			period = new Long(parameter[1]);
		} catch (NumberFormatException e) {
			throw new InvalidActionParamtersException(
					"Expecting a number, got " + parameter[1] + " instead.",
					Actions.FADELIGHT);
		}
		return new LedFadeAction(color, period);
	}

	public LedFadeAction(String color, long period) {
		this.color = color;
		this.period = period;
	}

	public LedFadeAction(LedColor color, long period) {
		this(color.getCode(), period);
	}

	@Override
	public String getBaseUrl() {
		return "http://api.karotz.com/api/karotz/led";
	}

	@Override
	public Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "fade");
		params.put("color", color);
		params.put("period", String.valueOf(period));
		return params;
	}

	@Override
	public long getDuration() {
		return period;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + (int) (period ^ (period >>> 32));
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
		LedFadeAction other = (LedFadeAction) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (period != other.period)
			return false;
		return true;
	}

}
