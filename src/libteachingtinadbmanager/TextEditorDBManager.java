/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import libteachingtinadbmanager.*;
import libteachingtinadbmanager.sqlite_db.SQLiteReadingLessonHandler;


// TODO: make this class only contain static methods.
//       it should never be an object, so not sure why that happened.

/**
 * This class will load the contents of a file, so we can parse
 * it many times without having to reload the file every time.
 * @author simon
 *
 */
public class TextEditorDBManager {
	public static String CONSONANT_PAIRS              = "Consonant Pairs";
	public static String VOWEL_CONSONANT_PAIRS        = "Vowel Consonant Pairs";
	public static String CONSONANT_GROUPS             = "Consonant Groups";
	public static String DOUBLE_CONSONANT_VOWEL_PAIRS = "Double Consonant Vowel Pairs";
	public static String DOUBLE_VOWEL_CONSONANT_PAIRS = "Double Vowel Consonant Pairs";
	public static String WORD                         = "Word";
	public static String SOUND                        = "Sound";
	public static String VOWEL_PAIRS                  = "Vowel Pairs";
	public static String SENTENCE                     = "Sentence";

	enum CardType { isSound, isWord, isSentence };
	//public static final String HEADING_CONSONANT_PAIRS              = "# Consonant Pairs:";
	//public static final String HEADING_CONSONANT_GROUPS             = "# Consonant Groups:";
	//public static final String HEADING_VOWEL_CONSONANT_PAIRS        = "# Vowel Consonant Pairs:";
	//public static final String HEADING_VOWEL_PAIRS                  = "# Vowel Pairs:";
	//public static final String HEADING_DOUBLE_CONSONANT_VOWEL_PAIRS = "# Double Consonant Vowel Pairs:";
	//public static final String HEADING_DOUBLE_VOWEL_CONSONANT_PAIRS = "# Double Vowel Consonant Pairs:";
	//public static final String HEADING_WORDS                        = "# Words:";
	//public static final String HEADING_SENTENCE                     = "# Sentence:";
	
	public static final ArrayList<String> HEADINGS = new ArrayList<String>();
	
	public static final int INDEX_CARD_FRONT_SIDE = 4;
	
	//static {
	//	HEADINGS.add( HEADING_CONSONANT_PAIRS              );
	//	HEADINGS.add( HEADING_CONSONANT_GROUPS             );
	//	HEADINGS.add( HEADING_VOWEL_CONSONANT_PAIRS        );
	//	HEADINGS.add( HEADING_VOWEL_PAIRS                  );
	//	HEADINGS.add( HEADING_DOUBLE_CONSONANT_VOWEL_PAIRS );
	//	HEADINGS.add( HEADING_DOUBLE_VOWEL_CONSONANT_PAIRS );
	//	HEADINGS.add( HEADING_WORDS                        );
	//	HEADINGS.add( HEADING_SENTENCE                     );
	//}
	
	//public TextEditorDBManager( File db_file ) {
	//		this.db_file = db_file;
	//		loadDBFile();
	//}
	
	public static ReadingLessonCreator readSQLiteDB( Connection db_connection, String sqlite_table_name , DeckSettings deck_settings) throws SQLException {
		ReadingLessonCreator reading_deck = new ReadingLessonCreator();
		
		String sql_select_sentences           = "SELECT * FROM reading_lessons WHERE sound_word_or_sentence=\"" + SENTENCE + "\";";
		String sql_select_words               = "SELECT * FROM reading_lessons WHERE sound_word_or_sentence=\"" + WORD     + "\";";
		String sql_select_words_reading_level = "SELECT reading_lesson_level FROM reading_lessons WHERE sound_word_or_sentence=\"" + WORD     + "\";";
		String sql_select_consonant_pairs              = "SELECT * FROM reading_lessons WHERE sound_type=\"" + CONSONANT_PAIRS              + "\";";
		String sql_select_vowel_consonant_pairs        = "SELECT * FROM reading_lessons WHERE sound_type=\"" + VOWEL_CONSONANT_PAIRS        + "\";";
		String sql_select_consonant_groups             = "SELECT * FROM reading_lessons WHERE sound_type=\"" + CONSONANT_GROUPS             + "\";";
		String sql_select_double_consonant_vowel_pairs = "SELECT * FROM reading_lessons WHERE sound_type=\"" + DOUBLE_CONSONANT_VOWEL_PAIRS + "\";";
		String sql_select_double_vowel_consonant_pairs = "SELECT * FROM reading_lessons WHERE sound_type=\"" + DOUBLE_VOWEL_CONSONANT_PAIRS + "\";";
		String sql_select_vowel_pairs                  = "SELECT * FROM reading_lessons WHERE sound_type=\"" + VOWEL_PAIRS                  + "\";";

		String card_text = "card_text";
		
		reading_deck.setConsonantPairs           ( SQLiteReadingLessonHandler.sqliteQueryToList( db_connection, sql_select_consonant_pairs,              card_text) );
		reading_deck.setVowelConsonantPairs      ( SQLiteReadingLessonHandler.sqliteQueryToList( db_connection, sql_select_vowel_consonant_pairs,        card_text ) );
		reading_deck.setConsonantGroups          ( SQLiteReadingLessonHandler.sqliteQueryToList( db_connection, sql_select_consonant_groups,             card_text ) );
		reading_deck.setDoubleConsonantVowelPairs( SQLiteReadingLessonHandler.sqliteQueryToList( db_connection, sql_select_double_consonant_vowel_pairs, card_text ) );
		reading_deck.setDoubleVowelConsonantPairs( SQLiteReadingLessonHandler.sqliteQueryToList( db_connection, sql_select_double_vowel_consonant_pairs, card_text ) );
		reading_deck.setVowelPairs               ( SQLiteReadingLessonHandler.sqliteQueryToList( db_connection, sql_select_vowel_pairs,                  card_text ) );
		
		reading_deck.setWordsReadingLevel( SQLiteReadingLessonHandler.sqliteQueryToList( db_connection, sql_select_words_reading_level, "reading_lesson_level" ) );
		reading_deck.setWords    ( SQLiteReadingLessonHandler.sqliteQueryToList( db_connection, sql_select_words,     card_text ) );
		reading_deck.setSentences( SQLiteReadingLessonHandler.sqliteQueryToList( db_connection, sql_select_sentences, card_text ) );
		
		try {
			Statement stat = db_connection.createStatement();
		
			String sql_get_last_card_id = "SELECT max(card_id) FROM " + sqlite_table_name + ";";
			ResultSet rs = stat.executeQuery( sql_get_last_card_id );
			
			int last_row_id = rs.getInt(1);
			
			String sql_select_max_level = "SELECT * FROM " + sqlite_table_name + " WHERE card_id=" + last_row_id + ";";

			rs = stat.executeQuery( sql_select_max_level );
			int reading_level = rs.getInt( "reading_lesson_level" );
			reading_deck.setLevel( reading_level );
		} catch ( SQLException e ) {
			e.printStackTrace();
		}

		return reading_deck;
	}
	/**
	 * Will get the word or sound from each card and make a List of Strings.
	 * @param list
	 * @return
	 */
	public static List<String> cardListToStringList(List<Card> list) {
		List<String> str_list = new ArrayList<String>();
		if( list != null ) {
			for( Card i : list ) {
				// Add just the card's text field to our list.
				str_list.add( i.getContent( ReadingLessonDeck.INDEX_TEXT ) );
			}
		}
		
		return str_list;
	}

	public static List<String> listToDBLines(List<String> list, CardType card_type, int reading_level ) {
		List<String> final_output = new ArrayList<String>();
		
		for(int i = 0; i < list.size(); i++ ) {
			String front = list.get(i);
			
			String front_with_ignored_characters_removed = (new WordWithIndexes( front, 0, front.length() )).getWordWithIgnoredCharactersRemoved();

			String sub_directory = "";
			
			String back = "";
			
			if( card_type == CardType.isSound ) {
				sub_directory = "sounds/";
				front = ReadingLessonDeck.IS_SOUND + "\t" + ReadingLessonDeck.READING_MODE + "\t" + front;
				back = "<audio:\"" + sub_directory + front_with_ignored_characters_removed + ".wav\">";

			} else if ( card_type == CardType.isWord ) {
				sub_directory = "words/";
				front = ReadingLessonDeck.IS_WORD + "\t" + ReadingLessonDeck.READING_MODE + "\t" + front;
				back = "<audio:\"" + sub_directory + front_with_ignored_characters_removed + ".wav\">";
				back += "\t<image:\"" + sub_directory + front_with_ignored_characters_removed + ".jpg\">";

			} else if ( card_type == CardType.isSentence ) {
				// Remove bad whitespace that would mess up our database file.
				front = front.replaceAll("\n", "<br>" );
				front = front.replaceAll("\t", " " );

				sub_directory = "sentences/";
				// The \t\t is to make an empty place for the 'image' and 'read along timing' tags.
				front = ReadingLessonDeck.IS_SENTENCE + "\t" + ReadingLessonDeck.SENTENCE_MODE + "\t" + front;
				
				// Name to be Reading Lesson 001 - Sentence.mp3
				/*TODO: Make a way to increment the sentence file numbers automatically
		 		* e.g.
		 		* Reading Lesson 0001 - Sentence.mp3
		 		* Reading Lesson 0001 - Sentence_2.mp3
		 		* Reading Lesson 0001 - Sentence_3.mp3
		 		* Reading Lesson 0001 - Sentence_4.mp3
		 		*/
				String filename = getFileNameWithoutExtension( reading_level );

				back = "<audio:\"" + sub_directory + filename + " - Sentence.wav\">";
				back += "\t<image:\"" + sub_directory + filename + ".jpg\">";
				back += "\t<read-along-timing:\"" + sub_directory + filename + ".timing\">";
			}
			
			String line = makeDBLine(front, back);
			
			final_output.add( line );
		}
		
		return final_output;
	}

	public static String lineToDBGroupLine( String line ) {
		String output = new CardsGroup(line, null).toString();
		return output;
	}
	
	/**
	 * This will return an array of strings with the lines to output to create a lesson file.
	 * @param deck
	 * @return
	 */
	public static ArrayList<String> getDatabaseOutput( ReadingLessonCreator deck ) {
		ArrayList<String> lines = new ArrayList<String>();
		
		lines.add( lineToDBGroupLine( TextEditorDBManager.CONSONANT_PAIRS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getConsonantPairs(), CardType.isSound, deck.getLevel() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.VOWEL_CONSONANT_PAIRS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getVowelConsonantPairs(), CardType.isSound, deck.getLevel() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.VOWEL_PAIRS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getVowelPairs(), CardType.isSound, deck.getLevel() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.CONSONANT_GROUPS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getConsonantGroups(), CardType.isSound, deck.getLevel() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.DOUBLE_CONSONANT_VOWEL_PAIRS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getDoubleConsonantVowelPairs(), CardType.isSound, deck.getLevel() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.DOUBLE_VOWEL_CONSONANT_PAIRS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getDoubleVowelConsonantPairs(), CardType.isSound, deck.getLevel() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.WORD ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getWords(), CardType.isWord, deck.getLevel() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.SENTENCE ) );
		lines.addAll(TextEditorDBManager.listToDBLines(deck.getSentences(), CardType.isSentence, deck.getLevel() ));
		
		return lines;
	}
	
	/**
	 * Returns the string "Reading Lesson 0001" if we pass the number 1 to it.
	 * @param reading_level
	 * @return
	 */
	public static String getFileNameWithoutExtension( int reading_level ) {
		String str_reading_level = "";
		if( reading_level < 10 ) {
			str_reading_level = "000" + reading_level;
		}
		else if( reading_level < 100 ) {
			str_reading_level = "00"  + reading_level;
		}
		else if( reading_level < 1000 ) {
			str_reading_level = "0"  + reading_level;
		}
		else {
			str_reading_level = ""   + reading_level;
		}
		return "Reading Lesson " + str_reading_level;
	}
	
	public static String getFileName( int reading_level ) {
		return getFileNameWithoutExtension( reading_level ) + ".txt";
	}
	
	public static boolean isDBLineAHeading( String line ) {
		for( int i = 0; i < HEADINGS.size(); i++ ) {
			if( line.compareToIgnoreCase( HEADINGS.get(i)) == 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isDBLineACard( String line ) {
		String[] arr_line = getArrayFromDBLine( line );
		return CardDBManager.isDBLineACard( arr_line );
	}
	
	public static boolean isDBLineBlank( String line ) {
		if( isDBLineAHeading(line) ) {
			// It's a heading, so not a blank line.
			return false;
		} else {
			// It's not a heading, so check if it's a blank line using CardDBManager.
			
			// CardDBManager uses an array to check if the string is a valid line.
			// So convert it to an array first.
			String arr_line[] = getArrayFromDBLine( line );
			
			return CardDBManager.isDBLineBlank( arr_line );
		}
	}
	
	public static String makeDBLine(String front, String back) {
		String box_num = "1";
		String daily_review_count = "0";
		String date = "01/01/2018";
		String time = "00:00:00";
		String seperator = "\t";
		
		return date + seperator + box_num + seperator + time + seperator + daily_review_count + seperator + front + seperator + back;
	}
	
	/**
	 * CardDBManager uses line split into an array to get each element of the card's data.
	 * This returns a string split into an array.
	 * @param line
	 * @return
	 */
	public static String[] getArrayFromDBLine( String line ) {
		return line.split("\t");
	}
	
	public String getCardFrontSide( String line ) {
		String arr_line[] = getArrayFromDBLine( line );
		
		if( arr_line.length > INDEX_CARD_FRONT_SIDE ) {
			return arr_line[INDEX_CARD_FRONT_SIDE];
		} else {
			return "Error: getCardFrontSide(): array out of bounds";
		}
	}

	//public ArrayList<String> getConsonantPairs() {
	//	return getLinesContentByHeading( HEADING_CONSONANT_PAIRS );
	//}
	//public ArrayList<String> getConsonantGroups() {
	//	return getLinesContentByHeading( HEADING_CONSONANT_GROUPS );
	//}
	//public ArrayList<String> getVowelConsonantPairs() {
	//	return getLinesContentByHeading( HEADING_VOWEL_CONSONANT_PAIRS );
	//}
	//public ArrayList<String> getVowelPairs() {
	//	return getLinesContentByHeading( HEADING_VOWEL_PAIRS );
	//}
	//public ArrayList<String> getDoubleConsonantVowelPairs() {
	//	return getLinesContentByHeading( HEADING_DOUBLE_CONSONANT_VOWEL_PAIRS );
	//}
	//public ArrayList<String> getDoubleVowelConsonantPairs() {
	//	return getLinesContentByHeading( HEADING_DOUBLE_VOWEL_CONSONANT_PAIRS );
	//}
	//public ArrayList<String> getWords() {
	//	return getLinesContentByHeading( HEADING_WORDS );
	//}
	//public String getSentence() {
	//	String str ="";
	//	ArrayList<String> sentence = getLinesContentByHeading( HEADING_SENTENCE );
	//	if( sentence.size() >= 1 ) {
	//		str = sentence.get( 0 );
	//	}
	//	
	//	return str;
	//}
	
	public ArrayList<String> getLinesContentByHeading( String a_heading ) {
		
		ArrayList<String> lines = getLinesByHeading( a_heading );
		ArrayList<String> new_list = new ArrayList<String>();
		
		for( int i = 0; i < lines.size(); i++ ) {
			new_list.add( getCardFrontSide( lines.get(i) ) );
		}
		
		return new_list;
	}
	
	/*
	 * This gets the cards from a card group that has been passed.
	 * e.g.
	 * Passing the String "Words" would get all the cards in the words group.
	 * Group	Consonant Pairs	17/07/2021	1.0	00:00:00	0
	 * 01/01/2018	1	00:00:00	0	th	<audio:"th.mp3">
	 * 01/01/2018	1	00:00:00	0	sh	<audio:"sh.mp3">
	 * 01/01/2018	1	00:00:00	0	ts	<audio:"ts.mp3">
	 *
	 * Group	Words	17/07/2021	1.0	00:00:00	0
	 * 01/01/2018	1	00:00:00	0	the	<audio:"the.mp3">
	 * 01/01/2018	1	00:00:00	0	cat	<audio:"cat.mp3">
	 * 01/01/2018	1	00:00:00	0	shat	<audio:"shat.mp3">
	 * 01/01/2018	1	00:00:00	0	its	<audio:"its.mp3">
	 * 01/01/2018	1	00:00:00	0	pants	<audio:"pants.mp3">
	 */
	public ArrayList<String> getLinesByHeading( String a_heading ) {
		System.exit(0);
		return null;
		//ArrayList<String> contents = getContents();
		//ArrayList<String> new_list = new ArrayList<String>();
		//boolean is_group = false;
		
		//for( int i = 0; i < contents.size(); i++ ) {
		//	if( contents.get(i).compareToIgnoreCase( a_heading ) == 0) {
		//		is_group = true;
		//	} else {
		//		if( is_group ) {
		//			String line = contents.get(i);
		//			if( isDBLineAHeading(line) ) {
		//				// We have reached another heading, so no more cards for this heading.
		//				// Might as well break out to save a few cycles.
		//				break;
		//			} else {
		//				// The line should be a card to study
		//				// Check if it is, and add it to our list
		//				if( isDBLineACard(line) ) {
		//					new_list.add( line );
		//				} else {
		//					System.out.println("Skipped this line, it's not a card.");
		//					System.out.println( line );
		//				}
		//			}
		//		}
		//	}
		//}
		//
		//return new_list;
	}
	
	public static ArrayList<String> getAllLessonFiles() {
		String str_dir = getDirectory();
		File directory = new File( str_dir );

		String [] directory_contents = directory.list();
		ArrayList<String> lessons_files = new ArrayList<String>();
		if( directory_contents != null ) {
			for( String str : directory_contents ) {
				if( str.matches("Reading Lesson .*txt") ) {
					lessons_files.add(str_dir + str);
				}
			}
		}
		Collections.sort(lessons_files);
		
		return lessons_files;
	}
	
	public static String getDirectory() {
		String dir =  System.getProperty( "user.dir" ) + File.separator + "Tinas Reading Lessons" + File.separator;
		File directory = new File( dir );
		if( ! directory.exists() ) {
			directory.mkdirs();
		}
		
		return dir;
	}
	
	/*public TextEditorReadingLessonDeck getDeck( int reading_level ) {
		TextEditorReadingLessonDeck deck = new TextEditorReadingLessonDeck(reading_level,
				getConsonantPairs(),
				getConsonantGroups(),
				getVowelConsonantPairs(),
				getVowelPairs(),
				getDoubleConsonantVowelPairs(),
				getDoubleVowelConsonantPairs(),
				getWords(),
				getSentence() );
		return deck;
	}*/
}
