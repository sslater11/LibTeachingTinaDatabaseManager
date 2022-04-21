/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;
import java.util.ArrayList;
import java.util.List;

/**
 * Make a new reading lesson and ignore all the words and sounds found in previous lessons.
 */
public class ReadingLessonCreator {
	// This will store all the contents of a single lesson.

	int level = 1; // Reading level as a number. Probably 1 level for every five words.

	// Null means there's no previous lesson, so it'll be the first lesson.
	ReadingLessonCreator prev_lesson = null;
	ReadingLessonCreator next_lesson = null;

	List<String> consonant_pairs              = new ArrayList<String>();
	List<String> consonant_groups             = new ArrayList<String>();
	List<String> vowel_consonant_pairs        = new ArrayList<String>();
	List<String> vowel_pairs                  = new ArrayList<String>();
	List<String> double_consonant_vowel_pairs = new ArrayList<String>();
	List<String> double_vowel_consonant_pairs = new ArrayList<String>();
	List<String> words                        = new ArrayList<String>();
	String sentence = "";

	/**
	 * This will parse the sentence passed, and will break it into
	 * vowel pairs, consonant pairs, vowel-consonant pairs, and consonant groups.
	 * We will also keep the sentence for use later on.
	 *
	 * If there are no previous lesson, pass a null argument. The null will be managed.
	 *
	 * static @param previous_lesson, sentence - a string of words, or a sentence or paragraph.
	 */
	public ReadingLessonCreator( ReadingLessonCreator previous_lesson, String sentence ) {
		setPreviousLesson ( previous_lesson );
		List <String> words = SentenceAnalyzer.getWords ( sentence );

		if( previous_lesson == null ) {
			setSentence                 ( sentence );
			setWords                    ( words );
			setConsonantPairs           ( SentenceAnalyzer.getConsonantPairs           ( words ) );
			setConsonantGroups          ( SentenceAnalyzer.getConsonantGroups          ( words ) );
			setVowelConsonantPairs      ( SentenceAnalyzer.getVowelConsonantPairs      ( words ) );
			setVowelPairs               ( SentenceAnalyzer.getVowelPairs               ( words ) );
			setDoubleConsonantVowelPairs( SentenceAnalyzer.getDoubleConsonantVowelPairs( words ) );
			setDoubleVowelConsonantPairs( SentenceAnalyzer.getDoubleVowelConsonantPairs( words ) );
		} else {
			setLevel( previous_lesson.getLevel() + 1 );
			/**
			 * Remove all duplicate words first, then AFTER that, find all vowel pairs,
			 * consonant pairs, etc for our new words.
			 */
			setSentence                 ( sentence );
			setWords                    ( words );
			removePreviousLessonsWords();

			setConsonantPairs           ( SentenceAnalyzer.getConsonantPairs           ( getWords() ) );
			setConsonantGroups          ( SentenceAnalyzer.getConsonantGroups          ( getWords() ) );
			setVowelConsonantPairs      ( SentenceAnalyzer.getVowelConsonantPairs      ( getWords() ) );
			setVowelPairs               ( SentenceAnalyzer.getVowelPairs               ( getWords() ) );
			setDoubleConsonantVowelPairs( SentenceAnalyzer.getDoubleConsonantVowelPairs( getWords() ) );
			setDoubleVowelConsonantPairs( SentenceAnalyzer.getDoubleVowelConsonantPairs( getWords() ) );
			
			// Remove all the letter groups from previous lessons
			// so this lesson is left with only new letter groups.
			removePreviousLessonsConsonantPairs();
			removePreviousLessonsConsonantGroups();
			removePreviousLessonsVowelConsonantPairs();
			removePreviousLessonsVowelPairs();
			removePreviousLessonsDoubleConsonantVowelPairs();
			removePreviousLessonsDoubleVowelConsonantPairs();
		}
	}
	/**
	 * Empty constructor for setting up the object manually.
	 */
	ReadingLessonCreator() {
	}


	public boolean hasNewWords() {
		if( getWords().size() >= 1 ) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasPreviousLesson() {
		if( this.prev_lesson != null ) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean hasNextLesson() {
		if( this.next_lesson != null ) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public int getLevel() {
		return this.level;
	}

	public int getCurrentReadingLevel( List<String> list ) {
		for( String i : list ) {
			for( String k : getWords() ) {
				if( i.compareToIgnoreCase( k ) == 0 ) {
					return getLevel();
				}
				
			}
		}
		if( this.prev_lesson == null ) {
			return 0;
		} else {
			return this.getPreviousLesson().getCurrentReadingLevel(list);
		}
	}
	public ReadingLessonCreator getPreviousLesson() {
		return this.prev_lesson;
	}
	public ReadingLessonCreator getNextLesson() {
		return this.next_lesson;
	}

	public List<String> getConsonantPairs() {
		return this.consonant_pairs;
	}
	public List<String> getConsonantGroups() {
		return this.consonant_groups;
	}
	public List<String> getVowelPairs() {
		return this.vowel_pairs;
	}
	public List<String> getVowelConsonantPairs () {
		return this.vowel_consonant_pairs;
	}
	public List<String> getDoubleConsonantVowelPairs() {
		return this.double_consonant_vowel_pairs;
	}
	public List<String> getDoubleVowelConsonantPairs() {
		return this.double_vowel_consonant_pairs;
	}
	public String getSentence() {
		return this.sentence;
	}
	public List<String> getWords() {
		return this.words;
	}
	/**
	 * Will get all the words from this lesson, and all the lessons before it.
	 * @return
	 */
	public List<String> getAllLessonsWords() {
		List<String> words = new ArrayList<String>();
		if( prev_lesson == null ) {
			words = getWords();
		}
		else {
			words.addAll( this.prev_lesson.getAllLessonsWords() );
			words.addAll( getWords() );
		}

		return words;
	}

	//public List<String> getAllWords() {
	//	List<String> list = new ArrayList<String>();
	//	ReadingLessonCreator previous_lesson = getPreviousLesson();

	//	while( previous_lesson != null ) {
	//		list.addAll( previous_lesson.getWords() );

	//		previous_lesson = previous_lesson.getPreviousLesson();
	//	}

	//	return list;

	//}

	public void setLevel( int level ) {
		this.level = level;
	}
	public void setPreviousLesson( ReadingLessonCreator previous_lesson ) {
		this.prev_lesson = previous_lesson;
	}
	public void setNextLesson( ReadingLessonCreator next_lesson ) {
		this.next_lesson = next_lesson;
	}
	public void setConsonantPairs(List<String> list) {
		this.consonant_pairs = list;
	}
	public void setConsonantGroups(List<String> list) {
		this.consonant_groups = list;
	}
	public void setVowelPairs(List<String> list) {
		vowel_pairs = list;
	}
	public void setVowelConsonantPairs (List<String> list) {
		this.vowel_consonant_pairs = list;
	}
	public void setDoubleConsonantVowelPairs (List<String> list) {
		this.double_consonant_vowel_pairs = list;
	}
	public void setDoubleVowelConsonantPairs (List<String> list) {
		this.double_vowel_consonant_pairs = list;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public void setWords(List<String> list) {
		this.words = list;
	}



	public void printAllLessonContents() {
		System.out.println("Level: " + getLevel() );
		//printList( getConsonantPairs() );
	
		//System.out.println("");
		//System.out.println("");
		//System.out.println("Vowel Consonant Pairs:");
		//printList( getVowelConsonantPairs() );
	
		//System.out.println("");
		//System.out.println("");
		//System.out.println("Vowel Pairs:");
		//printList( getVowelPairs() );
	
		//System.out.println("");
		//System.out.println("");
		//System.out.println("Consonant Groups:");
		//printList( getConsonantGroups() );
	
		//System.out.println("");
		//System.out.println("");
		//System.out.println("Double Consonant Vowel:");
		//printList( getDoubleConsonantVowelPairs() );
	
		//System.out.println("");
		//System.out.println("");
		//System.out.println("Double Vowel Consonant:");
		//printList( getDoubleVowelConsonantPairs() );
	
		//System.out.println("");
		//System.out.println("");
		//System.out.println("Words:");
		//printList( getWords() );
		
		//System.out.println("");
		//System.out.println("");
		//System.out.println("Sentence:");
		//System.out.println( getSentence() );
	}


	public void printAllLessonContentsAsLessonFile() {
		printList( TextEditorDBManager.getDatabaseOutput( this ) );
	}


	public static void printList(List<String> list) {
		for( int i = 0; i < list.size(); i++ ) {
			System.out.println( list.get(i) );
		}
	}


	/**
	 * Removes words found in previous lessons.
	 */
	private void removePreviousLessonsWords() {
		ReadingLessonCreator previous_lesson = getPreviousLesson();

		while( previous_lesson != null ) {
			setWords( removeDuplicatesFromList(previous_lesson.getWords(), this.getWords()) );
			previous_lesson = previous_lesson.getPreviousLesson();
		}
	}
	/**
	 * Removes ConsonantPairs found in previous lessons using recursion.
	 */
	private void removePreviousLessonsConsonantPairs() {
		ReadingLessonCreator previous_lesson = getPreviousLesson();

		while( previous_lesson != null ) {
			setConsonantPairs( removeDuplicatesFromList(previous_lesson.getConsonantPairs(), this.getConsonantPairs()) );
			previous_lesson = previous_lesson.getPreviousLesson();
		}
	}
	/**
	 * Removes ConsonantGroups found in previous lessons using recursion.
	 */
	private void removePreviousLessonsConsonantGroups() {
		ReadingLessonCreator previous_lesson = getPreviousLesson();

		while( previous_lesson != null ) {
			setConsonantGroups( removeDuplicatesFromList(previous_lesson.getConsonantGroups(), this.getConsonantGroups()) );
			previous_lesson = previous_lesson.getPreviousLesson();
		}
	}

	/**
	 * Removes VowelPairs found in previous lessons using recursion.
	 */
	private void removePreviousLessonsVowelPairs() {
		ReadingLessonCreator previous_lesson = getPreviousLesson();

		while( previous_lesson != null ) {
			setVowelPairs( removeDuplicatesFromList(previous_lesson.getVowelPairs(), this.getVowelPairs()) );
			previous_lesson = previous_lesson.getPreviousLesson();
		}
	}
	/**
	 * Removes VowelConsonantPairs found in previous lessons using recursion.
	 */
	private void removePreviousLessonsVowelConsonantPairs() {
		ReadingLessonCreator previous_lesson = getPreviousLesson();

		while( previous_lesson != null ) {
			setVowelConsonantPairs( removeDuplicatesFromList(previous_lesson.getVowelConsonantPairs(), this.getVowelConsonantPairs()) );
			previous_lesson = previous_lesson.getPreviousLesson();
		}
	}
	/**
	 * Removes DoubleConsonantVowelPairs found in previous lessons using recursion.
	 */
	private void removePreviousLessonsDoubleConsonantVowelPairs() {
		ReadingLessonCreator previous_lesson = getPreviousLesson();

		while( previous_lesson != null ) {
			setDoubleConsonantVowelPairs( removeDuplicatesFromList(previous_lesson.getDoubleConsonantVowelPairs(), this.getDoubleConsonantVowelPairs()) );
			previous_lesson = previous_lesson.getPreviousLesson();
		}
	}
	/**
	 * Removes DoubleVowelConsonantPairs found in previous lessons using recursion.
	 */
	private void removePreviousLessonsDoubleVowelConsonantPairs() {
		ReadingLessonCreator previous_lesson = getPreviousLesson();

		while( previous_lesson != null ) {
			setDoubleVowelConsonantPairs( removeDuplicatesFromList(previous_lesson.getDoubleVowelConsonantPairs(), this.getDoubleVowelConsonantPairs()) );
			previous_lesson = previous_lesson.getPreviousLesson();
		}
	}




	/**
	 * Will remove list_a duplicates from list_b
	 * @param list_a
	 * @param list_b
	 * @return returns a cut down version of list_b
	 */
	public static List<String> removeDuplicatesFromList( List<String> list_a, List<String> list_b ) {
		for( int a = 0; a < list_a.size(); a++ ) {
			for( int b = 0; b < list_b.size(); b++ ) {
				String str_a = list_a.get(a);
				String str_b = list_b.get(b);

				if( str_a.compareToIgnoreCase(str_b) == 0 ) {
					list_b.remove( b );
					b--;
				}
			}
		}
		return list_b;
	}



}
