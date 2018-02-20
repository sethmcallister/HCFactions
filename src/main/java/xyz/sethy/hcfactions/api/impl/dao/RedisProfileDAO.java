package xyz.sethy.hcfactions.api.impl.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.Profile;
import xyz.sethy.hcfactions.api.impl.HCFProfile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedisProfileDAO {
    private final Gson gson;
    private final Jedis jedis;

    public RedisProfileDAO() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.jedis = new Jedis();
    }

    public void insert(Profile profile) {
        try (Jedis connection = jedis) {
            connection.set(getKey(profile), this.gson.toJson(profile));
        }
    }

    public void update(Profile profile) {
        try (Jedis connection = jedis) {
            connection.set(getKey(profile), this.gson.toJson(profile));
        }
    }

    public void delete(Profile profile) {
        try (Jedis connection = jedis) {
            connection.del(getKey(profile));
        }
    }

    public Profile find(String string) {
        for (Profile profile : findAll()) {
            if (profile.getName().equalsIgnoreCase(string)) {
                return profile;
            }
        }
        return null;
    }

    public Profile find(UUID uuid) {

        try (Jedis connection = jedis) {
            final String json = connection.get(getKey(uuid));
            return this.gson.fromJson(json, HCFProfile.class);
        }
    }

    public List<Profile> findAll() {
        try (Jedis connection = jedis) {
            return connection.keys(getKeyWithoutIdentifier() + ":*").stream()
                    .map(k -> this.gson.fromJson(connection.get(k), HCFProfile.class))
                    .collect(Collectors.toList());
        }
    }

    private String getKey(UUID uuid) {
        return getKeyWithoutIdentifier() + ":" + uuid.toString();
    }

    private String getKey(Profile profile) {
        return getKeyWithoutIdentifier() + ":" + profile.getUniqueId().toString();
    }

    private String getKeyWithoutIdentifier() {
        return "hcf:profile:" + HCFAPI.getMapName();
    }
}
