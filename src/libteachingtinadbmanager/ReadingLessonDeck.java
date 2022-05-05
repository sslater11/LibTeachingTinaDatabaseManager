/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;

import java.util.ArrayList;
import java.io.File;

/**
 * Extends FlashcardGroupDeck and adds some methods to get the question from the flashcard.
 * It's different, because the flashcard format has been changed.
 * Here's an example of the new DB File Layout.
 *     #spelling/reading	word/text	audio	image
 *     #15/02/2022	1.4	19:40:56	3	#spelling#	dog	<audio:spell-dog.mp3><audio:spell-dog2.mp3>	<image:dog.jpg><image:dog2.jpg>
 *     #15/02/2022	1.4	19:40:56	3	#reading#	dog	<audio:dog.mp3><audio:dog2.mp3>	<image:dog.jpg><image:dog2.jpg>
 *
 *     Group	Words	15/02/2020	2.744	19:40:56	1
 *     15/02/2022	1.4	19:40:56	3	#spelling#	dog	<audio:spell-dog.mp3><audio:spell-dog2.mp3>	<image:dog.jpg><image:dog2.jpg>
 *     15/02/2022	1.4	19:40:56	3	#reading#	dog	<audio:dog.mp3><audio:dog2.mp3>	<image:dog.jpg><image:dog2.jpg>
 *
 *     Group	Sentence	15/02/2020	2.744	19:40:56	1
 *     15/02/2022	1.4	19:40:56	3	#sentence#	this is a test	<audio:this-is-a-test.mp3>	<image:test.jpg>    <read-along-timing:timings.txt>
 *     15/02/2022	1.4	19:40:56	3	#reading#	dog	<audio:dog.mp3><audio:dog2.mp3>	<image:dog.jpg><image:dog2.jpg>
 *
 *
 * The line is split to this order - spelling/reading	word/text	audio	image
 */
public class ReadingLessonDeck extends FlashcardGroupDeck {
    public ReadingLessonDeck(ArrayList<Card> d, File deck_file_path, DeckSettings s) {
        super(d, deck_file_path, s);
    }

    public static String READING_MODE  = "#READING#";
    public static String SPELLING_MODE = "#SPELLING#";
    public static String SENTENCE_MODE = "#SENTENCE#";

    public final static int INDEX_CARD_MODE          = 0;
    public final static int INDEX_TEXT               = 1;
    public final static int INDEX_AUDIO              = 2;
    public final static int INDEX_IMAGE              = 3;
    public final static int INDEX_READ_ALONG_TIMINGS = 4;

    public static ArrayList<String> getCardText( Card c ) {
        ArrayList<String> list = CardDBTagManager.makeStringAList( c.getContent(INDEX_TEXT) );
        return list;
    }
    public static ArrayList<String> getCardImage( Card c ) {
        ArrayList<String> list = CardDBTagManager.makeStringAList( c.getContent(INDEX_IMAGE) );
        return list;
    }
    public static ArrayList<String> getCardAudio( Card c ) {
        ArrayList<String> list = CardDBTagManager.makeStringAList( c.getContent(INDEX_AUDIO) );
        return list;
    }
    public static ArrayList<String> getCardReadAlongTimings( Card c ) {
        ArrayList<String> list = CardDBTagManager.makeStringAList( c.getContent(INDEX_READ_ALONG_TIMINGS) );
        return list;
    }
    public static boolean isCardReadingMode( Card c) {
        String current_mode = c.getContent( 0 );
        if( current_mode.compareToIgnoreCase( READING_MODE ) == 0 ) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isCardSpellingMode( Card c) {
        String current_mode = c.getContent( INDEX_CARD_MODE );
        if( current_mode.compareToIgnoreCase( SPELLING_MODE ) == 0 ) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isCardSentenceMode( Card c) {
        String current_mode = c.getContent( INDEX_CARD_MODE  );
        if( current_mode.compareToIgnoreCase( SENTENCE_MODE ) == 0 ) {
            return true;
        } else {
            return false;
        }
    }

    // Implement these 2 methods, but we should never use them, because our flashcards
    // are more complex than a simple question and answer flashcard.
    @Override
    public ArrayList<String> getQuestion() {
        return null;
    }
    @Override
    public ArrayList<String> getAnswer() {
        return null;
    }

}
