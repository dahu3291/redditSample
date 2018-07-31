package com.example.ajibade.myreddit.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

@Entity(tableName = "posts", indices = {@Index(value = "subreddit")})
public class Post {


    @PrimaryKey @NonNull
    String name;
    String title;
    int score;
    String author;
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    String subreddit;
    int num_comments;
    long created;

    String thumbnail;

    String url;

    // to be consistent w/ changing backend order, we need to keep a data like this
    private int indexInResponse = -1;

    public Post(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public int getNum_comments() {
        return num_comments;
    }

    public void setNum_comments(int num_comments) {
        this.num_comments = num_comments;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIndexInResponse() {
        return indexInResponse;
    }

    public void setIndexInResponse(int indexInResponse) {
        this.indexInResponse = indexInResponse;
    }

    public static class TypeAdapter implements JsonDeserializer<Post> {

        @Override
        public Post deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            Post post = new Post("");
            if (!"t3".equals(json.getAsJsonObject().get("kind").getAsString())) return post;

            JsonObject data = json.getAsJsonObject().get("data").getAsJsonObject();
            post.name = data.get("name").getAsString();
            post.title = data.get("title").getAsString();
            post.score = data.get("score").getAsInt();
            post.author = data.get("author").getAsString();
            post.subreddit = data.get("subreddit").getAsString();
            post.num_comments = data.get("num_comments").getAsInt();
            post.created = data.get("created_utc").getAsLong();
            post.thumbnail = data.get("thumbnail").getAsString();
            post.url = data.get("url").getAsString();

            return post;
        }
    }
}
