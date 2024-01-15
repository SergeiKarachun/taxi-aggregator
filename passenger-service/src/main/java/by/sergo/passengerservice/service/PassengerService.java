package by.sergo.passengerservice.service;

import by.sergo.passengerservice.domain.dto.request.PassengerCreateUpdateRequest;
import by.sergo.passengerservice.domain.dto.response.PassengerListResponse;
import by.sergo.passengerservice.domain.dto.response.PassengerResponse;

public interface PassengerService {
    PassengerResponse create(PassengerCreateUpdateRequest dto);
    PassengerResponse update(Long id, PassengerCreateUpdateRequest dto);
    PassengerResponse delete(Long id);
    PassengerResponse getById(Long id);
    PassengerResponse getByPhone(String phone);
    PassengerListResponse getAll();
    PassengerListResponse getAll(Integer page, Integer size, String field);
}
