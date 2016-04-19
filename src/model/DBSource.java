package model;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by torgammelgard on 2016-04-11.
 */
public class DBSource {

    private static final String DB_NAME = "learning_assistant";
    private static final MongoClient mongoClient = new MongoClient();

    private DBSource() {
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static void closeMongoClient() {
        mongoClient.close();
    }

    public static void addCard(Card card, String collectionName) {
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);

        db.getCollection(collectionName).insertOne(
                new Document("question", card.getQuestion()).append("answerAlternatives", Arrays.asList(card.getAnswerAlternatives())));
    }

    public static boolean deleteCard(Card card, String collectionName) {
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);

        DeleteResult deleteResult = db.getCollection(collectionName).deleteOne(new Document("question", card.getQuestion()));
        return deleteResult.getDeletedCount() > 0;
    }

    public static boolean editCard(Card cardToEdit, Card editedCard, String collectionName) {
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);

        UpdateResult updateResult = db.getCollection(collectionName).updateOne(new Document("question", cardToEdit.getQuestion()),
                new Document("$set", new Document("question", editedCard.getQuestion())
                        .append("answerAlternatives", Arrays.asList(editedCard.getAnswerAlternatives()))));
        return updateResult.getModifiedCount() > 0;
    }

    public static List<String> getCollectionNames() {
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);

        MongoIterable<String> collectionNames = db.listCollectionNames();

        ArrayList<String> names = new ArrayList<>();

        collectionNames.forEach(new Block<String>() {
            @Override
            public void apply(String s) {
                names.add(s);
            }
        });
        return names;
    }

    public static List<Card> getCollection(String collectionName) {
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);

        FindIterable<Document> res = db.getCollection(collectionName).find();

        ArrayList<Card> cards = new ArrayList<>();

        res.forEach(new Block<Document>() {

            @Override
            public void apply(Document document) {
                cards.add(DBSource.documentToCard(document));
            }
        });

        return cards;
    }

    public static List<Card> search(String searchString, String collectionName) {
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);

        FindIterable<Document> res = db.getCollection(collectionName).find(
                new Document("question", Pattern.compile(searchString)));

        ArrayList<Card> cards = new ArrayList<>();

        res.forEach(new Block<Document>() {
            @Override
            public void apply(Document document) {
                cards.add(DBSource.documentToCard(document));
            }
        });
        return cards;
    }

    private static Card documentToCard(Document document) {
        Card card = new Card();
        card.setQuestion((String) document.get("question"));
        ArrayList<Object> ansAlts = (ArrayList<Object>) document.get("answerAlternatives");
        card.setAnswerAlternatives(ansAlts.toArray(new String[]{}));
        return card;
    }
}
