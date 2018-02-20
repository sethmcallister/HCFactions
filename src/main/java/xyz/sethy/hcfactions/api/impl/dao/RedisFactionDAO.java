package xyz.sethy.hcfactions.api.impl.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;
import xyz.sethy.hcfactions.api.Faction;
import xyz.sethy.hcfactions.api.HCFAPI;
import xyz.sethy.hcfactions.api.impl.HCFaction;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RedisFactionDAO {
    private final Gson gson;
    private final Jedis jedis;

    public RedisFactionDAO() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.jedis = new Jedis();
    }

    public void insert(Faction faction) {
        try (Jedis connection = jedis) {
            connection.set(getKey(faction), this.gson.toJson(faction));
        }
    }

    public void update(Faction faction) {
        try (Jedis connection = jedis) {
            connection.set(getKey(faction), this.gson.toJson(faction));
        }
    }

    public void delete(Faction faction) {
        try (Jedis connection = jedis) {
            connection.del(getKey(faction));
        }
    }

    public Faction find(Object o) {
        UUID uuid = (UUID) o;

        try (Jedis connection = jedis) {
            final String json = connection.get(getKey(uuid));
            return this.gson.fromJson(json, HCFaction.class);
        }
    }

    public List<Faction> findAll() {
        try (Jedis connection = jedis) {
            return connection.keys(getKeyWithoutIdentifier() + ":*").stream()
                    .map(k -> (Faction) this.gson.fromJson(connection.get(k), HCFaction.class))
                    .collect(Collectors.toList());
        }
    }

    private String getKey(UUID uuid) {
        return getKeyWithoutIdentifier() + ":" + uuid.toString();
    }

    private String getKey(Faction faction) {
        return getKeyWithoutIdentifier() + ":" + faction.getFactionId().toString();
    }

    private String getKeyWithoutIdentifier() {
        return "hcf:faction:" + HCFAPI.getMapName();
    }
}
