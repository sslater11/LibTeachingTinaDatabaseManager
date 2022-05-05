/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;

import java.util.Date;
import java.util.Locale;

public class CardsGroup {
	public static final int INDEX_TAG                  = 0; // This is the tag at the start of the line that indicates it will be a group line.
	public static final int INDEX_NAME                 = 1;
	public static final int INDEX_REVIEW_DATE          = 2;
	public static final int INDEX_BOX_NUM              = 3;
	public static final int INDEX_REVIEW_TIME          = 4;
	public static final int INDEX_DAILY_REVIEW_COUNT   = 5;
	public static final int INDEX_NUM_ATTRIBUTES       = 6;
	
	public static final String DEFAULT_GROUP_TAG = "Group";

	protected boolean has_been_updated = false; // Check if the update() method has been run yet.
	
	protected String group_name;
	protected String review_date;
	protected String review_time;
	protected float  box_num;
	protected int    daily_review_count;
	
	protected String orig_review_date;
	protected String orig_review_time;
	protected float  orig_box_num; // -1 for uninitialised check in setter method.
	protected int    orig_daily_review_count; // -1 for uninitialised check in setter method.
	public DeckSettings settings;


	/**
	 * Make a blank group with just the group name.
	 * @param group_name
	 * @param settings
	 */
	public CardsGroup(String group_name, DeckSettings settings) {
		settings = settings;
		setName( group_name );
		setBoxNum();
		setReviewDate();
		setReviewTime();
		setDailyReviewCount();
	}

	/**
	 * Make a blank group.
	 * @param settings
	 */
	CardsGroup(DeckSettings s) {
		// Make a blank card group, so the user can set everything
		settings = s;
		
		setBoxNum();
		setName("Blank Group");
		setReviewDate();
		setReviewTime();
		setDailyReviewCount();
	}

	
	/**
	 * 
	 * @param settings
	 * @param review_date
	 * @param box_num
	 * @param review_time
	 * @param daily_review_count
	 */
	//CardsGroup(DeckSettings s, String review_date, float box_num, String review_time, int daily_review_count) {
	//	settings = s;
	//}
	/**
	 * 
	 * @param arr[]
	 * @param settings
	 */
	CardsGroup ( String arr[], DeckSettings s ) {
		settings = s;
		if ( (arr.length - INDEX_NUM_ATTRIBUTES) >= 0 ) {
			setOrigReviewDate      ( arr[INDEX_REVIEW_DATE]           );
			setOrigBoxNum          ( arr[INDEX_BOX_NUM]               );
			setOrigReviewTime      ( arr[INDEX_REVIEW_TIME]           );
			setOrigDailyReviewCount( arr[INDEX_DAILY_REVIEW_COUNT]    );

			setName            ( arr[INDEX_NAME]                  );
			setReviewDate      ( arr[INDEX_REVIEW_DATE]           );
			setBoxNum          ( arr[INDEX_BOX_NUM]               );
			setReviewTime      ( arr[INDEX_REVIEW_TIME]           );
			setDailyReviewCount( arr[INDEX_DAILY_REVIEW_COUNT]    );
			
			// If the review date is not today, then reset the card's daily_review_count and review_time.
			if ( MyDate.compare( getReviewDate(), 0 ) < 0 ) {
				setDailyReviewCount();
				setReviewTime();
			}
		} else {
			// Whoopsie, probably a bad database file.

			// No longer needed, because the variables are not final anymore
			// Just initialise these final variables to anything, to keep the compiler happy.
			//orig_review_date        = null;
			//orig_review_time        = null;
			//orig_box_num            = -1;
			//orig_daily_review_count =  0;

			System.out.println("Error in class 'CardGroup', at constructor. Array passed has too few elements.");
			System.out.println("Array must have at least "+ INDEX_NUM_ATTRIBUTES + " elements");
			System.out.println("Array only has " + arr.length + " elements.");
			System.exit(600);
		}
	}

	@Override
	public String toString() {
		String line;
		line = DEFAULT_GROUP_TAG;
		line += "\t" + getGroupName();
		line += "\t" + getReviewDate();
		line += "\t" + getBoxNum();
		line += "\t" + getReviewTime();
		line += "\t" + getDailyReviewCount();
		
		return line;
	}


	public static boolean isReviewNeeded(DeckSettings settings, String date, float box_num, String time, int daily_review_count) {
		return Card.isReviewNeeded( settings, date, box_num, time, daily_review_count );
	}
	
	public boolean isReviewNeeded() {
		return isReviewNeeded( settings, getReviewDate(), getBoxNum(), getReviewTime(), getDailyReviewCount() );
	}
	
	// TODO: old code for my daily review. Keep for future reference.
	/*
	public boolean isReviewIntervalOver() {
		return Card.isReviewIntervalOver( settings, getReviewTime() );
	}
	public static boolean isReviewIntervalOver(DeckSettings settings, String time) {
		return Card.isReviewIntervalOver( settings, time );
	}*/

	
	
	public static boolean isLegalTag( String str ) {
		String tag = DEFAULT_GROUP_TAG;
		tag = tag.toLowerCase(Locale.US);
		tag = tag.trim();
		
		str = str.toLowerCase(Locale.US);
		str = str.trim();
		 if ( str.compareTo(tag) == 0 ) {
			 return true;
		 } else {
			 return false;
		 }

	}
	
	public static boolean isLegalName( String str ) {
		 if ( str.length() > 1 ) {
			 return true;
		 } else {
			 return false;
		 }

	}

	public static boolean isLegalReviewDate(String str) {
		return Card.isLegalReviewDate(str);
	}
	
	public static boolean isLegalReviewTime(String str) {
		return Card.isLegalReviewTime(str);
	}
	
	public static boolean isLegalBoxNum(float box) {
		return Card.isLegalBoxNum( box );
	}
	public static boolean isLegalBoxNum(String box) {
		return Card.isLegalBoxNum( box );
	}
	
	public static boolean isLegalDailyReviewCount( int num ) {
		return Card.isLegalDailyReviewCount( num );
	}
	public static boolean isLegalDailyReviewCount( String str ) {
		return Card.isLegalDailyReviewCount( str );
	}

	public boolean hasBeenReviewedToday() {
		return hasBeenReviewedToday( settings, getReviewDate(), getBoxNum(), getReviewTime(), getDailyReviewCount() );
	}
	
	public static boolean hasBeenReviewedToday(DeckSettings settings, String date, float box_num, String time, int daily_review_count) {
		return Card.hasBeenReviewedToday( settings, date, box_num, time, daily_review_count );
	}


	public boolean hasBeenUpdated() {
		return has_been_updated;
	}

	public String getGroupName() {
		return group_name;
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

	public int getOrigDailyReviewCount() {
		return orig_daily_review_count;
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



	public String getOrigBoxNumString() {
		return "" + orig_box_num;
	}



	public void setName(String str) {
		group_name = str;
	}



	public void setReviewDate(String str) {
		if( MyDate.isLegalDate( str ) ) {
			review_date = str;
		} else {
			review_date = Card.DEFAULT_REVIEW_DATE;
		}
	}
	public void setReviewDate(Date new_date) {
		setReviewDate( MyDate.toString(new_date));
	}
	public void setReviewDate() {
		setReviewDate( MyDate.today() );
	}



	public void setReviewTime(String new_time) {
		if( MyDate.isLegalTime(new_time)) {
			review_time = new_time;
		} else {
			review_time = Card.DEFAULT_REVIEW_TIME;
		}
	}
	public void setReviewTime(Date new_time) {
		setReviewTime( MyDate.timeToString(new_time) );
	}
	public void setReviewTime() {
		setReviewTime( Card.DEFAULT_REVIEW_TIME );
	}



	public void setDailyReviewCount( int count ) {
		if( count < 0 ) {
			daily_review_count = Card.DEFAULT_DAILY_REVIEW_COUNT;
		} else {
			daily_review_count = count;
		}
	}
	public void setDailyReviewCount( String str ) {
		setDailyReviewCount( Integer.valueOf(str));
	}
	public void setDailyReviewCount() {
		setDailyReviewCount(Card.DEFAULT_DAILY_REVIEW_COUNT);
	}



	public void setBoxNum(float num) {
		if( num < 0 ) {
			// It's probably -1, uninitialised.
			box_num = Card.DEFAULT_BOX_NUM;
		} else {
			box_num = num;
		}
	}
	public void setBoxNum(String box) {
		setBoxNum( Float.valueOf(box) );
	}
	public void setBoxNum() {
		setBoxNum( Card.DEFAULT_BOX_NUM );
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

	
	
	public boolean isGroupName( String str ) {
		if( getGroupName().compareTo(str) == 0) {
			return true;
		} else {
			return false;
		}
	}


	public boolean compareOrigTo(CardsGroup other_group) {
		String str_date1 =             getOrigReviewDate();
		String str_date2 = other_group.getOrigReviewDate();
		
		String str_time1 =             getOrigReviewTime();
		String str_time2 = other_group.getOrigReviewTime();
		
		String str_name1 =             getGroupName();
		String str_name2 = other_group.getGroupName();
		if( str_date1.compareTo(str_date2) != 0 ) {
			return false;
			
		} else if( str_time1.compareTo(str_time2) != 0 ) {
			return false;

		} else if( str_name1.compareTo(str_name2) != 0 ) {
			return false;
	
		} else if( getOrigBoxNum() != other_group.getOrigBoxNum() ) {
			return false;
			
		} else if( getOrigDailyReviewCount() != other_group.getOrigDailyReviewCount() ) {
			return false;
			
		} else {
			// Both card's data matched up.
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
		if( has_been_updated == false ) {
			has_been_updated = true;
			
			// Only update if it's group mode, AND the group was chosen by review date, not by group name.
			if( settings.isGroupMode() ) {
				if( settings.isGroupReviewDate() ) {
					setReviewDate ( MyDate.toString( MyDate.today() ) );
					setReviewTime ( MyDate.timeToString( MyDate.currentTime() ) );
					setDailyReviewCount( getDailyReviewCount() + 1 );
					
					// Only increment the box_num once a day.
					if( getDailyReviewCount() ==  1 ) {
						setBoxNum( getBoxNum() * Card.BOX_NUM_MULTIPLIER );
					}
				} else {
					/* Group was chosen by it's name.
					 * I don't think we need to do anything, since the group was chosen by name.
					 * This mode doesn't write to a database, because it'll mess up the box number algorithm.
					 */
				}
			}
		}
	}
}
