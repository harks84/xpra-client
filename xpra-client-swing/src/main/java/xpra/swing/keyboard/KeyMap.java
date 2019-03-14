package xpra.swing.keyboard;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * @author Jakub Księżniak
 *
 */
public class KeyMap {

	private static final Map<Integer, String> keycodesMap = new HashMap<>();
	
	static {
		//alpha keys
		for(int i = KeyEvent.VK_A; i <= KeyEvent.VK_Z; ++i) {
			map(i, i);
		}
		// numeric keys
		for(int i = KeyEvent.VK_0; i <= KeyEvent.VK_9; ++i) {
			keycodesMap.put(i, Integer.toString(i-KeyEvent.VK_0));
		}
		//??? numpad keys
		for(int i = KeyEvent.VK_NUMPAD0; i <= KeyEvent.VK_NUMPAD9; ++i) {
			keycodesMap.put(i, Integer.toString(i-KeyEvent.VK_NUMPAD0));
		}
		
		//??? function keys
		for(int i = KeyEvent.VK_F1; i <= KeyEvent.VK_F12; ++i) {
			keycodesMap.put(i, "F" + Integer.toString(i - KeyEvent.VK_F1));
		}
		
		for(int i = KeyEvent.VK_F13; i <= KeyEvent.VK_F24; ++i) {
			keycodesMap.put(i, "F" + Integer.toString(i - KeyEvent.VK_F13 + 13));
		}
		
		keycodesMap.put(KeyEvent.VK_BACK_SPACE, "BackSpace");
		keycodesMap.put(KeyEvent.VK_TAB, "Tab");
		//keycodesMap.put(12, "KP_Begin");
		keycodesMap.put(KeyEvent.VK_ENTER, "Return");
		keycodesMap.put(KeyEvent.VK_SHIFT, "Shift_L");
		keycodesMap.put(KeyEvent.VK_CONTROL, "Control_L");
		keycodesMap.put(KeyEvent.VK_ALT, "Alt_L");
		keycodesMap.put(KeyEvent.VK_PAUSE, "Pause");
		keycodesMap.put(KeyEvent.VK_CAPS_LOCK, "Caps_Lock");
		keycodesMap.put(KeyEvent.VK_ESCAPE, "Escape");
		//keycodesMap.put(31, "Mode_switch");
		keycodesMap.put(KeyEvent.VK_SPACE, "space");
		keycodesMap.put(KeyEvent.VK_PAGE_UP, "Prior");
		keycodesMap.put(KeyEvent.VK_PAGE_DOWN, "Next");
		keycodesMap.put(KeyEvent.VK_END, "End");
		keycodesMap.put(KeyEvent.VK_HOME, "Home");
		keycodesMap.put(KeyEvent.VK_LEFT, "Left");
		keycodesMap.put(KeyEvent.VK_UP, "Up");
		keycodesMap.put(KeyEvent.VK_RIGHT, "Right");
		keycodesMap.put(KeyEvent.VK_DOWN, "Down");
		keycodesMap.put(KeyEvent.VK_PRINTSCREEN, "Print");
		keycodesMap.put(KeyEvent.VK_INSERT, "Insert");
		keycodesMap.put(KeyEvent.VK_DELETE, "Delete");
		keycodesMap.put(KeyEvent.VK_COLON, "colon");
		keycodesMap.put(KeyEvent.VK_SEMICOLON, "semicolon");
		keycodesMap.put(KeyEvent.VK_LESS, "less");
		keycodesMap.put(KeyEvent.VK_EQUALS, "equal");
		keycodesMap.put(KeyEvent.VK_GREATER, "greater");
		//keycodesMap.put(63, "question");
		keycodesMap.put(KeyEvent.VK_AT, "at");
		//keycodesMap.put(91, "Menu");
		//keycodesMap.put(92, "Menu");
		//keycodesMap.put(KeyEvent.return, "KP_Enter");
		keycodesMap.put(KeyEvent.VK_MULTIPLY, "KP_Multiply");
		keycodesMap.put(KeyEvent.VK_ADD, "KP_Add");
		keycodesMap.put(KeyEvent.VK_SUBTRACT, "KP_Subtract");
		//keycodesMap.put(110, "KP_Delete");
		keycodesMap.put(KeyEvent.VK_DIVIDE, "KP_Divide");
		keycodesMap.put(KeyEvent.VK_NUM_LOCK, "Num_Lock");
		keycodesMap.put(KeyEvent.VK_SCROLL_LOCK, "Scroll_Lock");
		keycodesMap.put(KeyEvent.VK_DEAD_CIRCUMFLEX, "dead_circumflex");
		keycodesMap.put(KeyEvent.VK_UNDERSCORE, "underscore");
		keycodesMap.put(KeyEvent.VK_EXCLAMATION_MARK, "exclam");
		keycodesMap.put(KeyEvent.VK_QUOTEDBL, "quotedbl");
		keycodesMap.put(KeyEvent.VK_NUMBER_SIGN, "numbersign");
		keycodesMap.put(KeyEvent.VK_DOLLAR, "dollar");
		//keycodesMap.put(165, "percent");
		keycodesMap.put(KeyEvent.VK_AMPERSAND, "ampersand");
		keycodesMap.put(KeyEvent.VK_LEFT_PARENTHESIS, "parenleft");
		keycodesMap.put(KeyEvent.VK_RIGHT_PARENTHESIS, "parenright");
		keycodesMap.put(KeyEvent.VK_ASTERISK, "asterisk");
		keycodesMap.put(KeyEvent.VK_PLUS, "plus");
		//keycodesMap.put(172, "bar");
		//keycodesMap.put(173, "minus");
		keycodesMap.put(KeyEvent.VK_BRACELEFT, "braceleft");
		keycodesMap.put(KeyEvent.VK_BRACERIGHT, "braceright");
		keycodesMap.put(KeyEvent.VK_DEAD_TILDE, "asciitilde");
		keycodesMap.put(KeyEvent.VK_SEMICOLON, "semicolon");
		keycodesMap.put(KeyEvent.VK_COMMA, "comma");
		keycodesMap.put(KeyEvent.VK_MINUS, "minus");
		keycodesMap.put(KeyEvent.VK_PERIOD, "period");
		keycodesMap.put(KeyEvent.VK_SLASH, "slash");
		keycodesMap.put(KeyEvent.VK_DEAD_GRAVE, "grave");
		keycodesMap.put(KeyEvent.VK_OPEN_BRACKET, "bracketleft");
		keycodesMap.put(KeyEvent.VK_BACK_SLASH, "backslash");
		keycodesMap.put(KeyEvent.VK_CLOSE_BRACKET, "bracketright");
		keycodesMap.put(KeyEvent.VK_QUOTE, "apostrophe");
		
		

	}
	
	private static void map(int keycode, int c) {
		keycodesMap.put(keycode, KeyEvent.getKeyText(c));
	}
	
	public static String getName(int keycode) {
		return keycodesMap.get(keycode);
	}
	
	
	public static Set<Entry<Integer, String>> getEntries() {
		return keycodesMap.entrySet();
	}

	public static List<String> getModifiers(int modifiers) {
		ArrayList<String> modifierList = new ArrayList<String>();
		if((modifiers & KeyEvent.SHIFT_MASK) != 0) {
			modifierList.add("shift");
		}
		
		if((modifiers & KeyEvent.CTRL_MASK) != 0) {
			modifierList.add("control");
		}
		
		if((modifiers & KeyEvent.ALT_MASK) != 0) {
			modifierList.add("alt");
		}

// TODO is this a modifier, what string?
//		if((modifiers & KeyEvent.ALT_GRAPH_MASK) != 0) {
//			modifierList.add("altgr");
//		}

		if((modifiers & KeyEvent.META_MASK) != 0) {
			modifierList.add("meta");
		}
		return modifierList;
	}
}
