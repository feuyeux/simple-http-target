package com.aliyuneventbridge.httptarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HttpTargetApplication {
    Logger logger = LoggerFactory.getLogger(HttpTargetApplication.class);
    // Record received cloudevents
    List<Map<String, Object>> eventList = new CopyOnWriteArrayList<>();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static void main(String[] args) {
		SpringApplication.run(HttpTargetApplication.class, args);
	}

    @RequestMapping("/cloudevents")
    public String onEvents(@RequestHeader Map<String, Object> headers, @RequestBody String body) {
        Map<String, Object> receivedEvent = new HashMap<>();
        try {
            // The schema of body is cloudevents v1.0 in general
            final Map<String, Object> bodyJsonMap = new Gson().fromJson(body, Map.class);
            receivedEvent.put("HttpBody", bodyJsonMap);
        } catch (Throwable e) {
            // You could push a constant body through eventbridge transformer
            receivedEvent.put("HttpBody", body);
        }
        receivedEvent.put("HttpHeaders", headers);

        logger.info(gson.toJson(receivedEvent));
        addEvent(receivedEvent);

	    return "OK";
    }

    @RequestMapping("/httptrace")
    public String requestLists() {
        return gson.toJson(eventList);
    }

    private void addEvent(Map<String, Object> event) {
        eventList.add(0, event);

        if (eventList.size() > 10) {
            eventList.remove(eventList.size() - 1);
        }
    }
}

