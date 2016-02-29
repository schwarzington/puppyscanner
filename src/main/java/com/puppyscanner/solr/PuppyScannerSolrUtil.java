package com.puppyscanner.solr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.puppyscanner.puppy.Puppy;

public class PuppyScannerSolrUtil {
	public String textFilter(String dirtyText){
		List<String> commonWords = populateCommonWords();
		dirtyText = dirtyText.replaceAll("[^\\w\\s]"," ").toLowerCase();
		String[] wordArray = dirtyText.split(" ");
		ArrayList<String>list = new ArrayList<String>(Arrays.asList(wordArray)); 
		list.removeAll(commonWords);
		String cleanText = "";
		for(String word: list){
			cleanText += word + " ";
		}
		return cleanText;
	}
	public boolean insertPuppy(Puppy puppy){
		return true;
	}
	public ArrayList<String> populateCommonWords(){
		String[] words = {"the","of","to","and","a","in","is","it","you","that","he","was","for","on","are","with","as","i", "these", "of", "their"};
		ArrayList<String> commonWords = new ArrayList<String>();
		return commonWords;
	}
}
