package me.gleeming.tracker.file;

import lombok.Getter;
import lombok.SneakyThrows;
import me.gleeming.tracker.DomainTracker;
import me.gleeming.tracker.join.JoinObject;
import me.gleeming.tracker.util.Callback;
import me.gleeming.tracker.util.FileUtility;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")
public class FileManager {
    @Getter private static FileManager instance;
    public FileManager() { instance = this; }

    // Keeps track of the domains players are connected with
    @Getter private final HashMap<UUID, String> domains = new HashMap<>();

    /**
     * Handles a player joining the server
     *
     * @param player Player
     * @param domain Domain
     */
    public void handleJoin(ProxiedPlayer player, String domain) {
        // Update hashmap for later use
        domains.put(player.getUniqueId(), domain.toLowerCase());

        DomainTracker.getInstance().async(() -> {
            Document data = getData();

            // Make sure join data exists
            if(data.get("joins") == null) data.append("joins", new ArrayList<>());
            List<String> cachedUUIDs = ((List<String>) data.get("joins"));

            // Check if player has played before, if not log their domain
            if(!cachedUUIDs.contains(player.getUniqueId().toString())) {
                cachedUUIDs.add(player.getUniqueId().toString());

                List<Document> domainJoins = (List<Document>) data.get(domain);
                if(domainJoins == null) domainJoins = new ArrayList<>();
                domainJoins.add(new JoinObject(player.getUniqueId(), new Date(System.currentTimeMillis())).toDocument());

                List<String> domains = (List<String>) data.get("domains");
                if(domains == null) domains = new ArrayList<>();
                if(!domains.contains(domain)) domains.add(domain);

                data.append("domains", domains);
                data.append("joins", cachedUUIDs);
                data.append(domain, domainJoins);

                setData(data);
            }
        });
    }

    /**
     * Gets the unique joins for a domain name
     *
     * @param domain Domain
     * @return Unique Joins
     */
    public void getJoins(String domain, Callback<Integer> callback) {
        BungeeCord.getInstance().getScheduler().runAsync(DomainTracker.getInstance(), () -> {
            List<Document> domainJoins = (List<Document>) getData().get(domain.toLowerCase());
            if(domainJoins == null) callback.call(0);
            else callback.call(domainJoins.size());
        });
    }

    /**
     * Gets the unique joins for a domain name
     *
     * @param domain Domain
     * @return Unique Joins
     */
    public void getJoins(String domain, long timeframe, Callback<Integer> callback) {
        if(timeframe == -1) {
            getJoins(domain, callback);
            return;
        }

        BungeeCord.getInstance().getScheduler().runAsync(DomainTracker.getInstance(), () -> {
            List<Document> domainJoins = (List<Document>) getData().get(domain.toLowerCase());
            if(domainJoins == null) {
                callback.call(0);
                return;
            }

            // Create the date that the join needs to be after
            Date date = new Date(System.currentTimeMillis() - timeframe);

            AtomicInteger joins = new AtomicInteger(0);
            domainJoins.stream()
                    .filter(joinDocument -> joinDocument.getDate("date").after(date))
                    .forEach(join -> joins.addAndGet(1));

            // Return the callback
            callback.call(joins.get());
        });
    }



    /**
     * Gets the domains and joins overall
     * @return Joins
     */
    public void getJoins(Callback<HashMap<String, Integer>> callback) {
        BungeeCord.getInstance().getScheduler().runAsync(DomainTracker.getInstance(), () -> {
            Document data = getData();
            HashMap<String, Integer> joins = new HashMap<>();
            ((List<String>) data.get("domains")).forEach(domain -> joins.put(domain, ((List<Document>) data.get(domain)).size()));
            callback.call(joins);
        });
    }

    /**
     * Gets the domains and joins in the past 24 hours
     * @return Joins
     */
    public void getJoins(long timeframe, Callback<HashMap<String, Integer>> callback) {
        if(timeframe == -1) {
            getJoins(callback);
            return;
        }

        BungeeCord.getInstance().getScheduler().runAsync(DomainTracker.getInstance(), () -> {
            Document data = getData();
            HashMap<String, Integer> joins = new HashMap<>();

            // Create the date that the join needs to be after
            Date date = new Date(System.currentTimeMillis() - timeframe);

            ((List<String>) data.get("domains")).forEach(domain -> {
                if(data.get(domain) == null) {
                    joins.put(domain, 0);
                } else {
                    AtomicInteger atomicJoins = new AtomicInteger(0);
                    ((List<Document>) data.get(domain)).forEach(joinDocument -> {
                        if(joinDocument.getDate("date").after(date)) atomicJoins.addAndGet(1);
                    });

                    joins.put(domain, atomicJoins.get());
                }
            });

            // Return the callback
            callback.call(joins);
        });
    }

    /**
     * Gets the domain a player is currently using
     *
     * @param uuid UUID
     * @return Player's UUID
     */
    public String getDomain(UUID uuid) {
        return domains.get(uuid);
    }

    /**
     * Gets the data from the file
     * @return Data
     */
    @SneakyThrows
    public Document getData() {
        File file = new File("domains.json");

        // Check if file exists and if not create it
        if(!file.exists()) {
            file.createNewFile();
            FileUtility.write(file, new Document());
            return new Document();
        }

        return FileUtility.readFromFile(file);
    }

    /**
     * Updates te data file
     * @param document Data
     */
    @SneakyThrows
    public void setData(Document document) {
        File file = new File("domains.json");

        // Check if file exists and if not create it
        if(!file.exists()) file.createNewFile();

        // Write to file
        FileUtility.write(file, document);
    }
}
