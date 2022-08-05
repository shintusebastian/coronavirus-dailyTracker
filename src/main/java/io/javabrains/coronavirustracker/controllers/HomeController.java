package io.javabrains.coronavirustracker.controllers;

import io.javabrains.coronavirustracker.models.LocationStats;
import io.javabrains.coronavirustracker.services.CoronavirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
/*we marked this as a controller and not a Rest Controller. Because this controller is not returning a rest
 * response. If we add a @RestController annotation, it means that all the methods in that Controller class returns a
 * Rest response. So, it has to be converted in to a JSON response and returned.
 * Here, we want to render a UI. so we are not marking it as a rest controller. So here, we are returning a name
 * which is pointing to a template. */
public class HomeController {
    @Autowired //autowired the Coronavirus data service so that the controller can access it.
    CoronavirusDataService coronavirusDataService;

    //    @GetMapping("/")//when there is a get mapping to / or the root url, return the home template.
//    public String home() {
//        return "home";//which should be mapped to an HTML file in the templates folder in resources.
////    }
//
//    @GetMapping("/")
//    public String home(Model model){
//        model.addAttribute("testName", "TEST");
//        //we placed an attribute on this model named testName
//        return "home";
//    }
    @GetMapping("/")
    public String home(Model model) {
        List<LocationStats> allStats = coronavirusDataService.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(l -> l.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(s -> s.getDiffFromPreviousDay()).sum();
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);

        return "home";
    }

}
