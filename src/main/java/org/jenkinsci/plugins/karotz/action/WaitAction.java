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

import org.jenkinsci.plugins.karotz.KarotzClient;
import org.jenkinsci.plugins.karotz.KarotzException;

/**
 * Simply wait for the given number of ms before executing another action.
 * 
 * @author Miquel Martin
 */
public class WaitAction extends KarotzAction {
	/** Time to wait. */
	private final long ms;

	public static KarotzAction getAction(String[] parameter)
			throws InvalidActionParamtersException {
		if (parameter.length != 1) {
			throw new InvalidActionParamtersException(
					"This action takes exactly one parameter (the wait time in ms)",
					Actions.WAIT);
		}

		long ms;
		try {
			ms = Long.parseLong(parameter[0]);
		} catch (NumberFormatException e) {
			throw new InvalidActionParamtersException("Expected a number,  got"
					+ parameter[0] + " instead.", Actions.WAIT);
		}
		return new WaitAction(ms);
	}

	public WaitAction(long ms) {
		this.ms = ms;
	}

	@Override
	public void execute(KarotzClient client) throws KarotzException {
		synchronized (this) {
			try {
				this.wait(ms);
			} catch (InterruptedException e) {
				throw new KarotzException("Unable to wait.");
			}
		}
	}

	@Override
	public String getBaseUrl() {
		return "http://api.karotz.com/api/karotz/led";
	}

	@Override
	public Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "light");
		return params;
	}

	@Override
	public long getDuration() {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (ms ^ (ms >>> 32));
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
		WaitAction other = (WaitAction) obj;
		if (ms != other.ms)
			return false;
		return true;
	}
}
