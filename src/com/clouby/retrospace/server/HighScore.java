package com.clouby.retrospace.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.JSONException;



public class HighScore extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3961076477647464957L;
	static final String ENTITY_NAME = "HighScoreRecord";
	static final String PASSWORD = "*******";
	static final int numOfEntities = 5; 
	
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query query = new Query(ENTITY_NAME);
		query.addSort("score",SortDirection.DESCENDING );
		PreparedQuery pq = datastore.prepare(query);
		JSONObject jsonEntity = new JSONObject(); 
		JSONArray jsonEntityArray = new JSONArray(); 

		int i = 0; 
		for (Entity result : pq.asIterable()) {
			if( i >=  numOfEntities){
				break; 
			}
			long score = (long) result.getProperty("score");
			String name  = (String) result.getProperty("name");
		
			try {
				jsonEntity.put("score", score);
				jsonEntity.put("name", name);
				jsonEntityArray.put(jsonEntity);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jsonEntity = new JSONObject(); 
			i++;
		}
		
	    try {
			jsonEntityArray.write(resp.getWriter());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		if(req.getParameter("pass").equals(PASSWORD)){
			// Places the location parameters in the same entity group as the Location record
			Entity highscoreInst = new Entity(ENTITY_NAME);


			highscoreInst.setProperty("date", new Date(Long.parseLong(req.getParameter("date"))));
			highscoreInst.setProperty("name", req.getParameter("name"));
			highscoreInst.setProperty("score", Long.parseLong(req.getParameter("score")));
		
			// Now put the entry to Google data store
			DatastoreService datastore =
					DatastoreServiceFactory.getDatastoreService();
			datastore.put(highscoreInst);
		}
	}
}
