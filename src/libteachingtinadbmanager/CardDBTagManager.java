/* © Copyright 2022, Simon Slater

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 2 of the License.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/


package libteachingtinadbmanager;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * version 1.1
 * Added a check for <br> so we can find it in a string if need be.
 * @author simon
 *
 */
public class CardDBTagManager {
	private static String version = "1.1";
	
	private static Pattern p_br_tag   = Pattern.compile("< *(br|BR|Br|bR)");
	/* This regex pattern will find these tags:
	 *      <img:"
	 *      <IMG:"
	 *      <image:"   
	 *      <IMAGE:"
	 * It also allows for zero or more spaces between the 'opening brace' and the letter 'i'.
	 * It also allows for zero or more spaces between the 'g'             and the        'colon'
	 * e.g.
	 *      <img: "
	 *      <   img  :  "
	 *      < img:"
	 */
	private static Pattern p_img_tag   = Pattern.compile("< *(img|IMG|image|IMAGE) *: *\"");
	private static Pattern p_read_along_timing_tag   = Pattern.compile("< *(rat|RAT|readalongtiming|READALONGTIMING) *: *\"");

	// Same as above, but with audio.
	private static Pattern p_audio_tag = Pattern.compile("< *(audio|AUDIO|Audio) *: *\"");
	
	/* This regex pattern will find these tags:
	 * <fontsize:
	 * <FONTSIZE:
	 * <FontSize:
	 * <Fontsize:
	 * <fontSize: << capital S.
	 * It also finds the same tags as above, with spaces between the words 'font' and 'size'
	 * It also allows for zero or more spaces between the 'opening brace' and the letter 'f'.
	 * It also allows for zero or more spaces between the 'e'             and the        'colon'
	 */
	private static Pattern p_font_size_tag = Pattern.compile("< *(font *size|FONT *SIZE|Font *Size|Font *size|font *Size) *: *\"");
	
	
	
	
	
	
	/* This regex pattern will find these closing tags:
	 * ">
	 * "/>
	 * Spaces can be included too, so these are also ok to use.
	 * " >
	 * 
	 * "/>
	 * "/ >
	 * " />
	 * "  /  >
	 * 
	 * the last regex will check for just a closing bracket
	 * >
	 * 
	 */
	private static Pattern p_xml_end_tag = Pattern.compile("(\" *>|\" */ *>|>)");
	
	
	// Constants for accessing the 'patters' array members.
	private static Pattern[] patterns = {
		p_xml_end_tag,
		p_img_tag,
		p_audio_tag,
		p_read_along_timing_tag,
		p_font_size_tag,
		p_br_tag
	};
	private static final int INDEX_XML_END_TAG            = 0;
	private static final int INDEX_IMAGE_TAG              = 1;
	private static final int INDEX_AUDIO_TAG              = 2;
	private static final int INDEX_READ_ALONG_TIMINGS_TAG = 3;
	private static final int INDEX_FONT_SIZE_TAG          = 4;
	private static final int INDEX_BR_TAG                 = 5;

	/**
	 * Will check if the string has a tag in it.
	 * The index is used to define what kind of tag is needed e.g. INDEX_XML_IMAGE_TAG
	 * @param str
	 * @param index
	 * @return
	 */
	public static boolean hasMatch(String str, int index) {
		Matcher m_tag = patterns[index].matcher( str );
		
		// Check if it's got the starting tag.
		if (m_tag.find()) {
			// Check if it's also got the closing tag.
			Matcher m_xml_end_tag = patterns[INDEX_XML_END_TAG].matcher( str.substring(m_tag.start()) );
			
			if( m_xml_end_tag.find()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Will return the tag's starting index in the string.
	 * Use pattern_index to define what kind of tag to look for e.g. INDEX_IMAGE_TAG.
	 * @param str
	 * @param pattern_index
	 * @return
	 */
	public static int getTagsStartingIndex( String str, int pattern_index ) {
		Matcher m_tag = patterns[pattern_index].matcher( str );
		if( m_tag.find() ) {
			return m_tag.start();
		} else {
			return -1;
		}
	}
	
	public static int getTagsEndingIndex(String str, int pattern_index) {
		Matcher m_tag = patterns[pattern_index].matcher( str );
		if( m_tag.find() ) {
			// Search for the match after the first position of the image tag.
			Matcher m_xml_end_tag = patterns[INDEX_XML_END_TAG].matcher( str.substring(m_tag.start()) );
			if( m_xml_end_tag.find() ) {
			System.out.println("start: " + m_tag.start());
				return m_xml_end_tag.end();
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
	
	/**
	 * Takes a tag, and strips the tags, to return just the content.
	 * @param str as a tag e.g. "<img:file.jpg/>", pattern_index e.g. INDEX_IMAGE_TAG
	 * @return Will return null if the string does not contain a proper tag, with a closing brace.
	 * Returns a string with just the content.
	 */
	public static String getTagsContents( String str, int pattern_index ) {
		
		if( hasMatch(str, pattern_index) ) {
			Matcher m_tag = patterns[pattern_index].matcher( str );
			Matcher m_xml_end_tag = patterns[INDEX_XML_END_TAG].matcher( str );
			
			m_tag.find();
			m_xml_end_tag.find();
			
			int content_start_index = m_tag.end();
			int content_end_index   = m_xml_end_tag.start();
			String new_str = str.substring(content_start_index, content_end_index);
			
			return new_str;
		} else {
			System.out.println("Tag not found, so returning null value.");
			return null;
		}
	}
	
	public static int getBRTagStartIndex( String str ){
		return getTagsStartingIndex(str, INDEX_BR_TAG);
	}
	
	public static int getBRTagEndIndex( String str ){
		return getTagsEndingIndex(str, INDEX_BR_TAG);
	}
	
	public static boolean hasImageTag(String str) {
		return hasMatch(str, INDEX_IMAGE_TAG);
	}
	
	
	public static int getImageTagStartIndex(String str) {
		return getTagsStartingIndex( str, INDEX_IMAGE_TAG );
	}
	
	public static int getImageTagEndIndex(String str) {
		return getTagsEndingIndex(str, INDEX_IMAGE_TAG);
	}

	/**
	 * Takes an image tag, and strips the tag, to return just the filename.
	 * @param str as an image tag e.g. "<img:file.jpg/>"
	 * @return Will return null if the string does not contain a proper image tag including a closing brace.
	 * Returns a string with just the filename.
	 */
	public static String getImageFilename( String str ) {
		return getTagsContents(str, INDEX_IMAGE_TAG);
	}
	
	
	public static boolean hasBRTag( String str ) {
		return hasMatch( str, INDEX_BR_TAG );
	}
	
	public static boolean hasAudioTag(String str) {
		return hasMatch(str, INDEX_AUDIO_TAG);
	}

	public static boolean hasReadAlongTimingsTag(String str) {
		return hasMatch(str, INDEX_READ_ALONG_TIMINGS_TAG);
	}

	public static int getReadAlongTimingsTagStartIndex(String str) {
		return getTagsStartingIndex( str, INDEX_READ_ALONG_TIMINGS_TAG );
	}
	public static int getReadAlongTimingsTagEndIndex(String str) {
		return getTagsEndingIndex(str, INDEX_READ_ALONG_TIMINGS_TAG);
	}

	public static int getAudioTagStartIndex(String str) {
		return getTagsStartingIndex( str, INDEX_AUDIO_TAG );
	}
	
	public static int getAudioTagEndIndex(String str) {
		return getTagsEndingIndex(str, INDEX_AUDIO_TAG);
	}
	
	/**
	 * Takes a read-along-timing tag, and strips the tag to return just the filename.
	 * @param str as an audio tag e.g. "<read-along-timing:timing.txt/>"
	 * @return Will return null if the string does not contain a proper image tag including a closing brace.
	 * Returns a string with just the filename.
	 */
	public static String getReadAlongTimingsFilename( String str ) {
		return getTagsContents(str, INDEX_READ_ALONG_TIMINGS_TAG);
	}

	/**
	 * Takes an audio tag, and strips the tag to return just the filename.
	 * @param str as an audio tag e.g. "<audio:file.jpg/>"
	 * @return Will return null if the string does not contain a proper image tag including a closing brace.
	 * Returns a string with just the filename.
	 */
	public static String getAudioFilename( String str ) {
		return getTagsContents(str, INDEX_AUDIO_TAG);
	}

	public static boolean hasFontSizeTag(String str) {
		return hasMatch(str, INDEX_FONT_SIZE_TAG);
	}
	
	public static int getFontSizeTagStartIndex(String str) {
		return getTagsStartingIndex( str, INDEX_FONT_SIZE_TAG );
	}
	
	public static int getFontSizeTagEndIndex(String str) {
		return getTagsEndingIndex(str, INDEX_FONT_SIZE_TAG);
	}
	
	public static int getFontSize( String str ) {
		return Integer.valueOf( getTagsContents(str, INDEX_FONT_SIZE_TAG) );
	}
	
	
	/**
	 * Will convert a question or answer string into an ArrayList
	 * It will split the string at any image tag found.
	 * @param str as a question or answer string
	 * @return a String ArrayList.<br>
	 * e.g. question text&lt;image:pic.jpg&gt;image above this.<br>
	 * becomes:<br>
	 * question text,<br> 
	 * &lt;image:pic.jpg&gt;,<br> 
	 * image above this
	 */
	public static ArrayList<String> makeStringAList( String str ) {
		ArrayList<String> listStr = new ArrayList<String>();
		// Replace all the <br> tags with \n.
		//str = str.replaceAll("<[bB][rR]>", "\n");

		// On each loop we will look at the string str, find a tag, store
		// it in an array list and remove if from the string.
		// Each itteration will mean the string is stripped by one tag.
		while( str.compareTo("") != 0){
			String tmp_str;
			
			int tag_start_index = 0;
			int tag_end_index;
			
			if( hasImageTag(str) && (getImageTagStartIndex(str) == 0) ) {
				tag_start_index = 0;
				tag_end_index = getImageTagEndIndex(str);
			} else if( hasReadAlongTimingsTag(str) && (getReadAlongTimingsTagStartIndex(str) == 0) ) {
				tag_start_index = 0;
				tag_end_index   = getReadAlongTimingsTagEndIndex     ( str );

			} else if( hasAudioTag(str) && (getAudioTagStartIndex(str) == 0) ) {
					tag_start_index = 0;
					tag_end_index   = getAudioTagEndIndex     ( str );

			} else if( hasFontSizeTag(str) && (getFontSizeTagStartIndex(str) == 0) ) {
				tag_start_index = 0;
				tag_end_index   = getFontSizeTagEndIndex     ( str );
				
			} else if( hasImageTag(str) && hasFontSizeTag(str)) {
				int font_size_tag_end_index = getFontSizeTagStartIndex( str );  
				int img_tag_end_index       = getImageTagStartIndex     ( str );
				if( img_tag_end_index < font_size_tag_end_index ) {
					tag_end_index = img_tag_end_index;
				} else {
					tag_end_index = font_size_tag_end_index;
				}
				
			} else if ( hasImageTag(str) ) {
				tag_end_index = getImageTagStartIndex( str );
				
			} else if ( hasFontSizeTag(str) ) {
				tag_end_index = getFontSizeTagStartIndex( str );
			} else if ( hasAudioTag(str ) ) {
				tag_end_index = getAudioTagStartIndex( str );
			} else if ( hasReadAlongTimingsTag(str) ) {
 				tag_end_index = getReadAlongTimingsTagStartIndex( str );

			} else {
				//tag_end_index = str.length() - 1;
				tag_end_index = str.length();
			}
			
			tmp_str = str.substring(0, tag_end_index);
			str     = str.substring( tag_end_index );
			
			listStr.add(tmp_str);	
		}
		
		//System.out.println("Array Contents:");
		//for( int i = 0; i < listStr.size(); i++ ) {
		//	System.out.println(listStr.get(i));
		//	if( hasImageTag(listStr.get(i)) ) {
		//		getImageFilename(listStr.get(i));
		//	}
		//}
		
		return listStr;
	}
}
