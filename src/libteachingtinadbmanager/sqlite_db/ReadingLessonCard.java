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
