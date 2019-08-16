import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static spark.Spark.get;
import static spark.Spark.port;

public class Server {

    public static Site[] getJSON(String urlString) {
        try {
            URL url = new URL(urlString);
            try {
                URLConnection urlConnection;
                if ((urlConnection = url.openConnection()) instanceof HttpURLConnection) {
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    HttpURLConnection connection = (HttpURLConnection) urlConnection;
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    Gson gsonSites = new Gson();
                    Site[] sites = gsonSites.fromJson(in, Site[].class);
                    sites = sortSites(sites);

                    return sites;
                } else {
                    return new Site[0];
                }
            } catch (IOException exception) {
            }
        } catch (MalformedURLException exception) {
        }
        return new Site[0];
    }

    public static Site[] sortSites(Site[] sites) {
        Site temp;
        for (int j = 0; j < sites.length; j++) {
            for (int i = j + 1; i < sites.length; i++) {
                if (sites[i].getName().compareTo(sites[j].getName()) < 0) {
                    temp = sites[j];
                    sites[j] = sites[i];
                    sites[i] = temp;
                }
            }
        }
        return sites;
    }

    public static void main(String[] args) {

        String apiURL = "https://api.mercadolibre.com/sites/";


        port(8085);

        get("/", (req, res) -> new Gson().toJsonTree(new Site()));

        get("/sites", (req, res) -> {
            res.type("application/json");
            return new Gson().toJson(getJSON(apiURL), Site[].class);
        });

        get("/sites/:id/categories", (req, res) -> {
            res.type("application/json");
            return new Gson().toJson(getJSON(apiURL + req.params(":id") + "/categories"), Site[].class);
        });


    }
}