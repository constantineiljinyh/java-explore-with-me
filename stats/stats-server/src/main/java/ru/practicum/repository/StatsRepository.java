package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndpointHit h " +
            "where h.timestamp between :start and :end " +
            "and ((:uris) is null or h.uri in :uris) " +
            "group by h.ip, h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<ViewStats> findUniqueStatistics(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select new ru.practicum.model.ViewStats(h.app, h.uri, count(h.ip)) " +
            "from EndpointHit h " +
            "where h.timestamp between :start and :end " +
            "and ((:uris) is null or h.uri in :uris) " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<ViewStats> findStatistics(LocalDateTime start, LocalDateTime end, List<String> uris);
}