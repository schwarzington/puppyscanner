package com.puppyscanner.solr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import com.puppyscanner.main.util.ScannerUtil;
import com.puppyscanner.puppy.Puppy;

public class PuppyScannerSolrUtil {
	String urlString = "http://localhost:8983/solr/puppyscanner";
	static final Logger logger = LogManager.getLogger(PuppyScannerSolrUtil.class.getName());
	SolrClient solr = new HttpSolrClient(urlString);
	ScannerUtil scanUtil = new ScannerUtil();
	Properties properties = scanUtil.initializeProperties();

	public String textFilter(String dirtyText) {
		List<String> commonWords = populateCommonWords();
		dirtyText = dirtyText.replaceAll("[^\\w\\s]", " ").toLowerCase();
		String[] wordArray = dirtyText.split(" ");
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(wordArray));
		list.removeAll(commonWords);
		String cleanText = "";
		for (String word : list) {
			cleanText += word + " ";
		}
		return cleanText;
	}

	public ArrayList<String> cleanList(ArrayList<String> dirtyList) {
		ArrayList<String> cleanList = new ArrayList<String>();
		for (String value : dirtyList) {
			cleanList.add(value.trim());
		}
		return cleanList;
	}

	public ArrayList<String> populateCommonWords() {
		String[] words = { "the", "of", "to", "and", "a", "in", "is", "it", "you", "that", "he", "was", "for", "on",
				"are", "with", "as", "i", "these", "of", "their" };
		ArrayList<String> commonWords = new ArrayList<String>();
		return commonWords;
	}

	public void insertPuppies(ArrayList<Puppy> puppies) throws SolrServerException, IOException {
		for (Puppy pup : puppies) {
			boolean gender = pup.getGender().equals("Male");
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", pup.getId());
			document.addField("animalId", pup.getId());
			document.addField("name", pup.getName());
			document.addField("price", pup.getCost());
			document.addField("sex", gender);
			ArrayList<String> values = cleanList(new ArrayList<String>(Arrays.asList(pup.getBreed())));
			ArrayList<String> colors = cleanList(new ArrayList<String>(Arrays.asList(pup.getColor())));
			ArrayList<String> images = cleanList(new ArrayList<String>(Arrays.asList(pup.getPictureURL())));
			document.addField("breed", values);
			document.addField("color", colors);
			document.addField("images", images);
			document.addField("description", pup.getDescription());
			document.addField("located", pup.getLocation());
			document.addField("declawed", false);
			document.addField("species", "Dog");
			document.addField("age", pup.getAge());
			document.addField("size", pup.getSize());
			document.addField("shelter", pup.getShelter());
			document.addField("intake", pup.getIntakeDate());
			document.addField("housetrained", false);
			document.addField("cleanDescription", pup.getCleanDescription());
			solr.add(document);
			logger.debug("Successfully added " + pup.getName() + " Breed:" + pup.getBreed()[0]);
			solr.commit();
		}
	}

	public ArrayList<String> getPuppiesByShelter(String shelter) throws SolrServerException, IOException {
		ArrayList<String> animalIds = new ArrayList<String>();
		SolrQuery query = new SolrQuery().setRows(5000);
		query.set("q", "shelter:" + shelter);
		QueryResponse rsp = solr.query(query);
		Iterator<SolrDocument> iter = rsp.getResults().iterator();
		int count = 0;
		while (iter.hasNext()) {
			SolrDocument resultDoc = iter.next();
			String id = (String) resultDoc.getFieldValue("id");
			animalIds.add(id);
			count++;
		}
		System.out.println(count);
		return animalIds;
	}

	public ArrayList<String> compareList(ArrayList<String> first, ArrayList<String> second) {
		ArrayList<String> animalIds = new ArrayList<String>();
		for (String id : first) {
			if (!second.contains(id)) {
				System.out.println(id);
				animalIds.add(id);
			}
		}
		return animalIds;
	}

	public void deletePuppies(ArrayList<String> puppies) throws SolrServerException, IOException {
		for (String puppy : puppies) {
			System.out.println(puppy);
			solr.deleteById(puppy);
			solr.commit();
		}
	}

	public ArrayList<String> getImages(ArrayList<String> images, String id) {
		String folder = null;
		ArrayList<String> locations = new ArrayList<String>();
		int counter = 1;
		for (String src : images) {
			// Exctract the name of the image from the src attribute
			String[] words = src.split("\\.");
			String name = properties.getProperty("filePath") + id + "_" + counter + "." + words[words.length - 1];
			System.out.println(name);
			locations.add(properties.getProperty("filePath") + id + "_" + counter + "." + words[words.length - 1]);
			if (!new File(name).exists()) {
				try {
					// Open a URL Stream
					URL url = new URL(src);
					InputStream in = url.openStream();
					OutputStream out = new BufferedOutputStream(new FileOutputStream(name));
					for (int b; (b = in.read()) != -1;) {
						out.write(b);
					}
					out.close();
					in.close();
				} catch (IOException e) {
					logger.error("Error getting image", e);
				}
			}

		}
		return locations;
	}
}
