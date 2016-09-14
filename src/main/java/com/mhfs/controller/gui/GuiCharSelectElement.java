package com.mhfs.controller.gui;

import com.mhfs.controller.mappings.actions.ActionButtonState;
import com.mhfs.controller.mappings.actions.ActionRegistry;

public class GuiCharSelectElement extends GuiSelectElement<Character>{

	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz ", SPECIAL_CHARS = "!\"§$%&/()=?*+'#:.;,";
	private static final ISelectableElement<Character>[] LOWERCASE_ALPHABET, UPPERCASE_ALPHABET, SPECIAL_ALPHABET;
	
	static {
		LOWERCASE_ALPHABET = convert(ALPHABET.toCharArray());
		UPPERCASE_ALPHABET = convert(ALPHABET.toUpperCase().toCharArray());
		SPECIAL_ALPHABET = convert(SPECIAL_CHARS.toCharArray());
	}
	
	private boolean capital, special;
	
	public GuiCharSelectElement(int middleX, int middleY) {
		super(middleX, middleY);
	}
	
	@Override
	public void handleInput() {
		super.handleInput();
		this.capital = ((ActionButtonState)ActionRegistry.getAction("CAPITAL")).getState();
		this.special = ((ActionButtonState)ActionRegistry.getAction("SPECIAL_CHAR")).getState();
	}

	@Override
	public ISelectableElement<Character>[] getElements() {
		if(special) {
			return SPECIAL_ALPHABET;
		} else if (capital) {
			return UPPERCASE_ALPHABET;
		}
		return LOWERCASE_ALPHABET;
	}
	
	private static ISelectableElement<Character>[] convert(char[] in) {
		ISelectableElement<Character>[] out = new SelectableCharacterImpl[in.length];
		for(int i = 0; i < in.length; i++) {
			out[i] = new SelectableCharacterImpl(in[i]);
		}
		return out;
	}
	
	private static class SelectableCharacterImpl implements ISelectableElement<Character> {

		private char theChar;
		
		public SelectableCharacterImpl(char c) {
			this.theChar = c;
		}
		
		@Override
		public String getDisplayString() {
			return this.theChar + "";
		}

		@Override
		public Character getResult() {
			return this.theChar;
		}
		
	}
}
