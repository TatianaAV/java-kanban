package ru.yandex.practicum.kanban.manager.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

import java.time.LocalTime;
import java.util.Objects;

import static jdk.internal.org.jline.utils.Colors.s;


public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        Object value = Objects.nonNull(duration)? duration :  Duration.ZERO;
        jsonWriter.value( value.toString() /*String.format("%02d:%02d:%02d", s/ 3600, (s% 3600) / 60, (s% 60))*/);

    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {

        return Duration.parse(jsonReader.nextString());
                 }
}
