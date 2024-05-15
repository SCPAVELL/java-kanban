package adapters;

import java.io.IOException;
import java.time.Instant;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class InstantAdapter extends TypeAdapter<Instant> {
	@Override
	public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
		if (instant != null) {
			jsonWriter.value(instant.toEpochMilli());
		} else {
			jsonWriter.nullValue();
		}
	}

	@Override
	public Instant read(JsonReader jsonReader) throws IOException {
		if (jsonReader.peek() == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		}
		return Instant.ofEpochMilli(Long.parseLong(jsonReader.nextString()));
	}
}