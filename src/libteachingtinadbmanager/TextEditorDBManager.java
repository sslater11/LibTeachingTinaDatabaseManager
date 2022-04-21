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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import libteachingtinadbmanager.*;


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
	public static String WORDS                        = "Words";
	public static String VOWEL_PAIRS                  = "Vowel Pairs";
	public static String SENTENCE                     = "Sentence";

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
	
	public static ReadingLessonCreator readDBFile( File db_file, DeckSettings deck_settings) {
		/*
		 * Database file layout
		 * All spaces separated with a tab. Even between the sum numbers and operator.
		 * The cards are the last thing to input, this allows the array to have any amount of fields for the card.
		 *
		 *    +--------------+-------+------------+----------+-----------+
		 *    |              |       |            |  Daily   |           |
		 *    |              |  Box  |  Review    |  Review  |           |
		 *    |  Date        |  Num  |  Time      |  Count   |  Cards    |
		 *    +--------------+-------+------------+----------+---+---+---+
		 *    |  29/10/2013  |  4    |  00:00:00  |  1       | 1 | + | 2 |
		 *    |  29/10/2013  |  4    |  00:00:00  |  1       | 5 | + | 3 |
		 *    +--------------+-------+------------+----------+-----------+
		 *
		 */
		Scanner db;
		ArrayList<String> lines = new ArrayList<String>();
		
		// Open the file and read it into the ArrayList called 'lines'.

		try {
			db = new Scanner(  db_file );
			// Read in the database contents.
			while ( db.hasNextLine() ) {
				// Get 1 line of input
				String line = db.nextLine();
				
				if( ! isDBLineBlank(line) ) {
					lines.add( line );
				}
			}
			
			db.close();
		}
		// Catch for when the file isn't opened.
		catch ( Exception e ) {
			System.out.println( "Could not find the database file: " + db_file );
			System.out.println( "This could be a problem with another type of exception though.");
			System.out.println( "See the debug info below" );
			e.printStackTrace();
		}
		

		// Convert it all to a list of Card objects, and add them to the reading_deck.
		ReadingLessonCreator reading_deck = new ReadingLessonCreator();
		
		ArrayList<Card> list;
		
		list = CardDBManager.readDBGetGroup(db_file, deck_settings, CONSONANT_PAIRS);
		reading_deck.setConsonantPairs( cardListToStringList( list ) );
		
		list = CardDBManager.readDBGetGroup(db_file, deck_settings, VOWEL_CONSONANT_PAIRS);
		reading_deck.setVowelConsonantPairs( cardListToStringList( list ) );
	
		list = CardDBManager.readDBGetGroup(db_file, deck_settings, CONSONANT_GROUPS);
		reading_deck.setConsonantGroups( cardListToStringList( list ) );
	
		list = CardDBManager.readDBGetGroup(db_file, deck_settings, DOUBLE_CONSONANT_VOWEL_PAIRS);
		reading_deck.setDoubleConsonantVowelPairs( cardListToStringList( list ) );
	
		list = CardDBManager.readDBGetGroup(db_file, deck_settings, DOUBLE_VOWEL_CONSONANT_PAIRS);
		reading_deck.setDoubleVowelConsonantPairs( cardListToStringList( list ) );
	
		list = CardDBManager.readDBGetGroup(db_file, deck_settings, VOWEL_PAIRS);
		reading_deck.setVowelPairs( cardListToStringList( list ) );
		
		list = CardDBManager.readDBGetGroup(db_file, deck_settings, WORDS);
		reading_deck.setWords( cardListToStringList( list ) );
	
		list = CardDBManager.readDBGetGroup(db_file, deck_settings, SENTENCE);
		if( list != null ) {
			if( list.size() >= 1 ) {
				reading_deck.setSentence( list.get(0).getContent(0) );
			}
		}
		
		return reading_deck;
	}
	
	//public ArrayList<String> getContents() {
	//	return this.contents;
	//}

	
	public static void writeDB(File db_file, ReadingLessonCreator deck) {
		// Make a list with all the database lines.
		ArrayList<String> lines = getDatabaseOutput( deck );

		// Try to open the database file to write to it.
		try {
			// Make/Open the file.
			if (!db_file.exists()) {
					try {
						db_file.createNewFile();
					} catch (IOException e) {
						System.out.println("Couldn't make the file: " + db_file.getAbsolutePath());
						e.printStackTrace();
					}
			}
			
			
			FileWriter fw = new FileWriter( db_file, false );
			BufferedWriter bw_new_db = new BufferedWriter( fw );
			
			// Write the data
			for( int i = 0; i < lines.size(); i++ ) {
				bw_new_db.write( lines.get(i) );
				System.out.println("Writing to file: " + lines.get(i));
				bw_new_db.newLine();
			}
			
			bw_new_db.close();
		}
		// Catch for when the file isn't opened.
		catch( Exception e ) {
			System.out.println( "Could not find the database file: " + db_file );
			System.out.println( "This is a try and catch exception, so the file might be ok!!!" );
			System.out.println( "Try some debugging to see what the real problem is." );
			e.printStackTrace();
			System.exit(100);
		}
	}
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
	
	public static List<String> cardListToStringList(List<Card> list) {
		List<String> str_list = new ArrayList<String>();
		if( list != null ) {
			for( Card i : list ) {
				// The zero is for the card's first entry.
				str_list.add( i.getContent( 0 ) );
			}
		}
		
		return str_list;
	}
	public static List<String> listToDBLines(List<String> list ) {
		List<String> final_output = new ArrayList<String>();
		
		for(int i = 0; i < list.size(); i++ ) {
			String front = list.get(i);
			
			String front_with_ignored_characters_removed = (new WordWithIndexes( front, 0, front.length() )).getWordWithIgnoredCharactersRemoved();

			String back = "<audio:\"" + front_with_ignored_characters_removed + ".mp3\">";
			
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
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getConsonantPairs() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.VOWEL_CONSONANT_PAIRS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getVowelConsonantPairs() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.VOWEL_PAIRS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getVowelPairs() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.CONSONANT_GROUPS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getConsonantGroups() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.DOUBLE_CONSONANT_VOWEL_PAIRS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getDoubleConsonantVowelPairs() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.DOUBLE_VOWEL_CONSONANT_PAIRS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getDoubleVowelConsonantPairs() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.WORDS ) );
		lines.addAll( TextEditorDBManager.listToDBLines( deck.getWords() ) );

		lines.add("");
		lines.add("");
		lines.add( lineToDBGroupLine( TextEditorDBManager.SENTENCE ) );
		// Sentence has a different method to convert it since it's a string.
		lines.add(sentenceToDBLine(deck.getSentence(), deck.getLevel()));
		
		return lines;
	}
	
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
	
	public static String sentenceToDBLine( String sentence, int reading_level ) {
		//name to be Reading Lesson 001 - sentence 1.mp3
		/*TODO: Make a way to increment the sentance file numbers automatically
		 * e.g.
		 * Reading Lesson 0001 - Sentence 1.mp3
		 * Reading Lesson 0001 - Sentence 2.mp3
		 * Reading Lesson 0001 - Sentence 3.mp3
		 * Reading Lesson 0001 - Sentence 4.mp3
		 */
		
		// Remove bad whitespace that would mess up our database file.
		sentence = sentence.replaceAll("\n", "<br>" );
		sentence = sentence.replaceAll("\t", " " );
		
		String filename = getFileNameWithoutExtension( reading_level );

		String sentence_audio_filename = "<audio:\"" + filename + " - Sentence.mp3\">";
		
		String line = makeDBLine("#SENTENCE#" + sentence, sentence_audio_filename);
		
		return line;
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
