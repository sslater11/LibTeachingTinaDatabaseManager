/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;
import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * @version 2.2
 * -- Change Log --
 * moved some classes around.
 
 -- v2.1 --
 01/02/2020
 Made some minor changes to get make this compatible with the android version.
 	Made classes public, and some attributes public too.
 
 
 
 
 -- v 2.0 --
 * removed reaction speed,target reaction speed, average reactoin speed
 * removed study mode, since it was never used.
 */

/*TODO:
 * 1. Add the ability to add pictures for the question or answer.
 *         it should check if the question or answer string has a substring with "<image:(some_filename)>"
 *         Image files should be in a folder with the same name as the database file.
 *         the database file would NOT go inside this folder. Only it's images/audio would.
 * 2. Add the ability to play audio files.
 *         it should check if the question or answer string has a substring with
 *         "<audio:(some_filename)>"
 *         "<sound:(some_filename)>"
 */




/* INFO about this app, read this to understand it.
 * This app's SRS is based on the Leitner System.
 *
 * ** The Leitner System **
 * Explanation:
 * 		The Leitner System was developed by Sebastian Leitner in the 1970s.
 * 		It uses paper flashcards and a series of boxes.
 *
 * 		Tomorrow you review the card again.
 * 		If you answer correctly, you put the flashcard in the second box,
 * 		which contains cards you review every 3 days.
 * 		If after 3 days you answer correctly again, it goes into the third box,
 * 		which gets reviewed after 5 days. And so on.
 *
 * 		If at any time you answer incorrectly, the flashcard returns to the first box
 *
 * ** My Modified Version of Leitner's System **
 * 		NNN = 5. Might make it smaller though
 * 		Each card will need to be answered correctly NNN times for it to go into the next 'box'.
 * 		The program won't end until we get all cards correct NNN times.
 * 		If you ever get a question wrong, the card will be put back into the first 'box'.
 *
 * 		The 'box' will just be a number. The first 'box' will be number 1.
 * 		The 'box' number will be the number of days away from the date to review.
 *	 		E.g. if (box = 1), and (card's date = yesterday);  then review it.
 *			E.g. if (box = 2), and (card's date = yesterday);  then skip it.
 * 			E.g. if (box = 3), and (card's date = 3 days ago); then review it.
 *
 * 		Instead of just adding 1 to the number, for each day,
 * 		I have chosen to multiply the box number by a constant, which will give a slower increment than 1.
 * 		It will give a nice round curve for learning.
 * 		If we get the question correct, we will multiply the 'box' number by 1.4.
 * 		We will then ROUND the number, so it becomes an integer, when reading the database.
 *
 * 		Because the app will ask the same question NNN times, there's more chance that I will
 * 		get the question wrong.
 * 		If I get the question wrong once out of NNN tries, the card's box number will be reset.
 * 		This seems a bit unfair, because of human error, they might be stuck on the same card for DAYS.
 * 		So I should add a 'fails' counter, to keep track of how many times the user has failed.
 * 		If their fail count is greater than 2, or 3(on second thought, 3 might be a bit high),
 * 		then the card's box number will be reset.
 * 		TODO: see above^   We need to implement the fails tracking. also log the successful attempts.
 * 		                   Log it all, because it's valid learning data.
 * 		I think that this may not be a great idea as I may fail once, because I forgot,
 * 		and now answer correctly because my memory has been refreshed.
 */

class MyDate {
	protected final static String STR_DATE_FORMAT = "dd/MM/yyyy";
	protected final static String STR_TIME_FORMAT = "HH:mm:ss";

	/**
	 * 
	 * @param d
	 * @param cards_box_num
	 * @return -1 if before today, 0 is today, 1 if after today
	 */
	public static int compare(String d, int cards_box_num){
		Date cards_date;
		try {
			cards_date = fromString(d);
		} catch (ParseException e) {
			e.printStackTrace();
			cards_date = new Date();
		}
		Date todays_date = today();
		
		// Add the box number to the day as days.
		// This gives an easy review algorithm.
		cards_date = addDays(cards_date, cards_box_num);
		
		// Check if the review should be now or in the future.
		if        ( cards_date.before(todays_date) ) {
			// It's before.
			return -1;
		} else if ( cards_date.after (todays_date) ) {
			// It's after.
			return 1;
		} else {
			// It's equal.
			return 0;
		}
		
	}
	
	/**
	 * 
	 * @param d
	 * @param cards_box_num
	 * @return -1 if before today, 0 is today, 1 if after today
	 */
	public static int compare(Date d, int cards_box_num){
		String str = toString(d);
		
		return compare(str, cards_box_num);
	}

	// TODO: removed all of the things for my old way of reviewing decks.
	// Kept this function, because it might come in useful
	/**
	 * 
	 * @param d
	 * @param review_interval
	 * @return -1 if before now, 0 is now, 1 if after now
	 */
	public static int compareTime(String d, int review_interval){
		Date cards_time;
		try {
			cards_time = timeFromString(d);
		} catch (ParseException e) {
			e.printStackTrace();
			cards_time = new Date();
		}
		Date current_time = currentTime();
		
		// Add the box number to the day as days.
		// This gives an easy review algorithm.
		//System.out.println("cards_current_time = " + cards_time.toString());
		
		cards_time = addMinutes(cards_time, review_interval);
		//System.out.println("cards_future_time = " + cards_time.toString());
		//System.out.println("todays time = " + cards_time.toString());
		
		// Check if the review should be now or in the future.
		if        ( cards_time.before(current_time) ) {
			// It's before.
			//System.out.println(cards_time.toString() + cards_time.toString() + " is before " + current_time);
			return -1;
		} else if ( cards_time.after (current_time) ) {
			// It's after.
			//System.out.println(cards_time.toString() + cards_time.toString() + " is after "  + current_time);
			return 1;
		} else {
			// It's equal.
			//System.out.println(cards_time.toString() + cards_time.toString() + " is equal "  + current_time);
			return 0;
		}
		
	}
	
	/**
	 * 
	 * @param d
	 * @param review_interval
	 * @return -1 if before, 0 if equal, 1 if after
	 */
	public static int compareTime(Date d, int review_interval){
		String str = timeToString(d);
		
		return compareTime(str, review_interval);
	}

	public static String toString(Date date1) {
		String str_date;
		
		//Set the format
		SimpleDateFormat sdf = new SimpleDateFormat(STR_DATE_FORMAT);
		
		str_date = sdf.format(date1);
		
		return str_date;
	}
	
	public static String timeToString(Date date1) {
		String str_date;
		
		//Set the format
		SimpleDateFormat sdf = new SimpleDateFormat(STR_TIME_FORMAT);
		
		str_date = sdf.format(date1);
		
		return str_date;
	}

	public static Date fromString( String date1 ) throws ParseException {
		Date date2;
		try {
			date2 = new SimpleDateFormat(STR_DATE_FORMAT).parse(date1);
			return date2;
		} catch (ParseException e) {
			// Make the code calling this deal with the exception.
			throw e;
			//e.printStackTrace();
			//System.exit(500);
			//return new Date();
		}
	}
	
	public static Date timeFromString( String date1 ) throws ParseException {
		Date date2;
		try {
			date2 = new SimpleDateFormat(STR_TIME_FORMAT).parse(date1);
			return date2;
		} catch (ParseException e) {
			// Make the code calling this deal with the exception.
			throw e;
			//e.printStackTrace();
			//System.exit(500);
			//return new Date();
		}
	}
	
	/**
	 * Will return today's date as Date() object.
	 * Will return a null value if fromString() throws a ParseException, which should never happen, but I'm still documenting it :).
	 * @return
	 */
	public static Date today() {
		//Get the current date
		Calendar currentDate = Calendar.getInstance();
		
		//format it as a string
		SimpleDateFormat formatter= new SimpleDateFormat(STR_DATE_FORMAT);
		String dateNow = formatter.format(currentDate.getTime());
		
		// Convert the string to a Date object and return it		
		try {
			return fromString(dateNow);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Will return the current time as a Date() object.
	 * Will return a null value if timeFromString() throws a ParseException, which should never happen, but I'm still documenting it :).
	 * @return
	 */
	public static Date currentTime() {
		//Get the current date
		Calendar currentDate = Calendar.getInstance();
		
		//format it as a string
		SimpleDateFormat formatter= new SimpleDateFormat(STR_TIME_FORMAT);
		String dateNow = formatter.format(currentDate.getTime());
		
		// Convert the string to a Date object and return it		
		try {
			return timeFromString(dateNow);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * @param d is of type 'Date'
	 * @param days is an integer 
	 * @return The result of d + days.
	 */
	public static Date addDays(Date d, int days) {
		//Date new_date = new Date();
		// Convert days to milliseconds and add it to date 
		//new_date.setTime( d.getTime() + (days*1000*60*60*24) );
	    
		//return new_date;
		
		
		Calendar c = Calendar.getInstance();
		c.setTime( d );
		c.add(Calendar.DATE, days);  // number of days to add
		
		return c.getTime();
	}
	
	/**
	 * @param d is a String of a date e.g. "dd/MM/yyyy".
	 * @param days is an integer 
	 * @return The result of d + days.
	 */
	public static String addDays(String d, int days) {
		// Convert string to a date.
		Date new_date = new Date();
		
		try {
			new_date = fromString(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Get the result
		new_date = addDays(new_date, days);
		
		// Return the result
		String str_new_date = toString(new_date);
		
		return str_new_date;
	}
	
	/**
	 * @param d is of type 'Date'
	 * @param mins is an integer 
	 * @return The result of d + mins.
	 */
	public static Date addMinutes(Date d, int mins) {
		//Date new_date = new Date();
		// Convert minutes to milliseconds and add it to date 
		//new_date.setTime( d.getTime() + (mins*1000*60) );
		
		//return new_date;
		
		Calendar c = Calendar.getInstance();
		c.setTime( d );
		c.add(Calendar.MINUTE, mins);  // number of minutes to add
		
		return c.getTime();

	}
	
	/**
	 * @param d is a String of a date e.g. "dd/MM/yyyy".
	 * @param mins is an integer 
	 * @return The result of d + mins.
	 */
	public static String addMinutes(String d, int mins) {
		// Convert string to a date.
		Date new_date = new Date();
		
		try {
			new_date = timeFromString(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Get the result
		new_date = addMinutes(new_date, mins);
		
		// Return the result
		String str_new_date = timeToString(new_date);
		
		return str_new_date;
	}
	
	public static boolean isLegalDate( String str ) {
		boolean b = true;
		try {
			fromString(str);
		} catch( ParseException e ) {
			b = false;
		}
		
		return b;
	}
	
	public static boolean isLegalTime( String str ) {
		boolean b = true;
		try {
			timeFromString(str);
		} catch( ParseException e ) {
			b = false;
		}
		
		return b;
	}
}


abstract public class Deck implements Serializable{
	public ArrayList<Card>        deck = new ArrayList<Card>();
	ArrayList<Card> learnt_deck = new ArrayList<Card>();
	
	public DeckSettings settings;
 	
	protected RandomizedIndex deck_index;

	protected File db_file;
	protected File db_config_file;
	
	protected String deck_name;
	

	public Deck (String db_filename, String db_config_filename) {
		this( new File(db_filename), new File(db_config_filename) );
	}
	
	public Deck( File db_filename, File db_config_filename ) {
		// Read database and send the cards found to the deck.
//		db_file        = new File(Environment.getExternalStorageDirectory(), db_filename) ;
//		db_config_file = new File(Environment.getExternalStorageDirectory(), db_config_filename) ;
		db_file        = db_filename;
		db_config_file = db_config_filename;
		
		settings = CardDBManager.getConfig(db_config_file);
		deck     = CardDBManager.readDB(db_file, settings);
		
		deck_index = new RandomizedIndex( this ); 
	}

	public Deck( String deck_name ) {
		this.deck_name = deck_name;
		
		db_file        = new File( getDeckPath() );
		db_config_file = new File( getDeckConfigPath() );
		
		settings = CardDBManager.getConfig(db_config_file);
		deck     = CardDBManager.readDB(db_file, settings);
		
		deck_index = new RandomizedIndex( this ); 
	}

	public Deck( ArrayList<Card> d, File deck_file_path, DeckSettings s ) {
		db_file = deck_file_path;
		db_config_file = s.getFile();

		deck = d;
		settings = s;
		
		deck_index = new RandomizedIndex( this );

	}
	public String  getDeckPath() {
		return getAppDataPath() + deck_name + ".txt";
	}
	
	public String getDeckConfigPath() {
		return getAppDataPath() + deck_name + ".conf";
	}
	
	public String getDeckMediaPath() {
		return getAppDataPath() + "media" + File.separator + deck_name + File.separator;
	}
	
	public String getAppDataPath() {
		return System.getProperty("user.dir") + File.separator + "decks" + File.separator;
	}
	
	
	
	public int getDeckGuiType() {
		return settings.getDeckGuiType();
	}
	
	public String getFilePath() {
		return db_file.getAbsolutePath();
	}

	
	public boolean checkAnswer( String users_answer ) {
		if ( users_answer == getAnswer().get(0) ) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkAnswer( int users_answer ) {
		return checkAnswer( "" + users_answer );
	}

	
	public boolean isLearnt() {
		if( settings.areCardsRemovable() ) {
			if( deck.size() == 0 ) {
				return true;
			} else {
				return false;
			}
		} else {
			boolean is_deck_learnt = true;
			for( int i = 0; i < deck.size(); i++ ) {
				if( deck.get(i).isLearnt() == false ) {
					is_deck_learnt = false;
					break;
				}
			}
			
			return is_deck_learnt;
		}
	}
	
	public abstract ArrayList<String> getQuestion();
	public abstract ArrayList<String> getAnswer();
	
	public Card getCurrentCard() {
		return deck.get(deck_index.getCurrent());
	}
	
	
	
	public void nextQuestion( boolean is_answer_correct, boolean stay_on_incorrect_card ) {
		if( is_answer_correct ) {
			// Mark it as successful.
			getCurrentCard().success();
		
			// Check if the card should be removed from the deck.
			if( settings.areCardsRemovable() ) {
				// Remove the card.
				// Check if the current card has been learnt, and move it to the learnt_deck.
				if( getCurrentCard().isLearnt() ) {
					// Update the card's details, used for writing to the database.
					learnt_deck.add( getCurrentCard() );
					deck.remove( deck_index.getCurrent() );
					
					// Update the list of indexes, since we just removed a card
					deck_index.ResetList();
				}
			}
			
			if( ! isLearnt() ) {
				deck_index.getNext();
			}
		} else {
			// They got it wrong, so the card's success count has been reset to 0.
			// Now there are more reviews needed, so reset the index list.
			getCurrentCard().failed();
			if( stay_on_incorrect_card ) {
				deck_index.ResetListKeepFirstIndexSame();
			} else {
				deck_index.ResetList();
			}
		}
	
		if( isLearnt() ) {
			System.out.println("nextQuestion() has been called, and the deck has now been learnt.");
			System.out.println("It was probably learnt during this last execution of it, so does that make this message meaningless?");
		}
	}

	public ArrayList<Card> getDeck() {
		return deck;
	}
	
	public ArrayList<Card> getLearntDeck() {
		if( settings.areCardsRemovable() ) {
			// The cards were moved to this deck.
			
			// Update all the cards in the deck.
			for(int i = 0; i < learnt_deck.size(); i++ ) {
				if ( learnt_deck.get(i).hasThisBeenUpdated() == false ) {
					learnt_deck.get(i).updateStudyData();
				}
			}
			return learnt_deck;
		} else {
			// The cards stayed in this deck.
			
			// update all the cards in this deck.
			for(int i = 0; i < deck.size(); i++ ) {
				if ( deck.get(i).hasThisBeenUpdated() == false ) {
					deck.get(i).updateStudyData();
				}
			}
			return deck;
		}
	}

	
	public String addHTMLRow( String str) {
			str = "        <td>" + str + "</td>\n";
			return str;
	}
	
	public String toHTML() {
		String html = "<table>";
	
		html += "    <tr>\n";
		html += addHTMLRow("Question");
		html += addHTMLRow("Box Num");
		html += addHTMLRow("Question");
		ArrayList<Card> current_deck = getLearntDeck();
		for(int i = 0; i < current_deck.size(); i++) {
			html += "    <tr>\n";
			html += addHTMLRow( current_deck.get(i).getContent(0) );
			html += addHTMLRow( "" + current_deck.get(i).getBoxNum() );
			html += "    </tr>\n";
			
		}
		html += "</table>";
		
		return html;
	}
	
	public int getNumCardsLeft() {
		System.out.println(deck_index.toString());
		if( isLearnt() ) {
			return 0;
		} else {
			return deck_index.size();
		}
	}
	
	public int getSuccessLimit() {
		return settings.getSuccessLimit();
	}
	
	public int getMaxCardCount() {
		return ( getSuccessLimit() * deck.size() );
	}

	
	
	/*public String[][] duplicate2dArray(int num_of_duplicates, String[][] arr){
		String[][] rand = new String[arr.length * num_of_duplicates][arr[0].length];

		// Copy the same array several times.
		// This method works best for shuffling.
		for ( int i = 0, ordered_index = 0;
		      i < rand.length;
		      i++, ordered_index++ ) {
			
			if ( ordered_index >= arr.length ) {
				ordered_index = 0;
			}
			for ( int k = 0; k < rand[i].length; k++) {
				rand[i][k] = arr[ordered_index][k];
			}
		}
		
		return arr;
	}*/

	
	/*static void shuffle2dArray(String[][] ar) {
		Random rand = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rand.nextInt(i + 1);
			// Simple swap
			for ( int k = ar[index].length - 1; k >= 0; k-- ) {
				String a = ar[index][k];
				ar[index][k] = ar[i][k];
				ar[i][k] = a;
			}
		}
	}*/

}
