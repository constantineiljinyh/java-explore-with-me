package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewStatsDto {

    private String app;

    private String uri;

    private long hits;
}
