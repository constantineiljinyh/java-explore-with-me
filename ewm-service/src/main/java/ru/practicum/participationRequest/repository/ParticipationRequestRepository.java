package ru.practicum.participationRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.participationRequest.model.ParticipationRequest;
import ru.practicum.participationRequest.model.ParticipationRequestState;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long id);

    List<ParticipationRequest> findAllByEventId(Long id);

    List<ParticipationRequest> findAllByIdIn(List<Long> ids);

    Long countByEventIdAndStatus(Long eventId, ParticipationRequestState status);
}
