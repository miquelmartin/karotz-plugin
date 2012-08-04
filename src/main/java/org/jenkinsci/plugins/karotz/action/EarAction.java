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
 * @author Miquel Martin
 */
public class EarAction extends KarotzAction {

	private static final int EAR_RESET_TIME = 2500;
	private Integer right;
	private Integer left;
	private Boolean relative;
	private Boolean reset;

	public static EarAction getAction(String... parameter)
			throws InvalidActionParamtersException {
		if (parameter.length == 0) {
			return new EarAction(); // Reset
		} else {
			if (parameter.length != 3) {
				throw new InvalidActionParamtersException(
						"Either 0 or 3 parameters needed", Actions.EARS);
			}
			try {
				Integer left = new Integer(parameter[0]);
				Integer right = new Integer(parameter[1]);
				Boolean relative = new Boolean(parameter[2]);
				return new EarAction(left, right, relative);
			} catch (NumberFormatException e) {
				throw new InvalidActionParamtersException(
						"Unable to parse ear position into a number",
						Actions.EARS);
			}
		}

	}

	public EarAction() {
		this.reset = true;
	}

	/**
	 * Move the karotz ears to an absolute position.
	 * 
	 * @param left
	 *            if not null, indicates the desired position.
	 * @param right
	 *            if not null, indicates the desired position.
	 */
	public EarAction(Integer left, Integer right, boolean relative) {
		this.left = left;
		this.right = right;
		this.relative = relative;
	}

	@Override
	public String getBaseUrl() {
		return "http://api.karotz.com/api/karotz/ears";
	}

	@Override
	public Map<String, String> getParameters() {
		Map<String, String> params = new HashMap<String, String>();
		if (reset != null) {
			params.put("reset", reset.toString());
		} else {
			if (left != null) {
				params.put("left", left.toString());
			}
			if (right != null) {
				params.put("right", right.toString());
			}
			if (relative != null) {
				params.put("relative", relative.toString());
			}
		}
		return params;
	}

	@Override
	public long getDuration() {
		return EAR_RESET_TIME;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result
				+ ((relative == null) ? 0 : relative.hashCode());
		result = prime * result + ((reset == null) ? 0 : reset.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		EarAction other = (EarAction) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (relative == null) {
			if (other.relative != null)
				return false;
		} else if (!relative.equals(other.relative))
			return false;
		if (reset == null) {
			if (other.reset != null)
				return false;
		} else if (!reset.equals(other.reset))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
}
