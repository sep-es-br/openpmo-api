package br.gov.es.openpmo.dto.indicators;

import br.gov.es.openpmo.model.indicators.Indicator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class IndicatorCardDto {

    private final Long id;

    @NotNull
    @NotEmpty
    private final String name;

    @NotNull
    @NotEmpty
    private final String description;

    public IndicatorCardDto(
            final Long id,
            final String name,
            final String description
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static IndicatorCardDto of(final Indicator indicator) {
        return new IndicatorCardDto(
                indicator.getId(),
                indicator.getName(),
                indicator.getDescription()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
