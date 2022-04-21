/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;

import java.util.Date;

public class Card {
	// These constants are for accessing the 'contents' array.
	public static final int INDEX_REVIEW_DATE           = 0;
	public static final int INDEX_BOX_NUM               = 1;
	public static final int INDEX_REVIEW_TIME           = 2;
	public static final int INDEX_DAILY_REVIEW_COUNT    = 3;
	public static final int INDEX_NUM_ATTRIBUTES        = 4;
	
	public static final float BOX_NUM_MULTIPLIER = (float) 1.4;
	
	protected final int NUM_MULTIPLIER = 1; // Number of times the card is added to the deck's card list.
	
	public static final int    DEFAULT_BOX_NUM = 1; /* when update() is run, all box numbers will be
	                                             * multiplied by BOX_NUM_MULTIPILER, so the
	                                             * default box number is really (1 * 1.4) = 1.4
	                                             */
	public static final String DEFAULT_REVIEW_DATE = "01/01/2014";
	public static final String DEFAULT_REVIEW_TIME = "00:00:00"; // Set the default time to midnight, since it's the earliest time in the day
	public static final int    DEFAULT_DAILY_REVIEW_COUNT = 0; // This will be incremented by 1 on update(), so it's really equal to 1.
	
	protected String review_date;
	protected String review_time;
	protected float box_num;
	protected int daily_review_count;
	protected int multiplier = NUM_MULTIPLIER; // Number of times the card is added to the deck's card list, used to make it stand out more.
	
	// Store the data found in the database.
	// Then it can be used later on to compare the reviewed cards
	protected String orig_review_date;
	protected String orig_review_time;
	protected float orig_box_num;
	protected int orig_daily_review_count;
	
	protected String contents[]; // The question and answer the card holds.
	protected int fails_count     = 0;
	protected int successes_count = 0;

	protected boolean has_this_been_updated = false; // Check if the update() method has been run yet.
	
	protected DeckSettings settings;
	public CardsGroup group;
	
	/**
	 * Making a blank card, which the user has to change everything.
	 */
	Card(DeckSettings s, CardsGroup g) {
		settings = s;
		group = g;
		review_date             = null;
		review_time             = null;
		orig_review_date        = null;
		orig_review_time        = null;

		box_num                 = -1;
		orig_box_num            = -1;
		
		daily_review_count      = -1;
		orig_daily_review_count = -1;
	}
	
	Card ( String line, String delimiter, DeckSettings s, CardsGroup g ) {
		this(line.split(delimiter), s, g);
	}
	
	Card ( String line, DeckSettings s, CardsGroup g ) {
		this(line, "\t", s, g);
	}
	
	Card ( String arr[], DeckSettings s, CardsGroup g) {
		settings = s;
		group = g;
		if ( (arr.length - INDEX_NUM_ATTRIBUTES) > 0 ) {
			setOrigReviewDate      ( arr[INDEX_REVIEW_DATE]           );
			setOrigReviewTime      ( arr[INDEX_REVIEW_TIME]           );
			setOrigBoxNum          ( arr[INDEX_BOX_NUM]               );
			setOrigDailyReviewCount( arr[INDEX_DAILY_REVIEW_COUNT]    );
			
			setReviewDate      ( arr[INDEX_REVIEW_DATE]           );
			setReviewTime      ( arr[INDEX_REVIEW_TIME]           );
			setBoxNum          ( arr[INDEX_BOX_NUM]               );
			setDailyReviewCount( arr[INDEX_DAILY_REVIEW_COUNT]    );
			
			// If the review date is not today, then reset the card's daily_review_count and review_time.
			if ( MyDate.compare( getReviewDate(), 0 ) < 0 ) {
				setDailyReviewCount();
				setReviewTime();
			}

			
			contents = new String[(arr.length - INDEX_NUM_ATTRIBUTES)];
			// Put the rest of the arr's contents into the 'contents' array.
			for (int i = INDEX_NUM_ATTRIBUTES, k = 0;  i < arr.length;  i++, k++ ) {
				contents[k] = arr[i];
			}
		} else {
			// Whoopsie, probably a bad database file.

			// No longer needed, because these variables are no longer final.
			// Just initialise these final variables to anything, to keep the compiler happy.
			//orig_review_date        = null;
			//orig_review_time        = null;
			//orig_box_num            = -1;

			System.out.println("Error in class 'Card', at constructor. Array passed has too few elements.");
			System.out.println("Array must have at least "+ INDEX_NUM_ATTRIBUTES + " elements");
			System.out.println("Array only has " + arr.length + " elements.");
			System.exit(600);
		}
	}

	public static boolean isLegalReviewDate(String str) {
		return MyDate.isLegalDate(str);
	}
	
	public static boolean isLegalReviewTime(String str) {
		return MyDate.isLegalTime(str);
	}

	
	public static boolean isLegalBoxNum(float box) {
		if( box >= 1 ) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isLegalBoxNum( String str ) {
		 return isLegalBoxNum ( Float.valueOf( str )    );

	}
	
	
	public static boolean isLegalDailyReviewCount(int num) {
		if( num >= -1 ) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isLegalDailyReviewCount( String str ) {
		 return isLegalDailyReviewCount( Integer.valueOf( str ) );
	}

	public String getOrigReviewDate() {
		return orig_review_date;
	}
	
	public String getOrigReviewTime() {
		return orig_review_time;
	}
	
	public float getOrigBoxNum() {
		return orig_box_num;
	}
	
	public int getOrigDailyReviewCount() {
		return orig_daily_review_count;
	}
	
	public String getReviewDate() {
		return review_date;
	}
	
	public String getReviewTime() {
		return review_time;
	}
	
	public float getBoxNum() {
		return box_num;
	}
	
	public int getDailyReviewCount() {
		return daily_review_count;
	}
	
	public int getSuccessCount() {
		return successes_count;
	}
	
	public int getMultiplier() {
		return multiplier;
	}

	public void setReviewDate(String str) {
		if( MyDate.isLegalDate( str ) ) {
			review_date = str;
		} else {
			review_date = DEFAULT_REVIEW_DATE;
		}
	}
	public void setReviewDate(Date new_date) {
		setReviewDate( MyDate.toString(new_date) );
	}
	public void setReviewDate() {
		setReviewDate( DEFAULT_REVIEW_DATE );
	}
	
	
	public void setReviewTime(String new_time) {
		if( MyDate.isLegalTime(new_time)) {
			review_time = new_time;
		} else {
			review_time = DEFAULT_REVIEW_TIME;
		}
	}
	public void setReviewTime(Date new_time) {
		setReviewTime( MyDate.timeToString(new_time) );
	}
	public void setReviewTime() {
		setReviewTime( DEFAULT_REVIEW_TIME );
	}

	
	public void setBoxNum(float num) {
		if( num < 0 ) {
			// It's probably -1, uninitialised.
			box_num = DEFAULT_BOX_NUM;
		} else {
			box_num = num;
		}
	}
	public void setBoxNum(String box) {
		setBoxNum( Float.valueOf(box) );
	}
	
	
	public void setMultiplier(int multiply ) {
		if( multiplier >= 1 ) {	
			multiplier  = multiply;
		} else {
			System.out.println("error in setMultiplier(). Number was less than 1, so setting it to 1");
			multiplier = 1;
		}
	}
	
	public void setDailyReviewCount( int num ) {
		if( num >= 0 ) {
			daily_review_count = num;
		} else {
			System.out.println("there was a problew with setDailyReviewCount( num ) to 0");
			daily_review_count = 0;
		}
	}
	public void setDailyReviewCount( String str ) {
		setDailyReviewCount( Integer.valueOf(str) );
	}
	public void setDailyReviewCount() {
		// Default to 0.
		setDailyReviewCount( DEFAULT_DAILY_REVIEW_COUNT );
	}
	public void setOrigReviewDate(String str) {
		orig_review_date = str;
	}
	public void setOrigReviewDate(Date new_date) {
		setOrigReviewDate( MyDate.toString(new_date));
	}

	
	public void setOrigReviewTime(String new_time) {
		orig_review_time = new_time;
	}
	public void setOrigReviewTime(Date new_time) {
		setOrigReviewTime( MyDate.timeToString(new_time) );
	}

	
	public void setOrigBoxNum(float num) {
		orig_box_num = num;
	}
	public void setOrigBoxNum(String box) {
		setOrigBoxNum( Float.valueOf(box) );
	}
	
	
	public void setOrigDailyReviewCount( int count ) {
		orig_daily_review_count = count;
	}
	public void setOrigDailyReviewCount( String str ) {
		setOrigDailyReviewCount( Integer.valueOf(str));
	}

	public void failed() {
		fails_count++;
		if( fails_count >= settings.getFailLimit() ) {
			setBoxNum(DEFAULT_BOX_NUM);
			// Only update the group's box number if it's a group mode, AND it's got it's group by review date, not by group name.
			if( settings.isGroupMode() && settings.isGroupReviewDate() ) {
				group.setBoxNum(DEFAULT_BOX_NUM);
			}
		}
		successes_count = 0;
	}
	
	public void success() {
		successes_count++;
	}
	
	public boolean isLearnt() {
		if ( successes_count >= settings.getSuccessLimit() ) {
			return true;
		} else {
			return false;
		}

	}
	
	public boolean hasThisBeenUpdated() {
		return has_this_been_updated;
	}
	
	public String getContent(int idx) {
		if ( idx < contents.length ) {
			return contents[idx];
		} else {
			return "";
		}
	}
	
	public String toString() {
		String line;
		line  =        getReviewDate();
		line += "\t" + getBoxNum();
		line += "\t" + getReviewTime();
		line += "\t" + getDailyReviewCount();
		
		for( int i = 0; i < contents.length; i++ ) {
			line += "\t" + contents[i];
		}
		
		return line;
	}
	
	public static String makeDBLine(String front, String back) {
		/*
		 * Database file layout
		 *
		 *    +--------------+-------+------------+----------+-----------+-----------+
		 *    |              |       |            |  Daily   |           |           |
		 *    |              |  Box  |  Review    |  Review  |           |           |
		 *    |  Date        |  Num  |  Time      |  Count   |  Front    |  back     |
		 *    +--------------+-------+------------+----------+-----------+-----------+
		 *    |  29/10/2013  |  4    |  00:00:00  |  1       | testing   | result    |
		 *    |  29/10/2013  |  4    |  00:00:00  |  1       | test2     | result2   |
		 *    +--------------+-------+------------+----------+-----------+-----------+
		 */

		Date today = MyDate.today();
		String review_date = MyDate.toString( today );
		String review_time = MyDate.timeToString( today );
		int box_num = DEFAULT_BOX_NUM;
		int daily_review_count = DEFAULT_DAILY_REVIEW_COUNT;
		
		String separator = "\t";
		
		String line = review_date        + separator + 
		              box_num            + separator + 
		              review_time        + separator + 
		              daily_review_count + separator + 
		              front              + separator + 
		              back;
		
		return line;
	}

	
	// TODO: old code for my daily review, keep for future reference
	/*
	public boolean isReviewIntervalOver() {
		return isReviewIntervalOver( settings, getReviewTime() );
	}
	public static boolean isReviewIntervalOver(DeckSettings settings, String time) {
		int mins = settings.getReviewInterval();
		// Check the review interval, to see if enough time has passed since the last review.
		if( MyDate.compareTime(time, mins) <= 0) {
			return true;
		} else {
			return false;
		}
		
	}*/

	public boolean isReviewNeeded() {
		return isReviewNeeded( settings, getReviewDate(), getBoxNum(), getReviewTime(), getDailyReviewCount() );
	}
	
	public static boolean isReviewNeeded(DeckSettings settings, String date, float box_num, String time, int daily_review_count) {
		int days = Math.round( box_num );
		// TODO: old code for my daily review. Keep for future reference.
		//int mins = settings.getReviewInterval();
	
		if( MyDate.compare(date, 0) == 0 ) {
			// TODO: This commented out code is the old code for checking if a review is needed. keep it for future reference.
			/*
			// The card is for today.
			// Check if the review count and interval allow a review.
			if( daily_review_count < settings.getDailyReviewLimit() ) {
				// Review count is lower than the limit, so it might need reviewing.
				
				// Check the review interval, to see if enough time has passed since the last review.
				if( isReviewIntervalOver(settings, time) ) {
					return true;
				}
			}
			*/
			// All the other checks failed, so return false.
			return false;
		} else if ( MyDate.compare(date, days) <= 0 ) {
			//System.out.println( MyDate.compare(date, days) + "      " + date + "   " + box_num + "   " + time + "   " +  daily_review_count);
			// Card's box number and date indicates the card is old, and will need a review
			return true;
		} else {
			// The card is to be reviewed in the future.
			return false;
		}
	}
	
	public boolean hasBeenReviewedToday() {
		return hasBeenReviewedToday( settings, getReviewDate(), getBoxNum(), getReviewTime(), getDailyReviewCount() );
	}
	
	public static boolean hasBeenReviewedToday(DeckSettings settings, String date, float box_num, String time, int daily_review_count) {
		if( MyDate.compare(date, 0) == 0 ) {
			// The card was last reviewed today.
			return true;
		} else {
			// The card was not reviewed today.
			return false;
		}
	}
	//public boolean isSameAs( Card other_card ) {
	/**
	 * Will compare the original values to that of another card's original values.
	 * @param other_card of type 'Card'
	 * @return boolean as true, if cards have the same content.
	 */
	public boolean compareOrigTo( Card other_card ) {
		
		String str_date1 =            getOrigReviewDate();
		String str_date2 = other_card.getOrigReviewDate();
		
		String str_time1 =             getOrigReviewTime();
		String str_time2 = other_card.getOrigReviewTime();
		
		if( str_date1.compareTo(str_date2) != 0 ) {
			return false;

		} else if( str_time1.compareTo(str_time2) != 0 ) {
			return false;

		} else if( getOrigBoxNum() != other_card.getOrigBoxNum() ) {
			return false;

		} else if( getOrigDailyReviewCount() != other_card.getOrigDailyReviewCount() ) {
			return false;

		} else {
			// Both card's data matches up so far.
			// Now check the cards contents against each other.
			for( int i = 0; i < contents.length; i++ ) {
				String str1 =            getContent(i);
				String str2 = other_card.getContent(i);
				if ( str1.compareTo(str2) != 0) {
					return false;
				}
			}

			return true;
		}
	}
	
	/**
	 * Will update the card's values for the:
	 *     Review Date
	 *     Review Time
	 *     Box Number
	 *     Daily Review Count
	 */
	public void updateStudyData() {
		group.updateStudyData();

		if( has_this_been_updated == false ) {
			has_this_been_updated = true;
			
			setReviewDate( MyDate.toString( MyDate.today() ) );
			setReviewTime( MyDate.timeToString( MyDate.currentTime() ) );
			setDailyReviewCount( getDailyReviewCount() + 1 );
			
			// Only update if it's group mode, AND the group was chosen by review date, not by group name.
			if( settings.isGroupMode() ) {
				if( settings.isGroupReviewDate() ) {
					
					// Only update the card's box number, if it's <= group's box number.
					// This stops the user from completing the deck, and making the card's box number get too big.
					// They would be able to practice daily, and get one card in the group wrong, resetting the groups' box number, and incrementing the others.
					// Repeated daily to a ridiculously high box number.
					if( getBoxNum() < group.getBoxNum() ) {
						// Only increment the box_num once a day.
						if( getDailyReviewCount() ==  1 ) {
							setBoxNum( getBoxNum() * BOX_NUM_MULTIPLIER );
						}
					}
				} else {
					/* Group was chosen by it's name.
					 * I don't think we need to do anything, since the group was chosen by name.
					 * This mode doesn't write to a database, because it'll mess up the box number algorithm.
					 */
				}
			} else {
				// Only increment the box_num once a day.
				if( getDailyReviewCount() ==  1 ) {
					setBoxNum( getBoxNum() * BOX_NUM_MULTIPLIER );
				}
			}
		}
	}
}
