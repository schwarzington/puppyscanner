package com.puppyscanner.scanner;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.solr.client.solrj.SolrServerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.puppyscanner.puppy.Puppy;
import com.puppyscanner.solr.PuppyScannerSolrUtil;

public class WrightWayScanner implements Scanner{
	private final String WW_PUPPY_URL = "http://www.petango.com/webservices/adoptablesearch/wsAdoptableAnimals.aspx?species=Dog&sex=A&agegroup=UnderYear&location=&site=&onhold=A&orderby=Name&colnum=4&css=http://www.petango.com/WebServices/adoptablesearch/css/styles.css&authkey=io53xfw8b0k2ocet3yb83666507n2168taf513lkxrqe681kf8&recAmount=&detailsInPopup=No&featuredPet=Include&stageID=";
	private final String WW_DOG_URL = "http://www.petango.com/webservices/adoptablesearch/wsAdoptableAnimals.aspx?species=Dog&sex=A&agegroup=OverYear&location=&site=&onhold=A&orderby=Name&colnum=4&css=http://www.petango.com/WebServices/adoptablesearch/css/styles.css&authkey=io53xfw8b0k2ocet3yb83666507n2168taf513lkxrqe681kf8&recAmount=&detailsInPopup=No&featuredPet=Include&stageID=";
	private final String WW_FULL_INFO_URL = "http://www.petango.com/webservices/adoptablesearch/wsAdoptableAnimalDetails.aspx?id=";
	public ArrayList<Puppy> scan(){
		System.out.println("Starting scan");
		SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
		ArrayList<Puppy> puppies = new ArrayList<Puppy>();
		Document doc, doc2;
		PuppyScannerSolrUtil util = new PuppyScannerSolrUtil();
		try {
			System.out.println("Attemping to Connect");
			doc = Jsoup.connect(WW_PUPPY_URL).get();
			Elements listed_puppies = doc.select(".list-item");
			doc2 = Jsoup.connect(WW_DOG_URL).get();
			Elements listed_dogs = doc2.select(".list-item");
			ArrayList<String>scannedPuppies = new ArrayList<String>();
			for(Element pup: listed_puppies){
				 scannedPuppies.add(pup.select(".list-animal-id").text());
			}
			for(Element dog: listed_dogs){
				 scannedPuppies.add(dog.select(".list-animal-id").text());
			}
			ArrayList<String>currentPuppies = util.getPuppiesByShelter("Wright-Way Rescue");
			ArrayList<String>puppiesToBeAdded = util.compareList(scannedPuppies, currentPuppies);
			ArrayList<String>puppiesToBeDeleted = util.compareList(currentPuppies, scannedPuppies);
			
			for (String id : puppiesToBeAdded) {
				try {
					Document puppyFullInfo = Jsoup.connect(WW_FULL_INFO_URL + id).get();
					String name = puppyFullInfo.select("#lbName").first().text();
					String[] breeds  = puppyFullInfo.select("#trBreed > .detail-value").first().text().split(",");
					String[] colors  = puppyFullInfo.select("#lblColor").first().text().split("/");
					String[] age = puppyFullInfo.select("#lbAge").first().text().split(" ");
					int sumAge = 0;
					for(int i = 0; i < age.length; i+= 2){
						if(age[i+1].toLowerCase().contains("year")){
							sumAge += 12 * Integer.parseInt(age[i]);
						} else if(age[i+1].toLowerCase().contains("month")) {
							sumAge += Integer.parseInt(age[i]);
						}
					}
					String location = "42.035139,-87.7770977";
					String description = puppyFullInfo.select("#lbDescription").first().text();
					Date intakeDate = formatter.parse(puppyFullInfo.select("#lblIntakeDate").first().text());
					String size = puppyFullInfo.select("#lblSize").first().text();
					NumberFormat format = NumberFormat.getCurrencyInstance();
					Number number = format.parse(puppyFullInfo.select("#lbPrice").first().text());
					float price = number.floatValue();
					String sex = puppyFullInfo.select("#lbSex").first().text();
					Puppy p = new Puppy(name, sex, sumAge, breeds);
					p.setShelter("Wright-Way Rescue");
					p.setId(id);
					p.setColor(colors);
					p.setLocation(location);
					p.setDescription(description);
					p.setIntakeDate(intakeDate);
					p.setSize(size);
					p.setCost(price);
					puppies.add(p);
				} catch (NullPointerException e){
					continue;
				}
			}
			util.insertPuppies(puppies);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (SolrServerException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return puppies;
		
	}
}
