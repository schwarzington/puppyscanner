package com.puppyscanner.scanner;

import java.io.IOException;
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
	private final String WW_FULL_INFO_URL = "http://www.petango.com/webservices/adoptablesearch/wsAdoptableAnimalDetails.aspx?id=";
	public ArrayList<Puppy> scan(){
		System.out.println("Starting scan");
		SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
		ArrayList<Puppy> puppies = new ArrayList<Puppy>();
		Document doc;
		PuppyScannerSolrUtil util = new PuppyScannerSolrUtil();
		try {
			System.out.println("Attemping to Connect");
			doc = Jsoup.connect(WW_PUPPY_URL).get();
			Elements listed_puppies = doc.select(".list-item");
			for (Element pup : listed_puppies) {
				try {
					String id = pup.select(".list-animal-id").text();
					Document puppyFullInfo = Jsoup.connect(WW_FULL_INFO_URL + id).get();
					String name = puppyFullInfo.select("#lbName").first().text();
					String[] breeds  = puppyFullInfo.select("#trBreed > .detail-value").first().text().split(",");
					String[] colors  = puppyFullInfo.select("#lblColor").first().text().split("/");
					String age = puppyFullInfo.select("#lbAge").first().text();
					String location = "42.035139,-87.7770977";
					String description = puppyFullInfo.select("#lbDescription").first().text();
					Date intakeDate = formatter.parse(puppyFullInfo.select("#lblIntakeDate").first().text());
					String size = puppyFullInfo.select("#lblSize").first().text();
					String sex = puppyFullInfo.select("#lbSex").first().text();
					Puppy p = new Puppy(name, sex, 10, breeds);
					p.setId(id);
					p.setColor(colors);
					p.setLocation(location);
					p.setDescription(description);
					p.setIntakeDate(intakeDate);
					p.setSize(size);
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
