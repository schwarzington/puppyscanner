package com.puppyscanner.scanner;

import java.io.IOException;
import java.util.ArrayList;





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
		ArrayList<Puppy> puppies = new ArrayList<Puppy>();
		Document doc;
		try {
			System.out.println("Attemping to Connect");
			doc = Jsoup.connect(WW_PUPPY_URL).get();
			Elements listed_puppies = doc.select(".list-item");
			for (Element pup : listed_puppies) {
				String id = pup.select(".list-animal-id").text();
				Document puppyFullInfo = Jsoup.connect(WW_FULL_INFO_URL + id).get();
				String name = puppyFullInfo.select("#lbName").first().text();
				String[] breeds  = puppyFullInfo.select("#trBreed > .detail-value").first().text().split(",");
				String age = puppyFullInfo.select("#lbAge").first().text();
				
				PuppyScannerSolrUtil util = new PuppyScannerSolrUtil();
				System.out.println(util.textFilter(puppyFullInfo.select("#lbDescription").first().text()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return puppies;
		
	}
}
