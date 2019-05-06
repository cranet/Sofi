package com.toad.sofiapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ImgurDeserializer implements JsonDeserializer<ArrayList<ImgurImage>> {

    @Override
    public ArrayList<ImgurImage> deserialize(JsonElement json, Type typeOfT,
                                             JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray data = (JsonArray) jsonObject.get("data");
        ArrayList<ImgurImage> images = new ArrayList<>();

        /*
        Use the album cover image if result is an album
         */
        for (JsonElement item : data) {
            JsonObject itemObject = item.getAsJsonObject();
            ImgurImage image = new ImgurImage();
            image.title = itemObject.get("tvTitle").getAsString();

            if (itemObject.get("is_album").getAsBoolean()) {
                image.id = itemObject.get("cover").getAsString();
            } else {
                image.id = itemObject.get("id").getAsString();
            }
            images.add(image);
        }
        return images;
    }
}
