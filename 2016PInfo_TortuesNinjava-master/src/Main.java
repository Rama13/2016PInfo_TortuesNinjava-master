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
			System.out.println("3 - Utilisateur contribuant le plus dans un ensemble de sujets");
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
				
				if (tag1.equals("c#")) tag1 = "c%23";
				
				String addr1 = "http://api.stackexchange.com/2.2/tags/" + tag1 + "/top-answerers/all_time?site=stackoverflow"
;
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
				
				if (tag2.equals("c#")) tag2 = "c%23";

				String addr2 = "http://api.stackexchange.com/2.2/tags/" + tag2 + "/top-answerers/all_time?site=stackoverflow"
;
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
				// PAR ENSEMBLE DE TAGS
				String ens; String[] tags;
				do {
					System.out.println("Veuillez entrer les tags");
					Scanner sc3 = new Scanner(System.in);
					ens = sc3.nextLine();
					tags = ens.split(" ");
					
					boolean b = true;
					for (String s : tags) {
						if (!checkTag(s)) {
							System.out.println("Tag invalide.\n");
							b = false;
						}
					}
					if (b) break;
				} while (true);		
				
				JSONArray js_ens = new JSONArray();
				for (String s : tags) {
					if (s.equals("c#")) s = "c%23";
					String addr3 = "http://api.stackexchange.com/2.2/tags/" + s + "/top-answerers/all_time?site=stackoverflow";
					js_ens.put(readJsonFromUrl(addr3).get("items"));
					//System.out.println(readJsonFromUrl(addr3).get("items").toString());
				}
				JSONArray json3 = join (js_ens);
				json3 = sortByPosts(json3);
				System.out.println(json3.getJSONObject(0).getJSONObject("user").get("display_name"));
				System.out.println(json3.getJSONObject(0).getJSONObject("user").get("link"));
				System.out.println(json3.getJSONObject(0).get("post_count") + " posts au total");
				break;
			default :
				
				break;
		}
		
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
		String addrTags = "http://api.stackexchange.com/2.2/tags?pagesize=100&order=desc&sort=popular&site=stackoverflow";

		JSONObject tagListO = readJsonFromUrl(addrTags);
		JSONArray tagList = (JSONArray) tagListO.get("items");
		
		for (int k = 0; k < tagList.length(); k++) {
			if (tagList.getJSONObject(k).getString("name").equals(tag)) {
				b = true;
			}
		}
		
		return b;
	}
	
	private static JSONArray join (JSONArray a) throws JSONException {
		JSONArray r = new JSONArray();
		List<List<Integer>> annuaire = new ArrayList<List<Integer>>();
		
		for (int i = 0; i < a.length(); i++) {
			for (int j = 0; j < a.getJSONArray(i).length(); j++) {
				int id = a.getJSONArray(i).getJSONObject(j).getJSONObject("user").getInt("user_id");
				annuaire.add(Arrays.asList(id, i, j, a.getJSONArray(i).getJSONObject(j).getInt("post_count")));
			}
		}
		
		for (int k = 0; k < annuaire.size(); k++) {
			for (int l = k+1; l < annuaire.size(); l++) {
				if (annuaire.get(k).get(0).equals(annuaire.get(l).get(0))) {
					annuaire.get(k).set(3, annuaire.get(k).get(3) + annuaire.get(l).get(3));
					annuaire.remove(l);
				}
			}
		}
		
		for (List<Integer> g : annuaire) {
			JSONObject o = a.getJSONArray(g.get(1)).getJSONObject(g.get(2));
			o.put("post_count", g.get(3));
			r.put(o);
		}
		
		return r;
	}
}
