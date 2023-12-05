package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    @NotNull
    private StateAction status;

    public enum StateAction {
        CONFIRMED,
        REJECTED
    }
}
