/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package libteachingtinadbmanager.sqlite_db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import libteachingtinadbmanager.ReadingLessonCreator;
import libteachingtinadbmanager.TextEditorDBManager;
import libteachingtinadbmanager.WordWithIndexes;

public class SQLiteReadingLessonHandler {
	public static String TABLE_NAME              = "reading_lessons";
	public static String CARD_ID                 = "card_id";
	public static String DATE_IN_MILLIS          = "date_in_millis";
	public static String BOX_NUM                 = "box_num";
	public static String READING_LESSON_LEVEL    = "reading_lesson_level";
	public static String SOUND_TYPE              = "sound_type";
	public static String SOUND_WORD_OR_SENTENCE  = "sound_word_or_sentence";
	public static String ID_OF_LINKED_CARD       = "id_of_linked_card";
	public static String IS_SPELLING_MODE        = "is_spelling_mode";
	public static String CARD_TEXT               = "card_text";
	public static String CARD_IMAGES             = "card_images";
	public static String CARD_AUDIO              = "card_audio";
	public static String CARD_READ_ALONG_TIMINGS = "card_read_along_timings";

	public static void sqliteInsertCard(
		Connection db_connection,
		String sqlite_table_name,
		long date_in_millis,
		int box_num,
		int reading_lesson_level,
		String sound_type,
		String sound_word_or_sentence,
		int id_of_linked_card,
		boolean is_spelling_mode,
		String card_text,
		String card_images,
		String card_audio,
		String card_read_along_timings
	) {
		String str_is_spelling_mode;
		String str_id_of_linked_card;

		if( is_spelling_mode ) {
			str_is_spelling_mode = "1";
		} else {
			str_is_spelling_mode = "0";
		}

		if( id_of_linked_card == 0 ) {
			str_id_of_linked_card = null;
		} else {
			str_id_of_linked_card = id_of_linked_card + "";
		}

		String sql_insert = "INSERT INTO " + sqlite_table_name + "( " +
			"card_id, " + // Setting this to null makes the primary key auto-increment.
			"date_in_millis, " +
			"box_num, " +
			"reading_lesson_level, " +
			"sound_type, " +
			"sound_word_or_sentence, " +
			"id_of_linked_card, " +
			"is_spelling_mode, " +

			"card_text, " +
			"card_images, " +
			"card_audio, " +
			"card_read_along_timings )" +

			"VALUES( NULL,?,?, ?,?,?, ?,?,?, ?,?,? );"; // As mentioned above, NULL is for the primary key to be auto-incremented.

		try {
			PreparedStatement prep = db_connection.prepareStatement( sql_insert );
			prep.setString( 1, date_in_millis + "" );
			prep.setString( 2, box_num + "" );
			prep.setString( 3, reading_lesson_level + "" );
			prep.setString( 4, sound_type );
			prep.setString( 5, sound_word_or_sentence );
			prep.setString( 6, str_id_of_linked_card );
			prep.setString( 7, str_is_spelling_mode );
			prep.setString( 8, card_text );
			prep.setString( 9, card_images );
			prep.setString(10, card_audio );
			prep.setString(11, card_read_along_timings );
			prep.executeUpdate();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> sqliteQueryToList( Connection db_connection, String sql_query, String column_name ) {
		ArrayList<String> list = new ArrayList<String>();

		try {
			Statement stat = db_connection.createStatement();

			ResultSet rs = stat.executeQuery( sql_query );
			while( rs.next() ) {
				list.add( rs.getString( column_name ) );
				System.out.println( rs.getString( column_name ) );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}

		return list;
	}

	/** 
	 * We pass the 2 connected sounds like "ab,ba" as an array {"ab","ba"}, and it will add them to the SQLite database and link their "id_of_linked_card" field.
	 * @param db_connection
	 * @param sqlite_table_name
	 * @param reading_level
	 * @param card_text
	 * @param sound_type
	 */
	public static void sqliteInsertVowelConsonantSound( Connection db_connection, String sqlite_table_name, int reading_level, String card_text[], String sound_type ) {
		if( card_text.length == 2 ) {
			// If there's 2 sounds, insert both
			sqliteInsertSound( db_connection, sqlite_table_name, reading_level, card_text[0], sound_type );
			sqliteInsertSound( db_connection, sqlite_table_name, reading_level, card_text[1], sound_type );
			
			// Find out each card's "card_id".
			int card_id_1 = -1;
			int card_id_2 = -1;
			try {
				String sql_query_1 = "SELECT * FROM " + SQLiteReadingLessonHandler.TABLE_NAME + " WHERE card_text=\"" + card_text[0] + "\" AND sound_type=\"" + sound_type + "\";";
				String sql_query_2 = "SELECT * FROM " + SQLiteReadingLessonHandler.TABLE_NAME + " WHERE card_text=\"" + card_text[1] + "\" AND sound_type=\"" + sound_type + "\";";
				Statement stat;
				ResultSet rs;
				stat = db_connection.createStatement();
				rs = stat.executeQuery( sql_query_1 );
				card_id_1 = rs.getInt( "card_id" );
				rs = stat.executeQuery( sql_query_2 );
				card_id_2 = rs.getInt( "card_id" );
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			// Update each SQL Line so each card's "id_of_linked_card" is linked to the other card.
			String sql_update = "UPDATE " + SQLiteReadingLessonHandler.TABLE_NAME + " SET id_of_linked_card=? WHERE card_id=?;";
			PreparedStatement prep;
			try {
				prep = db_connection.prepareStatement( sql_update );
				prep.setString(1, card_id_2 + "" );
				prep.setString(2, card_id_1 + "" );
				prep.executeUpdate();
				
				prep = db_connection.prepareStatement( sql_update );
				prep.setString(1, card_id_1 + "" );
				prep.setString(2, card_id_2 + "" );
				prep.executeUpdate();
			} catch ( SQLException e ) {
				e.printStackTrace();
			}

		} else if( card_text.length == 1 ) {
			sqliteInsertSound( db_connection, sqlite_table_name, reading_level, card_text[0], sound_type );
		}
	}
	
	public static void sqliteInsertSound( Connection db_connection, String sqlite_table_name, int reading_level, String card_text, String sound_type ) {
		String text_with_ignored_characters_removed = (new WordWithIndexes( card_text, 0, card_text.length() )).getWordWithIgnoredCharactersRemoved();
		String sub_directory = "sounds/";
		String card_images = "";
		String card_audio = "<audio:\"" + sub_directory + text_with_ignored_characters_removed + ".wav\">";
		String card_read_along_timings = "";

		sqliteInsertCard(db_connection, sqlite_table_name,
			-1, // Initial date in milliseconds set so the card will definitely be reviewed.
			1, // box num
			reading_level, // reading lesson level
			sound_type, // sound type, consonsant pairs, vowel pairs, etc.
			TextEditorDBManager.SOUND, // sound, word, or sentence
			0, // ID of linked card
			false, // is it spelling mode
			card_text, // card's text
			card_images,
			card_audio,
			card_read_along_timings
		);
	}
	
	public static void sqliteInsertWord( Connection db_connection, String sqlite_table_name, int reading_level, String card_text ) {
		String text_with_ignored_characters_removed = (new WordWithIndexes( card_text, 0, card_text.length() )).getWordWithIgnoredCharactersRemoved().toLowerCase();
		String sub_directory = "words/";
		String card_images = "<image:\"" + sub_directory + text_with_ignored_characters_removed + ".jpg\">";
		String card_audio  = "<audio:\"" + sub_directory + text_with_ignored_characters_removed + ".wav\">";
		String card_read_along_timings = "";

		sqliteInsertCard(db_connection, sqlite_table_name,
			-1, // Initial date in milliseconds set so the card will definitely be reviewed.
			1, // box num
			reading_level, // reading lesson level
			null, // sound type, consonsant pairs, vowel pairs, etc.
			TextEditorDBManager.WORD, // sound, word, or sentence
			0, // ID of linked card
			false, // is it spelling mode
			card_text, // card's text
			card_images,
			card_audio,
			card_read_along_timings
		);
	}
	public static void sqliteInsertSentence( Connection db_connection, String sqlite_table_name, int reading_level, String card_text ) {
		// Name to be Reading Lesson 001 - Sentence.mp3
		String filename = TextEditorDBManager.getFileNameWithoutExtension( reading_level );
		String sub_directory = "sentences/";

		String card_images = "<image:\"" + sub_directory + filename + ".jpg\">";
		String card_audio  = "<audio:\"" + sub_directory + filename + " - Sentence.wav\">";
		String card_read_along_timings = null;

		sqliteInsertCard(db_connection, sqlite_table_name,
			-1, // Initial date in milliseconds set so the card will definitely be reviewed.
			1, // box num
			reading_level, // reading lesson level
			null, // sound type, consonsant pairs, vowel pairs, etc.
			TextEditorDBManager.SENTENCE, // sound, word, or sentence
			0, // ID of linked card
			false, // is it spelling mode
			card_text, // card's text
			card_images,
			card_audio,
			card_read_along_timings
		);
	}
	
	public static void sqliteInsertSoundList( Connection db_connection, String sqlite_table_name, int reading_level, List<String> list, String sound_type ) {
		for( int i = 0; i < list.size(); i++ ) {
			sqliteInsertSound( db_connection, sqlite_table_name, reading_level, list.get(i), sound_type );
		}
	}

	public static void sqliteInsertSoundVowelConsonantList( Connection db_connection, String sqlite_table_name, int reading_level, List<String[]> list, String sound_type ) {
		for( int i = 0; i < list.size(); i++ ) {
			sqliteInsertVowelConsonantSound( db_connection, sqlite_table_name, reading_level, list.get(i), sound_type );
		}

	}
	public static void writeToSQLiteDB( Connection db_connection, String sqlite_table_name, ReadingLessonCreator deck)
	{
		List<String> list;
		String sound_type;
		
		sqliteInsertSoundList( db_connection, sqlite_table_name, deck.getLevel(), deck.getConsonantPairs(), TextEditorDBManager.CONSONANT_PAIRS );
		sqliteInsertSoundList( db_connection, sqlite_table_name, deck.getLevel(), deck.getConsonantGroups(), TextEditorDBManager.CONSONANT_GROUPS );
		sqliteInsertSoundList( db_connection, sqlite_table_name, deck.getLevel(), deck.getVowelPairs(), TextEditorDBManager.VOWEL_PAIRS );
		sqliteInsertSoundVowelConsonantList( db_connection, sqlite_table_name, deck.getLevel(), deck.getVowelConsonantPairs(), TextEditorDBManager.VOWEL_CONSONANT_PAIRS );
		sqliteInsertSoundList( db_connection, sqlite_table_name, deck.getLevel(), deck.getDoubleConsonantVowelPairs(), TextEditorDBManager.DOUBLE_CONSONANT_VOWEL_PAIRS );
		sqliteInsertSoundList( db_connection, sqlite_table_name, deck.getLevel(), deck.getDoubleVowelConsonantPairs(), TextEditorDBManager.DOUBLE_VOWEL_CONSONANT_PAIRS );

		list = deck.getWords();
		for( int i = 0; i < list.size(); i++ ) {
			sqliteInsertWord( db_connection, sqlite_table_name, deck.getLevel(), list.get(i) );
		}

		list = deck.getSentences();
		for( int i = 0; i < list.size(); i++ ) {
			sqliteInsertSentence( db_connection, sqlite_table_name, deck.getLevel(), list.get(i) );
		}
	}

	public static void updateLinkedCardID( Connection db_connection, int card_id, int new_linked_card_id ) {

		String sql_update = "UPDATE reading_lessons SET id_of_linked_card=? WHERE card_id=?;";
		try {
			PreparedStatement prep = db_connection.prepareStatement( sql_update );
			prep.setString(1, new_linked_card_id + "" );
			prep.setString(2, card_id + "" );
			prep.executeUpdate();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}

	public static void updateStudyData( Connection db_connection, int card_id, int date_in_millis, int box_num ) {
		String sql_update = "UPDATE reading_lessons SET date_in_millis=? , box_num=? WHERE card_id=?;";
		try {
			PreparedStatement prep = db_connection.prepareStatement( sql_update );
			prep.setString(1, date_in_millis + "" );
			prep.setString(2, box_num + "" );
			prep.setString(3, card_id + "" );
			prep.executeUpdate();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}

	public static void updateCardMedia( Connection db_connection, int card_id, String card_images, String card_audio, String card_read_along_timings ) {
		String sql_update = "UPDATE reading_lessons SET card_images=? , card_audio=? , card_read_along_timings=? WHERE card_id=?;";
		try {
			PreparedStatement prep = db_connection.prepareStatement( sql_update );
			prep.setString( 1, card_images );
			prep.setString( 2, card_audio );
			prep.setString( 3, card_read_along_timings );
			prep.setString( 4, card_id + "" );
			prep.executeUpdate();
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}
}
