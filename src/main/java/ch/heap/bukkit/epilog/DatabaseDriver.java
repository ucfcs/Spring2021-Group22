package ch.heap.bukkit.epilog;

import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class DatabaseDriver {
	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> collection;


	public DatabaseDriver(MongoClientURI uri) {
		mongoClient = new MongoClient(uri);
		database = mongoClient.getDatabase("epilog");
		collection = database.getCollection("data");
	}

	public void test() {
		Document document = new Document("name", "Café Con Leche")
				.append("contact",
						new Document("phone", "228-555-0149").append("email", "cafeconleche@example.com")
								.append("location", Arrays.asList(-73.92502, 40.8279556)))
				.append("stars", 3).append("categories", Arrays.asList("Bakery", "Coffee", "Pastries"));

		collection.insertOne(document);
	}

	public void sendData(Document doc) {
		collection.insertOne(doc);
	}
}