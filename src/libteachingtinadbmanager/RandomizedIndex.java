/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;

import java.util.ArrayList;
import java.util.Collections;



public class RandomizedIndex {
    protected ArrayList<Integer> index_list = new ArrayList<Integer>();
    protected Deck deck;

    public RandomizedIndex(Deck d) {
        deck = d;
        ResetList();
    }


    public void ResetList() {
        index_list = new ArrayList<Integer>();

        // Loop through the deck, and add the index for each card as many times as needed.
        for (int i = 0; i < deck.getDeck().size(); i++) {
            int questions_left = getCardsRepeatCount(deck, i);

            if (questions_left >= 1) {
                // Add the index multiple times.
                for (int k = 0; k < questions_left; k++) {
                    index_list.add(i);
                }
            } else {
                // If cards are NOT removable.
                if (!deck.settings.areCardsRemovable()) {
                    // add just one index, to keep it in the list.
                    index_list.add(i);
                }
            }
        }

        Collections.shuffle(index_list);
    }

    public void ResetListKeepFirstIndexSame() {
        int first_index = index_list.get(0);

        // Just reset the list until we set on one with the same first index.
        ResetList();
        while (first_index != index_list.get(0)) {
            ResetList();
        }
    }


    public int getNext() {
        if (index_list.size() > 1) {
            index_list.remove(0);
        } else {
            // The array is empty, or on it's last element, so reset the list.
            ResetList();
        }

        return getCurrent();
    }

    /**
     * @param deck
     * @param cards_index
     * @return Will return the number of questions needed to be asked for a SINGLE card (the current card).
     * WARNING: It will return a zero OR negative number if the card has been learnt.
     */
    public static int getCardsRepeatCount(Deck deck, int cards_index) {
        ArrayList<Card> d = deck.getDeck();

        int success_limit = deck.settings.getSuccessLimit();
        int success_count = d.get(cards_index).getSuccessCount();
        int multiplier = d.get(cards_index).getMultiplier();

        int questions_left = success_limit - success_count;
        questions_left = questions_left * multiplier;

        return questions_left;
    }

    /* TODO:
     * Still need to make sure the randomized list will not repeat the same index too many times in a row.
     * e.g. 1,1,1,1, 2,2,2, 3,3,3, 2,3
     *
     * Maybe just loop through the list n times, and swap one or two of the notes randomly.
     * Make sure I limit how many times the loop runs, otherwise it may end up being an infinite loop.
     */
    public int getCurrent() {
        return index_list.get(0);
    }

    public int size() {
        return index_list.size();
    }

    public String toString() {
        String str = "indexes: ";

        for (int i = 0; i < index_list.size(); i++) {
            String line = "\n";

            line += index_list.get(i).toString();
            //for( int k = 0; k < deck.getQuestion().size(); k++ ) {
            //line += deck.getQuestion().get(k) + " ";
            //}

            //str += line;
        }

        return index_list.toString();
        //return str;
    }
}
