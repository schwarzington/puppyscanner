package com.puppyscanner.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.puppyscanner.puppy.Puppy;

public class PuppyScannerSolrUtil {
	String urlString = "http://localhost:8983/solr/puppyscanner";
	SolrClient solr = new HttpSolrClient(urlString);
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
	public ArrayList<String> populateCommonWords(){
		String[] words = {"the","of","to","and","a","in","is","it","you","that","he","was","for","on","are","with","as","i", "these", "of", "their"};
		ArrayList<String> commonWords = new ArrayList<String>();
		return commonWords;
	}
	
	public void insertPuppies(ArrayList<Puppy> puppies) throws SolrServerException, IOException{
		for(Puppy pup: puppies){
			boolean gender = pup.getGender().equals("Male");
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", pup.getId());
			document.addField("animalId", pup.getId());
			document.addField("name", pup.getName());
			document.addField("price", pup.getCost());
			document.addField("sex", gender);
			ArrayList<String> values = new ArrayList<String>(Arrays.asList(pup.getBreed()));
			ArrayList<String> colors = new ArrayList<String>(Arrays.asList(pup.getColor()));
			document.addField("breed", values);
			document.addField("color", colors);
			document.addField("description", pup.getDescription());
			document.addField("located", pup.getLocation());
			document.addField("declawed", false);
			document.addField("species", "Dog");
			document.addField("age", pup.getAge());
			document.addField("size", pup.getSize());
			document.addField("intake", pup.getIntakeDate());
			document.addField("housetrained", false);
			document.addField("cleanDescription", pup.getCleanDescription());
			UpdateResponse response = solr.add(document);
			System.out.println(response.toString());
			solr.commit();
		}
	}
}
