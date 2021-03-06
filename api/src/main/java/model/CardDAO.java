package model;

import com.mongodb.BasicDBObject;
import com.mongodb.Function;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import model.entities.Card;
import model.entities.CardImpl;
import org.bson.Document;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static model.DatabaseHelper.*;

@Stateless
public class CardDAO {

    private MongoDatabase mongoDatabase;

    @Inject
    @MongoFromContainer
    private MongoClient mongoClient;

    @PostConstruct
    public void init() {
        this.mongoDatabase = mongoClient.getDatabase(DB_NAME);
    }

    public Optional<Card> getCard(long id) {
        BasicDBObject query = new BasicDBObject();
        query.put(KEY_ID, id);
        return Optional.ofNullable(mongoDatabase.getCollection(COLLECTION_NAME).find(query).map(docToCard).first());
    }

    List<Card> getCards() {
        List<Card> cards = new ArrayList<>();
        return mongoDatabase.getCollection(COLLECTION_NAME).find().map(docToCard).into(cards);

    }

    private Function<Document, Card> docToCard = document -> {
        Card newCard = new CardImpl();
        newCard.setId(document.getInteger(KEY_ID));
        newCard.setQuestion(document.getString(KEY_QUESTION));
        List ansAlts = (ArrayList) document.get(KEY_ANSWER_ALTERNATIVES);
        ArrayList<String> strings = new ArrayList<>();
        for (Object o : ansAlts) {
            if (o instanceof String) {
                strings.add((String) o);
            }
        }
        newCard.setAnswerAlternatives(strings.toArray(new String[]{}));
        if (document.keySet().contains(KEY_PRIORITY)) {
            int priority = document.getInteger(KEY_PRIORITY);
            newCard.setPriority(Prioritizable.Priority.values()[priority]);
        } else {
            newCard.setPriority(null);
        }
        return newCard;
    };
}
