package com.alvarium.serializers;

import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.time.Instant;

public class InstantConverterTest {

    @Test
    public void testJsonFormat() {
        var gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantConverter())
                .create();

        String json = gson.toJson(Instant.now());
        System.out.println(json);
    }

}
