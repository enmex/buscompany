package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.client.ChooseSeatDtoRequest;
import net.thumbtack.school.buscompany.dto.response.client.ChooseSeatDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.GetFreePlacesDtoResponse;
import net.thumbtack.school.buscompany.service.ClientService;
import net.thumbtack.school.buscompany.validation.Id;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/places")
public class PlaceController {
    private final ClientService clientService;

    public PlaceController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetFreePlacesDtoResponse getFreePlaces(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                  @PathVariable String orderId){
        return clientService.getFreePlaces(cookieValue, orderId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChooseSeatDtoResponse choosePlace(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                            @RequestBody @Valid ChooseSeatDtoRequest request){
        return clientService.choosePlace(cookieValue, request);
    }
}
