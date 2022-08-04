package io.javabrains.coronavirustracker.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CoronavirusDataService {
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    @PostConstruct//telling spring after constructing the instance of CoronavirusDataService, run this method
    public void fetchVirusData() throws IOException, InterruptedException {//we are making an http call using this method. For that we use http client in java.
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(httpResponse.body());
        /* if we run the program, we get the entire corona cases printed in the console in a csv format. So, we have to
        parse the csv file. It is printed as a string. Instead of splitting the string using split method, it is easier
        to use the apache commons csv library.
        we have added the apache commons library to our pom file to parse the httpResponse.body() to values which we can use.
        we can take the location, longitude, etc. out from the http response body.
        * */
StringReader csvBodyReader=new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            String state = record.get("Province/State");
            System.out.println(state);

        }
    }
}
