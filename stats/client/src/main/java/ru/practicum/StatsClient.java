package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String statsUri;

    public StatsClient(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.statsUri = serverUrl;
    }

    public void sendHit(EndpointHitDto endpointHitDto) {
        String url = statsUri + "/hit";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Void.class
        );
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        try {
            String urisAsString = String.join(",", uris);

            String url = statsUri + "/stats";
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                    .queryParam("start", start)
                    .queryParam("end", end)
                    .queryParam("uris", urisAsString)
                    .queryParam("unique", unique);

            String requestUrl = builder.toUriString();

            ParameterizedTypeReference<List<ViewStatsDto>> responseType = new ParameterizedTypeReference<List<ViewStatsDto>>() {
            };
            ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    null,
                    responseType
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                List<ViewStatsDto> statsList = response.getBody();
                log.info("Received stats: {}", statsList);
                return statsList;
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Error during request: {}", e.getMessage());

            return Collections.emptyList();
        }
    }
}

