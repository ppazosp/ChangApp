package ppazosp.changapp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class NominatimSearch {

    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/search";

    // Search function
    public static List<Place> searchCity(String query) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = NOMINATIM_BASE_URL + "?q=" + query + "&format=json&limit=10";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Place>>() {}.getType();
            assert response.body() != null;
            return gson.fromJson(response.body().string(), listType);
        }
    }
}
