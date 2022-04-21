/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package libteachingtinadbmanager;
import java.util.ArrayList;
import java.io.File;
public class FlashcardGroupDeck extends FlashcardDeck {
	public FlashcardGroupDeck(ArrayList<Card> d, File deck_file_path, DeckSettings s) {
		super(d, deck_file_path, s);

		// This is what Deck() does when it runs, so stick to this layout.
		db_file = deck_file_path;
		db_config_file = s.getFile();

		deck = d;
		settings = s;
		
		deck_index = new RandomizedIndex( this ); 
	}
}
