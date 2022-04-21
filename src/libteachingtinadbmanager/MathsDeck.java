/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;
import java.util.ArrayList;

public class MathsDeck extends Deck {

	protected final int INDEX_NUM_1    = 0;
	protected final int INDEX_OPERATOR = 1;
	protected final int INDEX_NUM_2    = 2;
	
	public MathsDeck(String deck_name) {
		super(deck_name);
	}

	@Override
	public ArrayList<String> getQuestion() {
		return null;
	}
	
	public String getQuestionAsString() {
		String question = getCurrentCard().getContent(INDEX_NUM_1)    + " " +
		                  getCurrentCard().getContent(INDEX_OPERATOR) + " " + 
		                  getCurrentCard().getContent(INDEX_NUM_2)    + " = ";
		
		return question;
	}

	@Override
	public ArrayList<String> getAnswer() {
		return null;
	}
	
	public int getAnswerAsInt() {
			Integer num_1;
			Integer num_2;
		try {
			num_1 = new Integer(getCurrentCard().getContent(INDEX_NUM_1));
			num_2 = new Integer(getCurrentCard().getContent(INDEX_NUM_2));
		} catch( NumberFormatException ex ) {
			return 0;
		}
		
		String operator = getCurrentCard().getContent(INDEX_OPERATOR);
		
		if( operator.compareTo("+")  == 0 ) {
			return num_1.intValue() + num_2.intValue();
		}
		else if( operator.compareTo("-")  == 0 ) {
			return num_1.intValue() - num_2.intValue();
		}
		else if( operator.compareTo("/")  == 0 ) {
			return num_1.intValue() / num_2.intValue();
		}
		else if( operator.compareTo("*")  == 0 ) {
			return num_1.intValue() * num_2.intValue();
		} else {
			return 0;
		}
	}

}
