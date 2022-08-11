package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.PROV_GENERATED_AT_TIME;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

public class TimestampPathComparator implements Comparator<LdesMember> {
	
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]'Z'");

    @Override
    public int compare(LdesMember firstLdesMember, LdesMember secondLdesMember) {
        LocalDateTime dateTimeFirstLdesMember = LocalDateTime.parse(firstLdesMember.getFragmentationObject(PROV_GENERATED_AT_TIME).toString(), formatter);
        LocalDateTime dateTimeSecondLdesMember = LocalDateTime.parse(secondLdesMember.getFragmentationObject(PROV_GENERATED_AT_TIME).toString(), formatter);
        return dateTimeFirstLdesMember.compareTo(dateTimeSecondLdesMember);
    }
}
