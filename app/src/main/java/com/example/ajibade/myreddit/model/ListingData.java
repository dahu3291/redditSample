package com.example.ajibade.myreddit.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListingData {
    private List<Post> children = new ArrayList<>();
    private String after;
    private String before;

    public List<Post> getChildren() {
        return children;
    }

    public String getAfter() {
        return after;
    }

    public String getBefore() {
        return before;
    }

    public boolean afterNotNull() {return after != null;}
    public boolean beforeNotNull() {return before != null;}

    public static class TypeAdapter implements JsonDeserializer<ListingData> {

        @Override
        public ListingData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            ListingData listingData = new ListingData();
            if (!"Listing".equals(json.getAsJsonObject().get("kind").getAsString())) return listingData;

            JsonObject data = json.getAsJsonObject().get("data").getAsJsonObject();

            JsonArray list = data.get("children").getAsJsonArray();
            List<Post> children = new ArrayList<>(list.size());
            for (JsonElement element: list) {
                children.add(context.deserialize(element, Post.class));
            }
            listingData.children = children;

            if (data.has("after") && !data.get("after").isJsonNull())
                listingData.after = data.get("after").getAsString();
            if (data.has("before") && !data.get("before").isJsonNull())
                listingData.before = data.get("before").getAsString();

            return listingData;
        }
    }
}
