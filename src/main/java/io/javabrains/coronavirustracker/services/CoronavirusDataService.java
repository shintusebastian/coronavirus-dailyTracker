package io.javabrains.coronavirustracker.services;

import io.javabrains.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronavirusDataService {
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct//telling spring after constructing the instance of CoronavirusDataService, run this method

    @Scheduled(cron = "* * 1 * * *")//this tells spring to run this method every second.
    /*we want to run this application not only once. But also, on a regular basis. suppose if we deploy this on AWS and
    run this for a while, then we can schedule running of this method. Scheduled annotation schedules the run of a method
    on a regular basis.
    Also, we need to Enable scheduling in the Main class to tell the application to enable scheduling.
    * */
    public void fetchVirusData() throws IOException, InterruptedException {//we are making an http call using this method. For that we use http client in java.
        List<LocationStats> newStats = new ArrayList<>();
        /*if someone is making a request at the time we are updating the stats, they should be able to see the old data
        that is why we are creating a list of newStats.*/
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
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases= Integer.parseInt(record.get(record.size() - 2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPreviousDay(latestCases - prevDayCases);

            //record.size() will give us the last column in the record.
            //record.get() gives us a string. So, we parsed it into an Integer.

//            String state = record.get("Province/State");
//            System.out.println(state);
//            System.out.println(locationStat);
            newStats.add(locationStat);

        }
        this.allStats = newStats;
    }
}
