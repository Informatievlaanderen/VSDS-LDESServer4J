package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.comparator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.contants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class TimestampPathComparator implements Comparator<LdesMember> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]'Z'");

    private static final String TIMESTAMP_PATH = RdfConstants.PROV_GENERATED_AT_TIME;

    @Override
    public int compare(LdesMember firstLdesMember, LdesMember secondLdesMember) {
        LocalDateTime dateTimeFirstLdesMember = LocalDateTime.parse(firstLdesMember.getFragmentationValue(TIMESTAMP_PATH), formatter);
        LocalDateTime dateTimeSecondLdesMember = LocalDateTime.parse(secondLdesMember.getFragmentationValue(TIMESTAMP_PATH), formatter);
        return dateTimeFirstLdesMember.compareTo(dateTimeSecondLdesMember);
    }
}
