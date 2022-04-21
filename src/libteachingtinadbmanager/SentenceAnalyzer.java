/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class can analyze a sentence, or paragraph, and strip the
 * punctuation and it can find all the pairs of
 * consonants, vowels, vowel-consonant, and also groups of consonants.
 * 
 * @author Simon Slater
 */
public abstract class SentenceAnalyzer {
	final static String[] VOWELS = { "a", "e", "i", "o", "u" };
	final static String[] CONSONANTS = { "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n",
	                                     "p", "q", "r", "s", "t", "v", "w", "x", "y", "z",
	};
	// Characters to ignore, so it we can say that "can't" has "n't",
	// and it's really just treated as "nt".
	final static String[] IGNORED_CHARACTERS = { "-", "'" };
	final static String[] PUNCTUATION = { ".", ",", "!", "?", ";", ":" };

	public static List<String> getWords(String str) {
		// Pass the string to getWordsListWithIndexes to convert it to a list of WordWithIndexes objects.
		// then make a list of strings and return it.
		ArrayList<WordWithIndexes> all_words_with_indexes = getWordsListWithIndexes( str );
		List<String> list = new ArrayList<String>();
		for( int i = 0; i < all_words_with_indexes.size(); i++ ) {
			list.add( all_words_with_indexes.get(i).getWord() );
		}

		return removeDuplicatesFromList( list );
	}
	
	public static String stripWhitespace( String str ) {
		// Use this pattern to find blank spaces at the start of the string.
		Pattern p_beginning_whitespace = Pattern.compile("^ ", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		// Use this pattern to find blank spaces at the end of the string.
		Pattern p_ending_whitespace    = Pattern.compile(" $", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		// Remove all newlines, and tabs, and replace them with spaces.
		str = str.replace("\n", " ");
		str = str.replace("\t", " ");
		
		// Remove all double spaces by looping and replacing 2 spaces with 1 until there's no more double spaces left..
		String new_str = "";
		while( new_str.compareTo(str) != 0 ) {
			new_str = str;
			str = str.replaceAll("  ", " ");
			// remove any whitespace at the start of the string.
			System.out.println( str );
			str = p_beginning_whitespace.matcher(str).replaceAll("");
			str = p_ending_whitespace.matcher(str).replaceAll("");
		}

		return str;
	}

	public static String stripIgnoredCharacters(String str) {
		// This is the easiest way to handle weird punctuation characters, like hyphens, quotes, etc.
		// Just strip them from the string, and I can figure it out afterwards.
		// It's a bit of hassle afterwards, but ultimately makes the code a lot cleaner.
		
		for( int i = 0; i < IGNORED_CHARACTERS.length; i++ ) {
			str = str.replace(IGNORED_CHARACTERS[i], "");
		}
		
		// Remove all newlines, and tabs, and replace them with spaces.
		str = str.replace("\n", " ");
		//str = str.replace("\t", " ");
		
		return str;
	}
	
	public static String stripPunctuation( String str ) {
		for( int i = 0; i < PUNCTUATION.length; i++ ) {
			str = str.replace(PUNCTUATION[i], "");
		}
		
		return str;
	}
	/**
	 * 
	 * Returns a list of vowel consonant pairs, like 'ba', ab', etc.
	 * Expects a formatted list. Pass a string to format it properly.
	 * 
	 * @param str
	 * @return
	 */
//	public static List<String> getVowelConsonantPairs(String str) {
//		List<String> words = getWords(str);
//		
//		return getVowelConsonantPairs( words );
//	}	

	
	/**
	 *
	 * Returns a list of vowel consonant pairs, like 'ba', ab', etc.
	 * Expects a formatted list. Pass a string to format it properly.
	 *
	 * @param words
	 * @return
	 */
	public static List<String> getVowelConsonantPairs(List<String> words) {
		List<String> pairs = new ArrayList<String>();

		for(int i = 0; i < words.size(); i++ ) {
			String str = words.get(i);

			if( str.length() > 1 ) {
				for ( int k = 1; k < str.length(); k++ ) {
					String new_pair = Character.toString( str.charAt(k-1) ) +
					                  Character.toString( str.charAt(k  ) );
	
					if( isVowelConsonantPair( new_pair ) ) {
						String reverse = new StringBuilder(new_pair).reverse().toString();
						
						pairs.add( new_pair );
						pairs.add( reverse );
					}
				}
			}
		}
		
		return removeDuplicatesFromList(pairs);
	}
	
//	public static List<String> getVowelPairs(String str) {
//		List<String> words = getWords(str);
//		
//		return getVowelPairs( words );
//
//	}
	
	public static List<String> getVowelPairs(List<String> words) {
		// Find
		// ao, oo, ou,ai, etc.....
		
		List<String> pairs = new ArrayList<String>();

		for(int i = 0; i < words.size(); i++ ) {
			String str = words.get(i);
			
			if( str.length() > 1 ) {
				for ( int k = 1; k < str.length(); k++ ) {
					String new_pair = Character.toString( str.charAt(k-1) ) +
					                  Character.toString( str.charAt(k  ) );
	
					if( isVowelPair( new_pair ) ) {
						String reverse = new StringBuilder(new_pair).reverse().toString();
						pairs.add( new_pair );
						pairs.add( reverse );
					}
				}
			}
		}

		return removeDuplicatesFromList(pairs);
	}
	
	
//	public static List<String> getConsonantGroups(String str) {
//		List<String> words = getWords(str);
//		
//		return getConsonantGroups(words);
//	}
	/** This will return consonant groups, and NOT consonant pairs.
	so if it finds 3 consonants in a row, it will return them.
	It will find things like 'rhym' 'rhymes', and 'ght' from the word 'thought'
	*/
	public static List<String> getConsonantGroups(List<String> words) {
		List<String> groups = new ArrayList<String>();
		
		for(int i = 0; i < words.size(); i++ ) {
			String str = words.get(i);
			String new_group = "";
			
			// So the loop below will look for a group of consonants, and it will add
			// a group ONLY when it stumbles upon a non-consonant.
			//
			// Hack:
			// I put a vowel on the end of the word, so it will always hit a vowel at the
			// end, and add the last group in the word.
			// Otherwise, we will miss the last consonant group in the word,
			// this little hack just makes the code easier to add the last group.
			//str = str + "A";
			// !!!!! CHANGED !!!!
			// Changed the last character for the hack to a non-alphabet character, to make this code more flexible, because we might want to check for vowels at some point, and putting in an extra one is just stupid.
			str = str + "!";
			
			for( int k = 0; k < str.length(); k++ ) {
				char ch = str.charAt(k);
				
				if( isConsonant(ch) ) {
					new_group = new_group + Character.toString(ch);
				} else {
					// We've hit a vowel/non-consonant character, so check if the
					// current group of consonants is
					// larger than 2, and add it to the list.
					
					if( new_group.length() > 2 ) {
						groups.add(new_group);
					}
					
					new_group = "";
				}
			}
		}
		return removeDuplicatesFromList(groups);
	}
	
//	public static List<String> getConsonantPairs(String str) {
//		List<String> words = getWords(str);
//
//		return getConsonantPairs( words );
//	}
	
	/**
	 * This will look for all consonant pairs, like 'br', 'ly', etc..	
	 */
	public static List<String> getConsonantPairs(List<String> words) {
		List<String> pairs  = new ArrayList<String>();
		
		for(int i = 0; i < words.size(); i++ ){
			String str = words.get(i);
			String new_pair = "";
			
			
			// So the loop below will look for pairs of consonants, and it will add
			// a pair ONLY when it is followed by a vowel.
			//
			// Hack:
			// I put a vowel on the end of the word, so it will always hit a vowel at the
			// end, and add the last pair in the word.
			// Otherwise, we will miss the last consonant pair in the word,
			// this little hack just makes the code easier to add the last pair.
			// !!!!! CHANGED !!!!
			//str = str + "A";
			// Changed the last character for the hack to a non-alphabet character, to make this code more flexible, because we might want to check for vowels at some point, and putting in an extra one is just stupid.
			str = str + "!";
			
			for( int k = 0; k < str.length(); k++ ) {
				char ch = str.charAt(k);
				
				if( isConsonant(ch) ) {
					new_pair = new_pair + Character.toString(ch);
				} else {
					// We've hit a vowel/non-consonant character, so check if the
					// current string is just 2 consonants,
					// and add it to the list.
					
					if( new_pair.length() == 2 ) {
						pairs.add(new_pair);
					}
					
					new_pair = "";
				}
			}
		}
		
		return removeDuplicatesFromList( pairs );

	}


//	public static List<String> getDoubleConsonantVowelPairs(String str) {
//		List<String> words = getWords(str);
//		
//		return getDoubleConsonantVowelPairs( words );
//	}

	/**
	 * This will return things like 'chu' from words like 'church'.
	 * Even though the learner will learn how to reach 'ch',
	 * they should learn the following vowel to make a proper sound,
	 * and it doesn't add too many extra cards to their review.
	 * 
	 * Also, by an accident, my code finds a single consonant followed by a double vowel,
	 * which means it will find things like 'coo' from 'cook', and I think this is probably a good thing, so I'm keeping this glitch.
	 * 
	 * @param words
	 * @return
	 */
	public static List<String> getDoubleConsonantVowelPairs(List<String> words) {
		List<String> pairs  = new ArrayList<String>();
		
		for(int i = 0; i < words.size(); i++ ){
			String str = words.get(i);
			String new_pair = "";
			// Hack:
			// I put a vowel on the end of the word, so it will always hit a vowel at the
			// end, and add the last pair in the word.
			// Otherwise, we will miss the last consonant pair in the word,
			// this little hack just makes the code easier to add the last pair.
			//str = str + "A";
			// !!!!! CHANGED !!!!
			// Changed the last character for the hack to a non-alphabet character, to make this code more flexible, because we might want to check for vowels at some point, and putting in an extra one is just stupid.
			str = str + "!";
			
			// Check if we've found our first consonant.
			boolean has_found_first_consonant = false;
			boolean has_found_first_vowel     = false;
			
			for( int k = 0; k < str.length(); k++ ) {
				char ch = str.charAt(k);
				
				if( isConsonant(ch) ) {
					if( has_found_first_consonant == false ) {
						has_found_first_consonant = true;
						new_pair = new_pair + Character.toString(ch);
					} else if( has_found_first_vowel == false ) {
						// The first consonant has been found, and this is also a consonant, so add it, making a consonant group.
						new_pair = new_pair + Character.toString(ch);
					} else {
						/* The above check means the first consonant and vowel have been
						 * found, so this is a new consonant letter, meaning the code has 
						 * hit the end of the consonant-vowel pair, and hit a new consonant.
						 */
						if( k != str.length()-1 ) {
							/*
							 * Only decrement if we're not at the very last letter.
							 */
							k--;
						}
						/* 
						 * Let's exit this loop, and add the new double-consonant-vowel-pair to the list.
						 */
						
						if( new_pair.length() >= 3 && isConsonant(new_pair.charAt(0)) && has_found_first_vowel) {
							/* There's at least 3 letters in the consonant-vowel pair.
							 * 2 consonants and 1 vowel or
							 * 1 consonant  and 2 vowels
							 * 
							 * The first letter is a consonant
							 * A vowel was also found
							 */
							pairs.add(new_pair);
						}
						
						// Reset variables for the next search in this word.
						new_pair = "";
						has_found_first_consonant = false;
						has_found_first_vowel     = false;
					}
				} else if( isVowel(ch) ) {
					if( has_found_first_consonant == true ) {
						has_found_first_vowel = true;
						new_pair = new_pair + Character.toString( ch );
					} else if( has_found_first_vowel == true ) {
						new_pair = new_pair + Character.toString( ch );
					}
					else {
						// Do nothing.
					}
				} else {
					/*
					 * Check if there is already a double-consonant vowel pair.
					 */
					
					if( new_pair.length() >= 3 && isConsonant(new_pair.charAt(0)) && has_found_first_vowel) {
						/* There's at least 3 letters in the consonant-vowel pair.
						 * 2 consonants and 1 vowel or
						 * 1 consonant  and 2 vowels
						 * 
						 * The first letter is a consonant
						 * A vowel was also found
						 */
						pairs.add( new_pair );
					}
					
					// Reset variables for the next search in this word.
					new_pair = "";
					has_found_first_consonant = false;
					has_found_first_vowel     = false;

				}
			}
		}
			
		return removeDuplicatesFromList( pairs );
	}
	
	
	/*
	 * This function finds things like 'oot', 'oots', and 'ots'.
	 * It's a reverse of DoubleConsonantVowelPair().
	 * I made this by copying the DoubleConsonantVowelPair() code, and swapping the words 'consonant', and 'vowel' around.
	 */
	
	public static List<String> getDoubleVowelConsonantPairs(List<String> words){
		List<String> pairs  = new ArrayList<String>();
		
		for(int i = 0; i < words.size(); i++ ){
			String str = words.get(i);
			String new_pair = "";
			// Hack:
			// I put a vowel on the end of the word, so it will always hit a consonant at the
			// end, and add the last pair in the word.
			// Otherwise, we will miss the last consonant pair in the word,
			// this little hack just makes the code easier to add the last pair.
			//str = str + "A";
			// !!!!! CHANGED !!!!
			// Changed the last character for the hack to a non-alphabet character, to make this code more flexible, because we might want to check for vowels at some point, and putting in an extra one is just stupid.
			str = str + "!";
			
			// Check if we've found our first vowel.
			boolean has_found_first_vowel = false;
			boolean has_found_first_consonant     = false;
			
			for( int k = 0; k < str.length(); k++ ) {
				char ch = str.charAt(k);
				
				if( isVowel(ch) ) {
					if( has_found_first_vowel == false ) {
						has_found_first_vowel = true;
						new_pair = new_pair + Character.toString(ch);
					} else if( has_found_first_consonant == false ) {
						// The first vowel has been found, and this is also a vowel, so add it, making a vowel group.
						new_pair = new_pair + Character.toString(ch);
					} else {
						/* The above check means the first vowel and consonant have been
						 * found, so this is a new vowel letter, meaning the code has 
						 * hit the end of the vowel-consonant pair, and hit a new vowel.
						 */
						if( k != str.length()-1 ) {
							/*
							 * Only decrement if we're not at the very last letter.
							 */
							k--;
						}
						/* 
						 * Let's exit this loop, and add the new double-vowel-consonant-pair to the list.
						 */
						
						if( new_pair.length() >= 3 && isVowel(new_pair.charAt(0)) && has_found_first_consonant) {
							/* There's at least 3 letters in the vowel-consonant pair.
							 * 2 vowels and 1 consonant or
							 * 1 vowel  and 2 consonants
							 * 
							 * The first letter is a vowel
							 * A consonant was also found
							 */
							pairs.add(new_pair);
						}
						
						// Reset variables for the next search in this word.
						new_pair = "";
						has_found_first_vowel     = false;
						has_found_first_consonant = false;
					}
				} else if( isConsonant(ch) ) {
					if( has_found_first_vowel == true ) {
						has_found_first_consonant = true;
						new_pair = new_pair + Character.toString( ch );
					} else if( has_found_first_consonant == true ) {
						new_pair = new_pair + Character.toString( ch );
					}
					else {
						// Do nothing.
					}
				} else {
					/*
					 * Check if there is already a double-vowel consonant pair.
					 */
					
					if( new_pair.length() >= 3 && isVowel(new_pair.charAt(0)) && has_found_first_consonant) {
						/* There's at least 3 letters in the vowel-consonant pair.
						 * 2 vowels and 1 consonant or
						 * 1 vowel  and 2 consonants
						 * 
						 * The first letter is a vowel
						 * A consonant was also found
						 */
						pairs.add( new_pair );
					}
					
					// Reset variables for the next search in this word.
					new_pair = "";
					has_found_first_vowel     = false;
					has_found_first_consonant = false;

				}
			}
		}
			
		return removeDuplicatesFromList( pairs );
	}
//	public static List<String> getDoubleVowelConsonantPairs(String str) {
//		List<String> words = getWords(str);
//		
//		return getDoubleVowelConsonantPairs( words );
//	}

	
	
	public static List<String> removeDuplicatesFromList( List<String> list ) {		
		for( int i = 0; i < list.size(); i++ ) {
			for( int k = i+1; k < list.size(); k++ ) {
				if( list.get(i).compareToIgnoreCase(list.get(k)) == 0) {
					//System.out.println();
					//System.out.print(list.get(i) + "   " + list.get(k) + "  " + i + "  " + k);
					list.remove(k);
					k = i;
					//k--;
				}
			}
		}
		
		return list;
	}
	
	public static boolean isConsonantPair( String str ) {
		if( str.length() == 2 ) {
			return isConsonantPair( str.charAt(0), str.charAt(1) );
		}
		
		return false;
	}


	public static boolean isConsonantPair( char ch1, char ch2 ) {
		if( isConsonant(ch1) && isConsonant(ch2) ) {
			return true;
		}
		
		return false;
	}

	public static boolean isVowelConsonantPair( String str ) {
		if( str.length() == 2 ) {
			return isVowelConsonantPair(str.charAt(0), str.charAt(1) );
		}

		return false;
	}

	public static boolean isVowelConsonantPair( char ch1, char ch2 ) {
		if( isConsonant(ch1) &&
		    isVowel    (ch2) ) {
			return true;
		}
		if( isVowel    (ch1) &&
		    isConsonant(ch2)) {
			return true;
		}
		
		return false;
	}

	public static boolean isVowelPair( String str ) {
		if( str.length() == 2 ) {
			return isVowelPair(str.charAt(0), str.charAt(1) );
		}

		return false;
	}

	public static boolean isVowelPair( char ch1, char ch2 ) {
		if( isVowel(ch1) && isVowel(ch2) ) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isVowel(String letter) {
		// Yes, use a string to check a single letter,
		// since we can ignore case, and test for string length anyway.
		if ( letter.length() == 1 ) {
			for( int i = 0; i < VOWELS.length; i++ ) {
				if( letter.compareToIgnoreCase(VOWELS[i]) == 0 ) {
					return true;
				}
			}
		}

		return false;
	}
	
	public static boolean isVowel( char ch ) {
		return isVowel( Character.toString( ch ) );
	}


	public static boolean isConsonant( char ch ) {
		return isConsonant( Character.toString( ch ) );
	}

	public static boolean isConsonant(String letter) {
		// Yes, use a string to check a single letter,
		// since we can ignore case, and test for string length anyway.

		if ( letter.length() == 1 ) {
			for( int i = 0; i < CONSONANTS.length; i++ ) {
				if( letter.compareToIgnoreCase(CONSONANTS[i]) == 0 ) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isIgnoredCharacter(String letter) {
		// Yes, use a string to check a single letter,
		// since we can ignore case, and test for string length anyway.

		if ( letter.length() == 1 ) {
			for( int i = 0; i < IGNORED_CHARACTERS.length; i++ ) {
				if( letter.compareToIgnoreCase(IGNORED_CHARACTERS[i]) == 0 ) {
					return true;
				}
			}
		}

		return false;
	}
	
	public static boolean isIgnoredCharacter(char ch) {
		return isIgnoredCharacter( Character.toString( ch ) );
	}



	/**
	 * This is to be used to get a list of words and their starting and ending index in a string.
	 * Used in TeachingTina app to click on a word and to make it play the audio for the word clicked on.
	 * I used the Spannable class to make each word clickable in the TextView.
	 */
	public static ArrayList<WordWithIndexes> getWordsListWithIndexes( String sentence ) {
		// Extract each word along with it's starting and ending index.
		// Store each word and it's indexes in an ArrayList.

		ArrayList<WordWithIndexes> words_with_indexes = new ArrayList<WordWithIndexes>();

		String temp_word;
		int starting_index = 0;
		int ending_index = 0;
		boolean found_word = false;
		for( int i = 0; i <= sentence.length(); i++ ) {
			if( i == sentence.length() ) {
			    // We've gone past the last character, so add the last string to the list.
				String str = sentence.substring( starting_index, ending_index );
				WordWithIndexes new_word = new WordWithIndexes( str, starting_index, ending_index );

				words_with_indexes.add( new_word );
				break;
			}
			char letter = sentence.charAt( i );
			if( found_word == false ) {
				// If we aren't looping through a word, look for the first letter

				if( isVowel( letter ) || isConsonant( letter ) || isIgnoredCharacter( letter ) ) {
					// The start of the word.
					found_word = true;
					starting_index = i;
					ending_index = i+1;
				}
			}
			else {
				// Looping through a word.
				// Keep looking until we reach the end of the word.
				
				// Check if the letter is an alphabet letter,
				// or if it's an ignored character in a word, like an apostrophe or hyphen.
				if( isVowel( letter ) || isConsonant( letter ) || isIgnoredCharacter( letter ) ) {
					// Update the last index.
					ending_index = i+1;
				} else {
					// We've reached the end of the word so store our results.
					found_word = false;

					String str = sentence.substring( starting_index, ending_index );
					WordWithIndexes new_word = new WordWithIndexes( str, starting_index, ending_index );

					words_with_indexes.add( new_word );
				}
			}

			// move this to SenenceAnalyzer so that we can reuse the same code later on when we need to check the database for any missing media.
			// it'll check if our sentence is missing any audio as it may parse it in an unexpected way and link to a file that doesn't exist.
			// make sure it can handle apostrophes properly!
		}

		return words_with_indexes;
	}
}

