/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;

import java.io.File;
import java.util.Locale;

// Version 1.1

/*
Changelog : added the reading_and_spelling deck type
 */

public class DeckSettings {
	public    static final   float DEFAULT_CONFIG_VERSION = (float) 1.1;
	protected static final     int DEFAULT_FAIL_LIMIT    = 2; // The amount of times the user can get a question wrong, before it's box number resets to DEFAULT_BOX_NUM.
	protected static final     int DEFAULT_SUCCESS_LIMIT = 4; // Number of times to be asked the question successfully, before the card is learnt/finished.
	protected static final     int DEFAULT_CARD_LIMIT    = 3; // Maximum number of cards for the deck.
	protected static final boolean DEFAULT_ARE_CARDS_REMOVABLE  = false;
	protected static final boolean DEFAULT_IS_GROUP_MODE        = false;
	protected static final boolean DEFAULT_IS_GROUP_REVIEW_DATE = true; // Is the default group mode 'review date', or 'group name'.
	
	/* Deck GUI Types explained 
	 * Flashcards: Will show a question and an answer, will include audio and images.
	 * Maths:      Will give the user some sums to figure out. Will include addition, subtraction and multiplication.
	 * Keyboard:   Will ask the user to enter the answer using a keyboard. It will show them the correct answer, so they can see it spelt properly.
	 *             e.g. Will show tina a picture of a Bat, and ask her to spell it. 
	 */
	public final static String[] DECK_GUI_TYPES = { "flashcard", "maths", "keyboard", "reading_and_spelling" };
	public final static int DECK_GUI_TYPE_FLASHCARDS           = 0;
	public final static int DECK_GUI_TYPE_MATHS                = 1;
	public final static int DECK_GUI_TYPE_KEYBOARD             = 2;
	public final static int DECK_GUI_TYPE_READING_AND_SPELLING = 3;

	protected int deck_gui_type;
	
	protected float config_version = DEFAULT_CONFIG_VERSION;
	
	
	protected int fail_limit    = DEFAULT_FAIL_LIMIT;
	protected int success_limit = DEFAULT_SUCCESS_LIMIT;
	protected int card_limit    = DEFAULT_CARD_LIMIT; // max number of cards for the deck.
	protected boolean are_cards_removable  = DEFAULT_ARE_CARDS_REMOVABLE; // Should we remove card from the deck if it's been learnt?
	protected boolean is_group_mode        = DEFAULT_IS_GROUP_MODE;
	protected boolean is_group_review_date = DEFAULT_IS_GROUP_REVIEW_DATE;
	
	protected File settings_file;

	/**
	 * Make a blank DeckSettings object
	 * The file parameter is just for getting the file's path.
	 * @param f
	 */
	DeckSettings(File f) {
		setFile(f);

		setDeckGuiType();
		setFailLimit();
		setSuccessLimit();
		setCardLimit();
		setAreCardsRemovable();
		setIsGroupMode();
	}
	
	public static boolean isDeckGuiTypeValid( String str ) {
		str = str.toLowerCase(Locale.US);
		for( int i = 0; i < DECK_GUI_TYPES.length; i++) {
			if( str.compareTo(DECK_GUI_TYPES[i]) == 0) {
				return true;
			}
		}
		return false;
	}
	public static boolean isDeckGuiTypeValid( int num ) {
		if( (num < DECK_GUI_TYPES.length) && (num >= 0) ) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isDeckGuiTypeMaths( String str ) {
		if( DECK_GUI_TYPES[DECK_GUI_TYPE_MATHS].compareTo(str) == 0 ) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isDeckGuiTypeMaths( int num ) {
		if( DECK_GUI_TYPE_MATHS == num ) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isDeckGuiTypeFlashcards( String str ) {
		if( DECK_GUI_TYPES[DECK_GUI_TYPE_FLASHCARDS].compareTo(str) == 0 ) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isDeckGuiTypeFlashcards( int num ) {
		if( DECK_GUI_TYPE_FLASHCARDS == num ) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isDeckGuiTypeKeyboard( String str ) {
		if( DECK_GUI_TYPES[DECK_GUI_TYPE_KEYBOARD].compareTo(str) == 0 ) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isDeckGuiTypeKeyboard( int num ) {
		if( DECK_GUI_TYPE_KEYBOARD == num ) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isGroupMode() {
		return is_group_mode;
	}
	
	public boolean isGroupReviewDate() { 
		return is_group_review_date;
	}
	
	public boolean areCardsRemovable() {
		return are_cards_removable;
	}
	
	
	
	public void setDeckGuiType( int num ) {
		if( (num < DECK_GUI_TYPES.length) && (num >= 0) ) {
			deck_gui_type = num;
		} else {
			System.out.println("Erorr: Deck GUI Type doesn't exist: " + num);
			System.out.println("Setting Deck GUI Type as 'flashcards'.");
			deck_gui_type = DECK_GUI_TYPE_FLASHCARDS;
		}
	}
	public void setDeckGuiType() {
		// Set the Deck Gui Type to the default: 'flashcards'.
		setDeckGuiType(DECK_GUI_TYPE_FLASHCARDS);
	}
	
	public void setDeckGuiType( String str ) {
		// This line is just in case the loop doesn't find a match.
		deck_gui_type = -1;
		
		str = str.toLowerCase(Locale.US);
		for( int i = 0; i < DECK_GUI_TYPES.length; i++ ) {
			if( str.compareTo(DECK_GUI_TYPES[i]) == 0) {
				deck_gui_type = i; 
				break;
			}
		}
		
		if( deck_gui_type == -1 ) {
			System.out.println("Error: Deck GUI Type doesn't exist: " + str);
			System.out.println("Setting Deck GUI Type as 'flashcards'.");
			setDeckGuiType(DECK_GUI_TYPE_FLASHCARDS);
		}
	}
	
	public void setFailLimit( int num ) {
		if( num >= 1 ) {
			fail_limit = num;
		} else {
			System.out.println("Error: fail limit set to default, number supplied was less than 1.");
			fail_limit = DEFAULT_FAIL_LIMIT;
		}
	}
	public void setFailLimit() {
		setFailLimit(DEFAULT_FAIL_LIMIT);
	}
	
	public void setSuccessLimit( int num ) {
		if( num >= 1 ) {
			success_limit = num;
		} else {
			System.out.println("Error: success limit set to default, number supplied was less than 1.");
			setSuccessLimit( DEFAULT_SUCCESS_LIMIT );
		}
	}
	public void setSuccessLimit() {
		setSuccessLimit( DEFAULT_SUCCESS_LIMIT );
	}

	public void setCardLimit( int num ) {
		if( num >= 1 ) {
			card_limit = num;
		} else {
			System.out.println("Error: card limit set to default, number supplied was less than 1.");
			card_limit = DEFAULT_CARD_LIMIT;
		}
	}
	public void setCardLimit() {
		setCardLimit(DEFAULT_CARD_LIMIT);
	}

	public void setFile(File s) {
		settings_file = s;
	}
	
	public void setFilePath(String s) {
		settings_file = new File(s);
	}
	
	public void setAreCardsRemovable( boolean b ) {
		are_cards_removable = b;
	}
	
	
	public void setAreCardsRemovable() {
		setAreCardsRemovable(DEFAULT_ARE_CARDS_REMOVABLE);
	}
	
	public void setIsGroupMode( boolean b ) {
		is_group_mode = b;
	}
	
	public void setIsGroupMode() {
		setIsGroupMode(DEFAULT_IS_GROUP_MODE);
	}
	
	public void setIsGroupReviewDate( boolean b ) {
		is_group_review_date = b;
	}
	
	public void setIsGroupReviewDate() {
		setIsGroupReviewDate(DEFAULT_IS_GROUP_REVIEW_DATE);
	}
	
	
	public int getDeckGuiType() {
		return deck_gui_type;
	}

	public int getFailLimit() {
		return fail_limit;
	}
	
	public int getSuccessLimit () {
		return success_limit;
	}
	
	public int getCardLimit() {
		return card_limit;
	}
	
	public String getConfigVersionString() {
		String str = "" + config_version;
		return str;
	}
	
	public String getDeckGuiTypeString() {
		return DECK_GUI_TYPES[getDeckGuiType()];
	}

	public String getFailLimitString() {
		String str = "" + fail_limit;
		return str;
	}
	
	public String getSuccessLimitString() {
		String str = "" + success_limit;
		return str;
	}
	
	public String getCardLimitString() {
		String str = "" + card_limit;
		return str;
	}
	

	public String getFilePath() {
		return settings_file.getAbsolutePath();
	}
	public File getFile() {
		return settings_file;
	}

}
