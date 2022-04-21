/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Scanner;

public class CardDBManager {

	private static String version = "1.5";
	/* Changes:
	 * v1.5 removed ReadingLessonDBManager as TextEditorDBManager is a better version.
	 *      removed ReadingLessonDeck as it's in it's own file now.
	 * v1.4
	 * made it work on android, probably undoing the change in v1.3
	 * v1.3
	 * Made it so we only pass the directory to the scanDirectory function instead of it checking if we're android or windows or linux.
	 * TODO: Remove dead code from scanDirectory after moving it to the other projects.
	 * v1.2
	 * Added a check for the directory to look in for the files.
	 * v1.1
	 * I added a ReadingLessonDBManager and ReadingLessonDeck class
	 */
	
	protected static final String STR_CONFIG_VERSION_NUM   = "config_version";
	protected static final String STR_DECK_GUI_TYPE        = "deck_gui_type";
	protected static final String STR_FAIL_LIMIT           = "fail_limit";
	protected static final String STR_SUCCESS_LIMIT        = "success_limit";
	protected static final String STR_CARDS_TO_STUDY_LIMIT = "cards_to_study_limit";
	protected static final String STR_ARE_CARDS_REMOVABLE  = "are_cards_removable";
	protected static final String STR_IS_GROUP_MODE        = "is_group_mode";
	protected static final String STR_IS_GROUP_REVIEW_DATE = "is_group_review_date";
	protected static final String STR_DAILY_REVIEW_LIMIT   = "daily_review_limit";
	protected static final String STR_REVIEW_INTERVAL      = "review_interval";
	protected static final String STR_MAX_STUDY_SESSIONS   = "max_study_sessions";

	
	
	/**
	 * 
	 * @param str_dir is the path to the directory
	 * @return 
	 * 
	 * Will return File[] array, listing all '.txt' files in the folder.
	 * 
	 * Will return an empty File[] array if the directory is empty.
	 * Will return a null value if it's not a directory.
	 */
	public static ArrayList<File> scanDirectory(String str_dir) {
		
		// Check what OS we are on to set the directory.
		File dir;
		String os_name = System.getProperty("os.name");
		String java_runtime = System.getProperty("java.runtime.name");

		if( java_runtime.equalsIgnoreCase("android runtime") ) {
			// Set if it's android.
			dir = new File(Environment.getExternalStorageDirectory(), str_dir) ;
		}
		else if( os_name.startsWith("Windows")) {
			// TODO: Set the path for windows properly.
			// Should be set to inside the Documents folder
			dir = new File("C:\\TeachingTina\\");
		}
		else {
			// It's probably linux.
			//dir = new File("/home/simon/tinasreadinglessonsstuff/");
			dir = new File(str_dir) ;
		}

		if( dir.isDirectory() ) {
			/* This one line will get all the files in the directory
			 * listFiles() also includes subdirectories, so
			 * this code finds just files that end with '.txt'.
			 */
			File[] files = dir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					String name = pathname.getName().toLowerCase(Locale.US);
					return name.endsWith(".txt") && pathname.isFile();
				}
			});
			
			ArrayList<File> file_list = new ArrayList<File>();
			for( int i = 0; i < files.length; i++ ) {
				file_list.add( files[i] );
			}
			
			// Sort the files, so they are in order.
			Collections.sort(file_list);
			
			return file_list;

		} else {
			// Not a directory.
			return null;
		}
	}
	
	
	
	public static DeckSettings getConfig(File config_file) {
		/* TODO: Things to add to the config
		 * 
		 * Maybes
		 * box number constant - probably not a good idea to add it to the config, since the user might want to change it too often!!! 
		 * Last review date, it might come in handy for a quick way to determine if the deck has been studied today already.
		 */

		Properties prop = new Properties();
		
		DeckSettings settings = new DeckSettings(config_file);
		try {
			// Load the config file.
			FileInputStream fin_conf = new FileInputStream(config_file);
			prop.load( fin_conf );

			// Get the properties

			String config_version       = prop.getProperty( STR_CONFIG_VERSION_NUM   );
			String deck_gui_type        = prop.getProperty( STR_DECK_GUI_TYPE        );
			String fail_limit           = prop.getProperty( STR_FAIL_LIMIT           );
			String success_limit        = prop.getProperty( STR_SUCCESS_LIMIT        );
			String card_limit           = prop.getProperty( STR_CARDS_TO_STUDY_LIMIT );
			String are_cards_removable  = prop.getProperty( STR_ARE_CARDS_REMOVABLE  );
			String is_group_mode        = prop.getProperty( STR_IS_GROUP_MODE        );
			String is_group_review_date = prop.getProperty( STR_IS_GROUP_REVIEW_DATE );
			String daily_review_limit   = prop.getProperty( STR_DAILY_REVIEW_LIMIT   );
			String review_interval      = prop.getProperty( STR_REVIEW_INTERVAL      );
			String max_study_sessions   = prop.getProperty( STR_MAX_STUDY_SESSIONS   );
			
			// For some reason getProperty returns a word with double quotes around it,
			// so this line removes the quotation marks.
			if( deck_gui_type != null ) {
				deck_gui_type = deck_gui_type.replaceAll("^\"|\"$", "");
			}
			
			if ( config_version != null ) {
				if( Float.valueOf( config_version ) != DeckSettings.DEFAULT_CONFIG_VERSION) {
					// TODO: Update the config file to the current one.
					// Unless it's a newer version.
				}
			} else {
				//  TODO: No config version found, so do something
			}
			if( deck_gui_type != null ) {
				if( DeckSettings.isDeckGuiTypeValid(deck_gui_type) ) {
					settings.setDeckGuiType(deck_gui_type);
				} else {
					/* It's not a valid gui type.
					 * the setDeckGuiType() method will set it to a default
					 * It'll also print out an error message.
					 * 
					 * at this time of writing the default is 'flashcards'.
					 */
					settings.setDeckGuiType(deck_gui_type);
				}
			} else {
				/* No dec_gui_type found, so setting it to the default.
				 * the setDeckGuiType() method will set it to a default
				 * 
				 * at this time of writing the default is 'flashcards'.
				 */
				settings.setDeckGuiType();
			}
			
			if( fail_limit != null ) {
				settings.setFailLimit( Integer.valueOf(fail_limit) );
			} else {
				// Set it to the default limit.
				settings.setFailLimit();
			}
			
			if( success_limit != null ) {
				settings.setSuccessLimit( Integer.valueOf(success_limit) );
			} else {
				// Set it to the default limit.
				settings.setSuccessLimit();
			}
			
			if( card_limit != null ) {
				settings.setCardLimit( Integer.valueOf(card_limit) );
			} else {
				// Set it to the default limit.
				settings.setCardLimit();
			}
			
			if( are_cards_removable != null ) {
				settings.setAreCardsRemovable( strToBool(are_cards_removable) );
			} else {
				// Set it to the default value.
				settings.setAreCardsRemovable();
			}
			
			if( is_group_mode != null ) {
				settings.setIsGroupMode( strToBool(is_group_mode) );
			} else {
				// Set it to the default value.
				settings.setIsGroupMode();
			}

			if( is_group_review_date != null ) {
				settings.setIsGroupReviewDate( strToBool(is_group_review_date) );
			} else {
				// Set it to the default value.
				settings.setIsGroupReviewDate();
			}
			
			
			fin_conf.close();
			
			
		} catch (IOException e) {
			System.out.println("Probably no config file, so just set the defaults.");
			e.printStackTrace();
		}
		
		return settings;
	}
	
	public static void writeConfigFile(DeckSettings settings) {
		Properties prop = new Properties();

		try {
	    		//set the properties value
			prop.setProperty( STR_CONFIG_VERSION_NUM  ,           settings.getConfigVersionString()    );
			prop.setProperty( STR_DECK_GUI_TYPE       ,           settings.getDeckGuiTypeString()      );
			prop.setProperty( STR_FAIL_LIMIT          ,           settings.getFailLimitString()        );
			prop.setProperty( STR_SUCCESS_LIMIT       ,           settings.getSuccessLimitString()     );
			prop.setProperty( STR_CARDS_TO_STUDY_LIMIT,           settings.getCardLimitString()        );
			prop.setProperty( STR_ARE_CARDS_REMOVABLE , boolToStr(settings.areCardsRemovable())        );
			prop.setProperty( STR_IS_GROUP_MODE       , boolToStr(settings.isGroupMode()      )        );
			prop.setProperty( STR_IS_GROUP_REVIEW_DATE, boolToStr(settings.isGroupReviewDate())        );

			//save properties to project root folder
			FileOutputStream fout_conf = new FileOutputStream(settings.getFile());
			prop.store(fout_conf, null);
			fout_conf.close();
	 
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static boolean isDBLineACard( String[] line ) {
		boolean b;
		if( line.length > Card.INDEX_NUM_ATTRIBUTES
		 && Card.isLegalReviewDate      ( line[Card.INDEX_REVIEW_DATE          ] )
		 && Card.isLegalReviewTime      ( line[Card.INDEX_REVIEW_TIME          ] )
		 && Card.isLegalBoxNum          ( line[Card.INDEX_BOX_NUM              ] )
		 && Card.isLegalDailyReviewCount( line[Card.INDEX_DAILY_REVIEW_COUNT   ] ) ) {
			b = true;
		} else {
			b = false;
		}
		return b;
	}
	
	public static boolean isDBLineBlank( String arr[] ) {
		if( (arr.length >= 1) ) {
			if( (arr[0].length() >= 1) && (arr[0].charAt(0) == '#') ) {
				return true;
			} else if ( arr[0].compareTo("") == 0 ) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isDBLineAGroup( String arr[] ) {
		/*System.out.println("arr.length" + arr.length);
		System.out.println("arr.length" + arr.length);
		System.out.println("arr.length" + arr.length);
		System.out.println("arr.length" + arr.length);
		System.out.println( arr[CardsGroup.INDEX_TAG               ] );
		System.out.println( arr[CardsGroup.INDEX_NAME              ] );
		System.out.println( arr[CardsGroup.INDEX_REVIEW_DATE       ] );
		System.out.println( arr[CardsGroup.INDEX_BOX_NUM           ] );
		System.out.println( arr[CardsGroup.INDEX_REVIEW_TIME       ] );
		System.out.println( arr[CardsGroup.INDEX_DAILY_REVIEW_COUNT] );
		
		System.out.println( CardsGroup.isLegalTag             ( arr[CardsGroup.INDEX_TAG               ] ));
		System.out.println( CardsGroup.isLegalName            ( arr[CardsGroup.INDEX_NAME              ] ));
		System.out.println( CardsGroup.isLegalReviewDate      ( arr[CardsGroup.INDEX_REVIEW_DATE       ] ));
		System.out.println( CardsGroup.isLegalBoxNum          ( arr[CardsGroup.INDEX_BOX_NUM           ] ));
		System.out.println( CardsGroup.isLegalReviewTime      ( arr[CardsGroup.INDEX_REVIEW_TIME       ] ));
		System.out.println( CardsGroup.isLegalDailyReviewCount( arr[CardsGroup.INDEX_DAILY_REVIEW_COUNT] ));*/

		if( arr.length == CardsGroup.INDEX_NUM_ATTRIBUTES
		 && CardsGroup.isLegalTag             ( arr[CardsGroup.INDEX_TAG               ] )
		 && CardsGroup.isLegalName            ( arr[CardsGroup.INDEX_NAME              ] )
		 && CardsGroup.isLegalReviewDate      ( arr[CardsGroup.INDEX_REVIEW_DATE       ] )
		 && CardsGroup.isLegalBoxNum          ( arr[CardsGroup.INDEX_BOX_NUM           ] )
		 && CardsGroup.isLegalReviewTime      ( arr[CardsGroup.INDEX_REVIEW_TIME       ] )
		 && CardsGroup.isLegalDailyReviewCount( arr[CardsGroup.INDEX_DAILY_REVIEW_COUNT] ) ) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 *  Will return a string ArrayList with all the groups it has found in the file.
	 *  The cards returned will be reviewed by the user.
	 * @param db_file is a File().
	 * @return ArrayList<String>.
	 * Will return null if something went wrong with scanning the database.
	 * Will return an empty ArrayList if there are no group names found. ArrayList.length will return 0. 
	 */
	public static ArrayList<String> readDBGetGroupNames(File db_file, DeckSettings deck_settings){
		/*
		 * Database file layout
		 * All spaces separated with a tab. Even between the sum numbers and operator.
		 * The cards are the last thing to input, this allows the array to have any amount of fields for the card.
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
		ArrayList<String> list = new ArrayList<String>();
		int cards_found  = 0;
		int groups_found = 0;
		
		// Open the file.
		Scanner db;
		
		try {
			db = new Scanner(  db_file );
			System.out.println("Reading database from file: " + db_file.getAbsolutePath());
		
			// Scan through the database.
			while ( db.hasNextLine() ) {
				// Get 1 line of input
				
				String line[] = db.nextLine().split("\t");
				
				if( isDBLineBlank(line) ) {
					continue;
				} else if( isDBLineAGroup( line ) ) {
					groups_found++;
					CardsGroup group = new CardsGroup(line, deck_settings);
					// Get it's name and return it.
					list.add( group.getGroupName() );
				} else if( isDBLineACard( line ) ) {
					cards_found++;
				} else {
					continue;
				}
			}
						
			if( cards_found == 0 || groups_found == 0 ) {
				list = null;
			}

			db.close();

		}
		// Catch for when the file isn't opened.
		//catch( Exception e ) {
		catch ( Exception e ) {
			System.out.println( "Could not find the database file: " + db_file );
			System.out.println( "This could be a problem with another type of exception though.");
			System.out.println( "See the debug info below" );
			e.printStackTrace();
			// Useless return to keep compiler happy, program should have quit on this exception.
			list = null;
		}
		
		return list;
		
	}
	
		
	/***
	 * Will return a Cards() ArrayList for the group matching the 'group_name' parameter.
	 * To get a group by review date, set the 'group_name' parameter to an empty string: ""
	 * @param db_file is a File()
	 * @param deck_settings is DeckSettings()
	 * @param group_name is a String with a name matching that in the DB file.
	 * @return ArrayList<Card>.
	 * Will return null if something went wrong with scanning the database.
	 * Will return an empty ArrayList if there are no reviews needed. ArrayList.length will return 0. 
	 ***/
	public static ArrayList<Card> readDBGetGroup(File db_file, DeckSettings deck_settings, String group_name){
		/*
		 * Database File Layout
		 * Each section is separated by a tab in the file.
		 * Each group line starts with the word "Group", to differentiate it from a normal card line. 
		 *    +---------+--------------+------------+----------+------------+----------+
		 *    |         |              |            |          |            |  Daily   |
		 *    |  Group  |              |   Review   |   Box    |  Review    |  Review  |
		 *    |  Tag    |  Group Name  |    Date    |  Number  |  Time      |  Count   |
		 *    +---------+--------------+------------+----------+------------+----------+
		 *    |  Group  |   Level 1    | 29/10/2013 |    1     |  00:00:00  |  1       |
		 *    |  Group  |   Level 2    | 29/10/2013 |    1     |  00:00:00  |  1       |
		 *    +---------+--------------+------------+----------+------------+----------+
		 */
		
		/*
		 * Database file layout
		 * All spaces separated with a tab. Even between the sum numbers and operator.
		 * The cards are the last thing to input, this allows the array to have any amount of fields for the card.
		 *    +--------------+-------+------------+----------+-----------+
		 *    |              |       |            |  Daily   |           |
		 *    |              |  Box  |  Review    |  Review  |           |
		 *    |  Date        |  Num  |  Time      |  Count   |  Cards    |
		 *    +--------------+-------+------------+----------+---+---+---+
		 *    |  29/10/2013  |  4    |  00:00:00  |  1       | 1 | + | 2 |
		 *    |  29/10/2013  |  4    |  00:00:00  |  1       | 5 | + | 3 |
		 *    +--------------+-------+------------+----------+-----------+
		 */
		ArrayList<Card> deck = new ArrayList<Card>();
		
		// Open the file.
		Scanner db;
		
		try {
			db = new Scanner(  db_file );
			System.out.println("Reading database from file: " + db_file.getAbsolutePath());
			int cards_found  = 0;
			int groups_found = 0;
		
			// Scan through the database normally.
			while ( db.hasNextLine() ) {
				// Get 1 line of input

				String line[] = db.nextLine().split("\t");
				
				if( isDBLineBlank(line) ) {
					continue;
				} else if( isDBLineAGroup(line) ) {
					groups_found++;
					CardsGroup group = new CardsGroup( line, deck_settings );
					Card new_card;
					
					boolean scan_this_group = false;
					if( group_name.compareTo("") == 0) {
						// group name was empty, so get group by review date.
						if ( group.isReviewNeeded() ) {
							scan_this_group = true;
						} else if( group.hasBeenReviewedToday() ) {
							// It's been reviewed, but it's review interval is in the future
							scan_this_group = false;
						} else {
							scan_this_group = false;
						}
					} else {
						// Scan the file for a group with the same name as group_name
						// Used when the user tells the program what group to load.
						if( group.isGroupName(group_name) ) {
							scan_this_group = true;
						} else {
							scan_this_group = false;
						}
					}
					
					if( scan_this_group == true ) {
						String[] empty = { "", "" };
						line = empty;
						// Loop until we find another group
						while( db.hasNextLine() && (isDBLineAGroup(line) == false) ) {
							line = db.nextLine().split("\t");
							
							if( isDBLineACard(line) ) {
								cards_found++;
								new_card = new Card( line, deck_settings, group );

								deck.add( new_card );
							}
						}
						// Break out of the main while loop, since we've added the cards.
						break;
					}
				} else if( isDBLineACard(line) ) {
					cards_found++;
				}
			}
			
			if ( cards_found == 0 || groups_found == 0 ) {
				// There are no cards in the deck, so probably an error.
				deck = null;
				
			}

			db.close();
		}
		// Catch for when the file isn't opened.
		//catch( Exception e ) {
		catch ( Exception e ) {
			System.out.println( "Could not find the database file: " + db_file );
			System.out.println( "This could be a problem with another type of exception though.");
			System.out.println( "See the debug info below" );
			e.printStackTrace();
			// Useless return to keep compiler happy, program should have quit on this exception.
			deck = null;
		}
		
		return deck;
	}
	
	
	/***
	 * Will return a Card() ArrayList for a group that is needing a review
	 * The cards returned will be reviewed by the user.
	 * @param db_file is a File()
	 * @param deck_settings as DeckSettings
	 * @return ArrayList<Card>.
	 * Will return null if something went wrong with scanning the database.
	 * Will return an empty ArrayList if there are no reviews needed. ArrayList.length will return 0. 
	 ***/
	public static ArrayList<Card> readDBGetGroup(File db_file, DeckSettings deck_settings ){
		return readDBGetGroup(db_file, deck_settings, "");
	}
	
	
	/**
	 * Read the database by card's review date.
	 * Will return a Card() ArrayList with all the cards it has found in the file.
	 * The cards returned will be reviewed by the user.
	 * @param db_file is a File()
	 * @param deck_settings as DeckSettings
	 * @return ArrayList<Card>.
	 * Will return null if something went wrong with scanning the database.
	 * Will return an empty ArrayList if there are no reviews needed. ArrayList.length will return 0. 
	 */
	public static ArrayList<Card> readDBGetNormal(File db_file, DeckSettings deck_settings){
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

		 */
		ArrayList<Card> deck = new ArrayList<Card>();
		
		// Open the file.
		Scanner db;
		
		try {
			db = new Scanner(  db_file );
			System.out.println("Reading database from file: " + db_file.getAbsolutePath());
			int cards_counter = 0; // Number of cards added to the deck.
			int cards_found = 0;
		
			// Scan the file

			
			// Scan through the database normally.
			while ( db.hasNextLine() && (cards_counter < deck_settings.getCardLimit()) ) {
				// Get 1 line of input
				
				String line[] = db.nextLine().split("\t");
				
				if( isDBLineBlank(line) ) {
					continue;
				} else if( isDBLineAGroup(line) ) {
					// Not using groups, so just skip this line.
					continue;
				} else if( isDBLineACard(line) ) {
					// Group object added to complete the initialiser of the new_card object.
					// It's a blank group object.
					CardsGroup group = new CardsGroup(deck_settings );
					Card new_card = new Card( line, deck_settings, group );
					cards_found++;
					if ( new_card.isReviewNeeded() ) {
						deck.add(new_card);
						cards_counter++;
					}
				}
			}
			
			if ( cards_found == 0 ) {
				// There are no cards in the deck, so probably an error.
				deck = null;
			} else if ( deck.size() < deck_settings.getCardLimit() ) {
				// The deck is too small, so return an empty list.
				deck = new ArrayList<Card>();
			}

			db.close();

		}
		// Catch for when the file isn't opened.
		//catch( Exception e ) {
		catch ( Exception e ) {
			System.out.println( "Could not find the database file: " + db_file );
			System.out.println( "This could be a problem with another type of exception though.");
			System.out.println( "See the debug info below" );
			e.printStackTrace();
			// Useless return to keep compiler happy, program should have quit on this exception.
			deck = null;
		}
		
		return deck;
	}
	
	public static ArrayList<Card> readDB(File db_file, DeckSettings deck_settings){
		if( deck_settings.isGroupMode() ) {
			// See if we get cards by by group name or review date.
			if( deck_settings.isGroupReviewDate() ) {
				return readDBGetGroup(db_file, deck_settings);
			} else {
				// Get by group name. Since we don't know what group to get, just return the first one.
				ArrayList<String> groups = readDBGetGroupNames(db_file, deck_settings);
				
				if( groups == null ) {
					// There was some problem getting the deck, so return null.
					// This will be used to telll the user there's an error in the deck.
					return null;
					
				} else if( groups.size() == 0 ) {
					// The deck is too small, so return an empty list.
					return new ArrayList<Card>();
					
				} else {
					return readDBGetGroup(db_file, deck_settings, groups.get(0));
				}
			}
		} else {
			// it's not group mode, read the database by card's review date.
			return readDBGetNormal(db_file, deck_settings);
		}
	}
	
	public static void writeDB(File db_file, ArrayList<Card> learnt_deck, DeckSettings deck_settings){
		if( deck_settings.isGroupMode() ) {
			writeDBGroup(db_file, learnt_deck, deck_settings);
		} else {
			// it's not group mode, read the database by card's review date.
			writeDBNormal(db_file, learnt_deck, deck_settings);
		}
	}

	public static void writeDB(String db_file_path, ArrayList<Card> learnt_deck, DeckSettings deck_settings){
		File db_file = new File(db_file_path);
		writeDB( db_file, learnt_deck, deck_settings );
	}
	
	/**
	 * Will overwrite the old database file with the learnt_deck's cards.
	 * Overwrites for the chosen group. 
	 * @param db_file is a File()
	 * @param learnt_deck is ArrayList<Card>
	 * @param deck_settings is DeckSettings
	 */
	public static void writeDBGroup(File db_file, ArrayList<Card> learnt_deck, DeckSettings deck_settings) {
		// Will copy the whole file to a temporary file, and write the new values to it as it's writing.
		// Then delete the old file and replace with the new one.

		// Try to open the database file.
		Scanner db;
		try {
			// Open the database file
			File db_tmp_file = new File(db_file.getAbsolutePath() + ".tmp");
			db = new Scanner( db_file );


			// Make/Open the temp file.
			if (!db_tmp_file.exists()) {
					try {
						db_tmp_file.createNewFile();
					} catch (IOException e) {
						System.out.println("Couldn't make the file: " + db_tmp_file.getAbsolutePath());
						e.printStackTrace();
					}
			}

			if (!db_tmp_file.exists()) {
				db_tmp_file.createNewFile();

			}
			
			
			FileWriter fw = new FileWriter(db_tmp_file, false);
			BufferedWriter bw_new_db = new BufferedWriter(fw);
			
			// Scan the file
			boolean group_found   = false; // Will become true when we find our first groupa in the while loop
			while ( db.hasNextLine() ) {
				
				String line = db.nextLine();
				String line_arr[] = line.split("\t");
				
				Card db_card;
				
				
				if( isDBLineBlank( line_arr ) ) {
					bw_new_db.write( line );
					bw_new_db.newLine();
					continue;
				} else if( isDBLineAGroup( line_arr ) ) {
					if ( group_found == true ) {
						// Reset it, since we've found another group.
						// This stops errors with finding similar cards in a different group
						// This should never happen, since the learnt_deck.size() would be 0, but better to be safe than sorry
						group_found = false;
					}
					// Make a blank Card() with just the deck settings.
					CardsGroup group = new CardsGroup( line_arr, deck_settings);
						
					if( learnt_deck.size() > 0
					 && group.compareOrigTo( learnt_deck.get(0).group ) ) {
						// Group found, so write cards for it on the loop's next iteration.
						group_found = true;

						// Write the updated group line. e.g. review date, box num.
						bw_new_db.write( learnt_deck.get(0).group.toString() );
						bw_new_db.newLine();
						continue;
					} else {
						// Group not found, so just write the line.
						bw_new_db.write( line );
						bw_new_db.newLine();
						continue;
					}
				} else if ( isDBLineACard( line_arr) ) {
					if( learnt_deck.size() > 0
					 && group_found == true ) {
						// Passed this card the learnt_deck's group, because it is required for the initialization, not neccessarily needed though.
						db_card = new Card( line_arr, deck_settings, learnt_deck.get(0).group);
						for( int i = 0; i < learnt_deck.size(); i++ ) {
							if( db_card.compareOrigTo( learnt_deck.get(i) ) ) {
								// Write the line
								bw_new_db.write( learnt_deck.get(i).toString() );
								bw_new_db.newLine();
								
								// Remove the card from the learnt_deck, since we don't need to match it again.
								// This also stops it from matching an identical card.
								// Useful for a quick hack to make a deck that repeats a lot.
								learnt_deck.remove(i);
								break;
							}
						}
					} else {
						// Just write the line as normal.
						bw_new_db.write( line );
						bw_new_db.newLine();
						continue;
					}
				} else {
					// There's something wrong with this line, and it could cause problems, so comment it out.
					String comment = "# This line has errors, commented it out. Line starts after this hash #";
					
					// Add the comment to the original line.
					String tmp_line = comment + line;
					
					bw_new_db.write( tmp_line );
					bw_new_db.newLine();
					continue;
				}
			}
			
			db.close();
			bw_new_db.close();
			
			// Replace the old database file with the new one.
			File old_db_file = new File(db_file.getAbsolutePath());
			File new_db_file = new File(db_file.getAbsolutePath() + ".tmp");
			
			db_file.delete();
			db_tmp_file.renameTo(db_file);
			new_db_file.renameTo(old_db_file);
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
	
	/**
	 * Will overwrite the old database file with the learnt_deck's cards. 
	 * @param db_file is File()
	 * @param learnt_deck is ArrayList<Card>
	 */
	public static void writeDBNormal(File db_file, ArrayList<Card> learnt_deck, DeckSettings deck_settings) {
		// Will copy the whole file to a temporary file, and write the new values to it as it's writing.
		// Then delete the old file and replace with the new one.

		// Try to open the database file.
		Scanner db;
		try {
			// Open the database file
			File db_tmp_file = new File(db_file.getAbsolutePath() + ".tmp");
			db = new Scanner( db_file );

			// Make/Open the temp file.
			if (!db_tmp_file.exists()) {
					try {
						db_tmp_file.createNewFile();
					} catch (IOException e) {
						System.out.println("Couldn't make the file: " + db_tmp_file.getAbsolutePath());
						e.printStackTrace();
					}
			}

			if (!db_tmp_file.exists()) {
				db_tmp_file.createNewFile();
			}
			
			
			FileWriter fw = new FileWriter( db_tmp_file, false );
			BufferedWriter bw_new_db = new BufferedWriter( fw );
			
			// Scan the file
			while ( db.hasNextLine() ) {
				String line = db.nextLine();
				String line_arr[] = line.split("\t");
				
				Card db_card;
				
				if( isDBLineBlank( line_arr )
				 || isDBLineAGroup(line_arr) ) {
					// Just write the line and move on to the next.
					bw_new_db.write( line );
					bw_new_db.newLine();
					continue;
				} else if ( isDBLineACard( line_arr) ) {
					boolean is_line_written = false;
					if( learnt_deck.size() > 0 ) {
						// Make a blank group, just to initialise the new card with.
						CardsGroup group = new CardsGroup(deck_settings);
						db_card = new Card( line_arr, deck_settings, group );
						for( int i = 0; i < learnt_deck.size(); i++ ) {
							if( db_card.compareOrigTo( learnt_deck.get(i) ) ) {
								is_line_written = true;
								// Write the line
								bw_new_db.write( learnt_deck.get(i).toString() );
								bw_new_db.newLine();
								
								// Remove the card from the learnt_deck, since we don't need to match it again.
								// This also stops it from matching an identical card.
								// Useful for a quick hack to make a deck that repeats a lot.
								learnt_deck.remove(i);
								break;
							}
						}
					}
					if( is_line_written == false ) {
						// Just write the line as normal.
						bw_new_db.write( line );
						bw_new_db.newLine();
						continue;
					}
				} else {
					// There's something wrong with this line, and it could cause problems, so comment it out.
					String comment = "# This line has errors, commented it out. Line starts after this hash #";
					
					// Add the comment to the original line.
					String tmp_line = comment + line;
					
					bw_new_db.write( tmp_line );
					bw_new_db.newLine();
					continue;
				}
			}
			
			db.close();
			bw_new_db.close();
			
			// Replace the old database file with the new one.
			File old_db_file = new File(db_file.getAbsolutePath());
			File new_db_file = new File(db_file.getAbsolutePath() + ".tmp");
			
			db_file.delete();
			db_tmp_file.renameTo(db_file);
			new_db_file.renameTo(old_db_file);
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
	
	/**
	 * Will convert "yes", "no", "true" and "false" Strings to a boolean value.
	 * Will return false if it's encountered an error, so bewarned!!!
	 * @param str
	 * @return
	 */
	public static boolean strToBool(String str) {
		/****/if( str.toLowerCase(Locale.US).trim().compareTo("yes")  == 0 ) {
			return true;
		}
		else if( str.toLowerCase(Locale.US).trim().compareTo("true")  == 0 ) {
			return true;
		}
		else if( str.toLowerCase(Locale.US).trim().compareTo("no")    == 0 ) {
			return false;
		}
		else if( str.toLowerCase(Locale.US).trim().compareTo("false") == 0 ) {
			return false;
		} else {
			System.out.println("String passed to strToBool() is not the correct type.");
			return false;
		}
	}
	
	public static String boolToStr(boolean b) {
		if( b ) {
			return "yes";
		} else {
			return "no";
		}
	}
}
