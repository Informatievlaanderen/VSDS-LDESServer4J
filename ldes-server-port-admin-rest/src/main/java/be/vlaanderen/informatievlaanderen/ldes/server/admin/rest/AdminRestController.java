package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/v1")
public class AdminRestController {


    //@Autowired
    //private

    @InitBinder
    private void initBinder(WebDataBinder binder) {

    }


    @PostMapping(value = "${ldes.collectionname}")
    public void getEventStreams() {

    }


}
