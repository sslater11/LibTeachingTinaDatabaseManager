/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;

import java.util.ArrayList;

public class ReadingLessonDeck {
    ArrayList<ReadingLessonCard> deck;
    ArrayList<ReadingLessonCard> learnt_deck = new ArrayList<ReadingLessonCard>();
    ReadingLessonRandomizedIndex deck_index;
    boolean are_cards_removable = true;

    public ReadingLessonDeck(ArrayList<ReadingLessonCard> deck) {
        this.deck = deck;
        deck_index = new ReadingLessonRandomizedIndex( deck, are_cards_removable );
    }

    public int countCardsLeftToStudy() {
        int total_reviews_left = 0;
        for( int i = 0; i < deck.size(); i++ ) {
            total_reviews_left += deck.get(i).getCardsRepeatCount();
        }

        return total_reviews_left;
    }
    public ReadingLessonCard getCurrentCard() {
        return deck.get( deck_index.getCurrent() );
    }

    public ArrayList<ReadingLessonCard> getLearntDeck() {
        return learnt_deck;
    }

    public boolean isLearnt() {
        if( deck.size() == 0 )
        {
            return true;
        } else {
            return false;
        }
    }

    public void nextQuestion( boolean is_answer_correct, boolean stay_on_incorrect_card ) {
        if( is_answer_correct ) {
            // Mark it as successful.
            getCurrentCard().success();

            // Check if the card should be removed from the deck.
            if( are_cards_removable ) {
                // Remove the card.
                // Check if the current card has been learnt, and move it to the learnt_deck.
                if( getCurrentCard().isLearnt() ) {
                    // Update the card's details, used for writing to the database.
                    learnt_deck.add( getCurrentCard() );
                    deck.remove( deck_index.getCurrent() );

                    // Update the list of indexes, since we just removed a card
                    deck_index.ResetList();
                }
            }

            if( ! isLearnt() ) {
                deck_index.getNext();
            }
        } else {
            // They got it wrong, so the card's success count has been reset to 0.
            // Now there are more reviews needed, so reset the index list.
            getCurrentCard().failed();
            if( stay_on_incorrect_card ) {
                deck_index.ResetListKeepFirstIndexSame();
            } else {
                deck_index.ResetList();
            }
        }

        if( isLearnt() ) {
            System.out.println("nextQuestion() has been called, and the deck has now been learnt.");
            System.out.println("It was probably learnt during this last execution of it, so does that make this message meaningless?");
        }
    }

    public int size() {
        return deck.size();
    }
}
