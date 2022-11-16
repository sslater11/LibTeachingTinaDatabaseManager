/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package libteachingtinadbmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import libteachingtinadbmanager.Card;

public class ReadingLessonCard {
	public static final int DEFAULT_SUCCESS_LIMIT = 5;
	public static final float BOX_NUM_MULTIPLIER = (float) 1.4;
	public static final int    DEFAULT_BOX_NUM = 1; /* when update() is run, all box numbers will be
	 * multiplied by BOX_NUM_MULTIPILER, so the
	 * default box number is really (1 * 1.4) = 1.4
	 */

	int card_id;
	long date_in_millis;
	float box_num;
	int reading_lesson_level;
	String sound_type;
	String sound_word_or_sentence;
	int id_of_linked_card;
	boolean is_spelling_mode;


	String card_text;
	String card_images;
	String card_audio;
	String card_read_along_timings;

	int failed_count = 0;
	int successes_count = 0;

	boolean has_been_updated = false;

	public ReadingLessonCard(
			int card_id,
			long date_in_millis,
			float box_num,
			int reading_lesson_level,
			String sound_type,
			String sound_word_or_sentence,
			int id_of_linked_card,
			boolean is_spelling_mode,

			String card_text,
			String card_images,
			String card_audio,
			String card_read_along_timings
	)
	{
		this.card_id                  = card_id;
		this.date_in_millis         = date_in_millis;
		this.box_num                = box_num;
		this.reading_lesson_level   = reading_lesson_level;
		this.sound_type             = sound_type;
		this.sound_word_or_sentence = sound_word_or_sentence;
		this.id_of_linked_card      = id_of_linked_card;
		this.is_spelling_mode       = is_spelling_mode;
		this.card_text              = card_text;
		this.card_images            = card_images;
		this.card_audio             = card_audio;
		this.card_read_along_timings= card_read_along_timings;
	}

	public static boolean isLegalBoxNum(float box) {
		if( box >= 1 ) {
			return true;
		} else {
			return false;
		}
	}
	public boolean isIsSpellingMode() {
		return is_spelling_mode;
	}
	public boolean isSpellingMode() {
		return this.is_spelling_mode;
	}
	public boolean isReadingMode() {
		return ! this.is_spelling_mode;
	}
	public void setReviewDate( long date ) {
		this.date_in_millis = new Date().getTime();
	}
	public void failed() {
		failed_count++;
		box_num = DEFAULT_BOX_NUM;
		successes_count = 0;
	}

	public void success() {
		successes_count++;
	}

	public boolean isLearnt() {
		if ( successes_count >= DEFAULT_SUCCESS_LIMIT ) {
			return true;
		} else {
			return false;
		}

	}
	public boolean isReviewNeeded(DeckSettings settings, String date, float box_num, String time, int daily_review_count) {
		// Add the box number to the day as days.
		// This gives an easy review algorithm.
		Date cards_date = new Date( date_in_millis );
		cards_date = addDays( cards_date, Math.round(box_num) );

		Date todays_date = new Date();
		// Check if the review should be now or in the future.
		if ( cards_date.before( todays_date ) ) {
			// It's before.
			return true;
		} else {
			// It's after or equal.
			return false;
		}
	}
	public boolean isASentence() {
		if( this.sound_word_or_sentence.compareToIgnoreCase( "Sentence" ) == 0 ) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * @param d is of type 'Date'
	 * @param days is an integer
	 * @return The result of d + days.
	 */
	public static Date addDays(Date d, int days) {
		Calendar c = Calendar.getInstance();
		c.setTime( d );
		c.add(Calendar.DATE, days);  // number of days to add

		return c.getTime();
	}


	public int getCardsRepeatCount() {
		return DEFAULT_SUCCESS_LIMIT - successes_count;
	}
	public String getCardText() {
		return card_text;
	}

	public String getCardImages() {
		return this.card_images;
	}
	public String getCardReadAlongTimings() {
		return this.card_read_along_timings;
	}

	public int getCardID() {
		return card_id;
	}

	public long getDateInMillis() {
		return date_in_millis;
	}

	public float getBoxNum() {
		return box_num;
	}

	public int getReadingLessonLevel() {
		return reading_lesson_level;
	}

	public String getSoundType() {
		return sound_type;
	}

	public String getSoundWordOrSentence() {
		return sound_word_or_sentence;
	}

	public int getIdOfLinkedCard() {
		return id_of_linked_card;
	}

	public ArrayList<String> getCardImagesAsArrayList() {
		return CardDBTagManager.makeStringAList( this.card_images);
	}

	public ArrayList<String> getCardAudioAsArrayList() {
		return CardDBTagManager.makeStringAList( this.card_audio );

	}

	public int getFailedCount() {
		return this.failed_count;
	}

	public int getSuccessesCount() {
		return this.successes_count;
	}

	public void update( long date_in_millis ) {
		if( has_been_updated == false ) {
			has_been_updated = true;
			if (failed_count == 0) {
				// They remember this card.
				// Multiply the box number so we increase the review time.
				box_num = box_num * BOX_NUM_MULTIPLIER;
			} else {
				// Reset the card's box number
				box_num = box_num * DEFAULT_BOX_NUM;
			}
			// Reset the review date to today.
			this.date_in_millis = date_in_millis;
		}
	}
}
