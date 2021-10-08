package ch.heap.bukkit.epilog;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.bukkit.Bukkit;

public class DatabaseDriver {
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> collection;

	public DatabaseDriver(ConnectionString uri) {
		mongoClient = MongoClients.create(uri);
		database = mongoClient.getDatabase("epilog");
		collection = database.getCollection("data2");

		Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
	}

	public void sendData(Document doc) {
		try {
			collection.insertOne(doc);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(doc.toString());
		}
	}
}