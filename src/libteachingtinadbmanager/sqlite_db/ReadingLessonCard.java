/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package libteachingtinadbmanager;

public class ReadingLessonCard {
	int rowid;
	int date_in_millis;
	int box_num;
	int reading_lesson_level;
	String sound_type;
	String sound_word_or_sentence;
	int id_of_linked_card;
	int is_spelling_mode;

	String card_text;
	String card_media;

	ReadingLessonCard(
		int rowid,
		int date_in_millis,
		int box_num,
		int reading_lesson_level,
		String sound_type,
		String sound_word_or_sentence,
		int id_of_linked_card,
		int is_spelling_mode,

		String card_text,
		String card_media
	)
	{
		this.rowid                  = rowid;
		this.date_in_millis         = date_in_millis;
		this.box_num                = box_num;
		this.reading_lesson_level   = reading_lesson_level;
		this.sound_type             = sound_type;
		this.sound_word_or_sentence = sound_word_or_sentence;
		this.id_of_linked_card      = id_of_linked_card;
		this.is_spelling_mode       = is_spelling_mode;
		this.card_text              = card_text;
		this.card_media             = card_media;
	}
}
