package me.gleeming.tracker.join;

import lombok.Data;
import org.bson.Document;

import java.util.Date;
import java.util.UUID;

@Data
public class JoinObject {
    private final UUID uuid;
    private final Date date;

    /**
     * Serializes a join object into a document
     * @return Serialized Join Object
     */
    public Document toDocument() {
        return new Document()
                .append("uuid", uuid.toString())
                .append("date", date);
    }

    /**
     * Parses a join object from a document
     *
     * @param document Document
     * @return Join Object
     */
    public static JoinObject fromDocument(Document document) {
        return new JoinObject(
                UUID.fromString(document.getString("uuid")),
                document.getDate("date")
        );
    }
}
