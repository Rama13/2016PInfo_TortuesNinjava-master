package ninja;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;


public class Main {

	static String key = "&key=Pw9D)judG8FE4tJR3qjUTA((";
	
	public static void main(String[] args) throws MalformedURLException, IOException, JSONException {
	
		
		System.setProperty("http.proxyHost", "cache.univ-st-etienne.fr");
		System.setProperty("http.proxyPort", "3128");
		System.setProperty("http.proxyHost", "cache.univ-st-etienne.fr");
		System.setProperty("http.proxyPort", "3128");
		
		int cmd = -1;
		while (cmd < 0 || cmd > 3) {
			System.out.println("Que souhaitez-vous faire ?");
			System.out.println("1 - Utilisateur top tag");
			System.out.println("2 - 10 utilisateurs les plus actifs");
			//System.out.println("3 - Utilisateur contribuant le plus dans un ensemble de sujets");
			//System.out.println("0 - Sortir");
			Scanner sc0 = new Scanner(System.in);
			cmd = sc0.nextInt();
		}
		
		switch (cmd) {
			case 1 :
				String tag1 = "";
				do {
					System.out.println("Veuillez entrer un tag");
					Scanner sc1 = new Scanner(System.in);
					tag1 = sc1.nextLine();
					if (!checkTag(tag1)) {
						System.out.println("Tag invalide.\n");
					} else {
						break;
					}
				} while (true);
				
				String addr1 = "http://api.stackexchange.com/2.2/tags/" + tag1 + "/top-answerers/all_time?site=stackoverflow" + key;
				JSONObject json1 = readJsonFromUrl(addr1);
				JSONArray tab1 = (JSONArray) json1.get("items");

				// PERSONNE TOP TAG
				System.out.println ("\nTop tag user in " + tag1 + "\n");
				System.out.println (tab1.getJSONObject(0).getJSONObject("user").get("display_name"));
				System.out.println (tab1.getJSONObject(0).getJSONObject("user").get("link"));
				break;
			case 2 :
				String tag2 = "";
				do {
					System.out.println("Veuillez entrer un tag");
					Scanner sc2 = new Scanner(System.in);
					tag2 = sc2.nextLine();
					if (!checkTag(tag2)) {
						System.out.println("Tag invalide.\n");
					} else {
						break;
					}
				} while (true);

				String addr2 = "http://api.stackexchange.com/2.2/tags/" + tag2 + "/top-answerers/all_time?site=stackoverflow" + key ;
				JSONObject json2 = readJsonFromUrl(addr2);
				JSONArray tab2 = (JSONArray) json2.get("items");

				// TOP 10
				System.out.println ("\n\n\nTop 10 users in " + tag2);
				JSONArray top10 = sortByPosts (tab2);
				for (int i = 0; i < 10; i++) {
					JSONObject obj = top10.getJSONObject(i).getJSONObject("user");
					System.out.println ("");
					System.out.println(obj.get("display_name"));
					System.out.println(obj.get("link"));
					System.out.println("Posts : " + top10.getJSONObject(i).get("post_count"));
				}
				break;
			case 3 :
				
				break;
			default :
				
				break;
		}
		
		/*// PAR ENSEMBLE DE TAGS
		String ens = "c javascript python";
		String[] tags = ens.split(" ");
		JSONArray js_ens = new JSONArray();
		for (String s : tags) {
			String addr3 = "http://api.stackexchange.com/2.2/tags/" + s + "/top-answerers/all_time?site=stackoverflow";
			js_ens.put(readJsonFromUrl(addr2));
		}*/
		
	}
	public static JSONObject readJsonFromUrl (String url) throws MalformedURLException, IOException, JSONException {
		URL link = new URL(url);
		InputStream is = link.openStream();
		GZIPInputStream gis = new GZIPInputStream(is);
		BufferedReader reader = new BufferedReader(new InputStreamReader(gis, Charset.forName("UTF-8")));
		JSONObject jo = new JSONObject(readAll(reader));
		
		is.close();
		return jo;
	}


	private static String readAll (Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	  }
	
	
	private static JSONArray sortByPosts (JSONArray tab) throws JSONException {
		JSONArray sorted = new JSONArray();
		
		for (int i = 0; i < 10; i++) {
			int count = tab.getJSONObject(0).getInt("post_count");
			int ind_max = 0;
			for (int j = 1; j < tab.length(); j++) {
				if (tab.getJSONObject(j).getInt("post_count") > count) {
					count = tab.getJSONObject(j).getInt("post_count");
					ind_max = j;
				}
			}
			sorted.put(tab.getJSONObject(ind_max));
			tab.remove(ind_max);
		}
		return sorted;
	}
	
	private static boolean checkTag (String tag) throws MalformedURLException, IOException, JSONException {
		boolean b = false;
		String addrTags = "http://api.stackexchange.com/2.2/tags?pagesize=100&order=desc&sort=popular&site=stackoverflow" + key;

		JSONObject tagListO = readJsonFromUrl(addrTags);
		JSONArray tagList = (JSONArray) tagListO.get("items");
		
		for (int k = 0; k < tagList.length(); k++) {
			if (tagList.getJSONObject(k).getString("name").equals(tag)) {
				b = true;
				//#=%23
			}
		}
		
		return b;
	}
}


