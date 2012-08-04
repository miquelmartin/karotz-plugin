package org.jenkinsci.plugins.karotz.dance;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.jenkinsci.plugins.karotz.action.EarAction;
import org.jenkinsci.plugins.karotz.action.KarotzAction;
import org.jenkinsci.plugins.karotz.action.LedFadeAction;
import org.jenkinsci.plugins.karotz.action.LedLightAction;
import org.jenkinsci.plugins.karotz.action.LedOffAction;
import org.jenkinsci.plugins.karotz.action.SpeakAction;
import org.jenkinsci.plugins.karotz.action.WaitAction;
import org.junit.Before;
import org.junit.Test;

public class TestDanceParser {

	private VisibleParser parser;

	class VisibleParser {
		public String[] parseParams(String dance, int start, int end)
				throws MalformedDanceException {
			return DanceParser.getActionParameters(dance, start, end);
		}
	};

	@Before
	public void setup() {
		parser = new VisibleParser();
	}

	@Test
	public void testParseParameters1() throws MalformedDanceException {
		String dance = "a,b,c";
		String[] actual = parser.parseParams(dance, 0, dance.length());
		String[] expected = new String[] { "a", "b", "c" };
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testParseParameters2() throws MalformedDanceException {
		String dance = "111,222,333";
		String[] actual = parser.parseParams(dance, 0, dance.length());
		String[] expected = new String[] { "111", "222", "333" };
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testParseParameters3() throws MalformedDanceException {
		String dance = "111,222,";
		String[] actual = parser.parseParams(dance, 0, dance.length());
		String[] expected = new String[] { "111", "222", "" };
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testParseParameters4() throws MalformedDanceException {
		String dance = "111,,333";
		String[] actual = parser.parseParams(dance, 0, dance.length());
		String[] expected = new String[] { "111", "", "333" };
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testParseParameters5() throws MalformedDanceException {
		String dance = ",222,333";
		String[] actual = parser.parseParams(dance, 0, dance.length());
		String[] expected = new String[] { "", "222", "333" };
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testParseParameters6() throws MalformedDanceException {
		String dance = "";
		String[] actual = parser.parseParams(dance, 0, dance.length());
		String[] expected = new String[0];
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testParseWithQuotes1() throws MalformedDanceException {
		String dance = "111,\"222,333\",444";
		String[] actual = parser.parseParams(dance, 0, dance.length());
		String[] expected = new String[] { "111", "222,333", "444" };
		assertArrayEquals(expected, actual);
	}

	@Test(expected = MalformedDanceException.class)
	public void testParseWithQuotes2() throws MalformedDanceException {
		String dance = "111,\"222,333,444";
		parser.parseParams(dance, 0, dance.length());
	}

	@Test
	public void testParseWithQuotes3() throws MalformedDanceException {
		String dance = "\"111\",222,333,444";
		String[] actual = parser.parseParams(dance, 0, dance.length());
		String[] expected = new String[] { "111", "222", "333", "444" };
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testParseWithQuotes4() throws MalformedDanceException {
		String dance = "111,222,333,\"444\"";
		String[] actual = parser.parseParams(dance, 0, dance.length());
		String[] expected = new String[] { "111", "222", "333", "444" };
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testParseWithQuotes5() throws MalformedDanceException {
		String dance = "111,\"\",333,444";
		String[] actual = parser.parseParams(dance, 0, dance.length());
		String[] expected = new String[] { "111", "", "333", "444" };
		assertArrayEquals(expected, actual);
	}

	@Test
	public void testDanceParseEars1() throws MalformedDanceException {
		List<KarotzAction> actual = DanceParser.parse("EARS(1,2,true), EARS()");
		List<KarotzAction> expected = new LinkedList<KarotzAction>();
		expected.add(new EarAction(1, 2, true));
		expected.add(new EarAction());
		assertEquals(expected, actual);
	}

	@Test(expected = MalformedDanceException.class)
	public void testDanceParseEars2() throws MalformedDanceException {
		DanceParser.parse("EARS(a,2,true), EARS()");

	}

	@Test(expected = MalformedDanceException.class)
	public void testDanceParseEars3() throws MalformedDanceException {
		DanceParser.parse("EARS(1,b,true), EARS()");
	}

	@Test
	public void testDanceParseLightOff() throws MalformedDanceException {
		List<KarotzAction> actual = DanceParser.parse("LIGHTOFF()");
		List<KarotzAction> expected = new LinkedList<KarotzAction>();
		expected.add(new LedOffAction());
		assertEquals(expected, actual);
	}

	@Test
	public void testDanceParseLightUp() throws MalformedDanceException {
		List<KarotzAction> actual = DanceParser.parse("LIGHTUP(00FF22)");
		List<KarotzAction> expected = new LinkedList<KarotzAction>();
		expected.add(new LedLightAction("00FF22"));
		assertEquals(expected, actual);
	}

	@Test
	public void testDanceParseFade() throws MalformedDanceException {
		List<KarotzAction> actual = DanceParser.parse("FADELIGHT(00FF22,10)");
		List<KarotzAction> expected = new LinkedList<KarotzAction>();
		expected.add(new LedFadeAction("00FF22", 10));
		assertEquals(expected, actual);
	}

	@Test(expected = MalformedDanceException.class)
	public void testDanceParseFade2() throws MalformedDanceException {
		DanceParser.parse("FADELIGHT(00FF22,onion)");
	}

	@Test
	public void testDanceParseWait() throws MalformedDanceException {
		List<KarotzAction> actual = DanceParser.parse("WAIT(1000)");
		List<KarotzAction> expected = new LinkedList<KarotzAction>();
		expected.add(new WaitAction(1000));
		assertEquals(expected, actual);
	}

	@Test
	public void testDanceParseSpeak() throws MalformedDanceException {
		List<KarotzAction> actual = DanceParser
				.parse("SPEAK(hello there how are you,EN)");
		List<KarotzAction> expected = new LinkedList<KarotzAction>();
		expected.add(new SpeakAction("hello there how are you", "EN"));
		assertEquals(expected, actual);
	}

	@Test
	public void testDanceParseSpeak2() throws MalformedDanceException {
		List<KarotzAction> actual = DanceParser
				.parse("SPEAK(\"hello there. How are you\",EN)");
		List<KarotzAction> expected = new LinkedList<KarotzAction>();
		expected.add(new SpeakAction("hello there. How are you", "EN"));
		assertEquals(expected, actual);
	}

	@Test
	public void testDanceParseSpeak3() throws MalformedDanceException {
		List<KarotzAction> actual = DanceParser
				.parse("SPEAK(\"hello there. How are you\",Klingon)");
		List<KarotzAction> expected = new LinkedList<KarotzAction>();
		expected.add(new SpeakAction("hello there. How are you", "Klingon"));
		assertEquals(expected, actual);
	}

	@Test
	public void testDanceParseGeneral1() throws MalformedDanceException {
		List<KarotzAction> actual = DanceParser
				.parse("SPEAK(\"hello there. How are you\",Klingon),WAIT(10),EARS(20,20,true)");
		List<KarotzAction> expected = new LinkedList<KarotzAction>();
		expected.add(new SpeakAction("hello there. How are you", "Klingon"));
		expected.add(new WaitAction(10));
		expected.add(new EarAction(20, 20, true));
		assertEquals(expected, actual);
	}

	@Test(expected = MalformedDanceException.class)
	public void testDanceParseGeneral2() throws MalformedDanceException {
		DanceParser
				.parse("SPEAK(\"hello there. How are you\",Klingon),WAIT(10),EARS(20,20,true");
	}

	@Test(expected = MalformedDanceException.class)
	public void testDanceParseGeneral3() throws MalformedDanceException {
		DanceParser
				.parse("SPEAK(\"hello there. How are you,Klingon),WAIT(10),EARS(20,20,true)");
	}
}
