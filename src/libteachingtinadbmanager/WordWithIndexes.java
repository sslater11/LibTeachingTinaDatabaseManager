/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;

/**
 * This class stores a word and a starting and ending index.
 * The indexes the location of where the word is as a substring of a sentence.
 *
 * Used in TeachingTina app to click on a word and to make it play the audio for the word clicked on.
 * I used the Spannable class to make each word clickable in the TextView.
 */
public class WordWithIndexes {
	private String word;
	private int starting_index;
	private int ending_index;

	WordWithIndexes( String word, int starting_index, int ending_index ) {
		this.word = word;
		this.starting_index = starting_index;
		this.ending_index = ending_index;
	}

	public String getWord() {
		return this.word;
	}

	public String getWordWithIgnoredCharactersRemoved() {
		String result = "";
		for( int i = 0; i < this.word.length(); i++ ) {
			char letter = this.word.charAt( i );

			if( SentenceAnalyzer.isIgnoredCharacter( letter ) == false) {
				result += letter;
			}
		}

		return result;
	}

	public int getStartingIndex() {
		return this.starting_index;
	}

	public int getEndingIndex() {
		return this.ending_index;
	}
}
