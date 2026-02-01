package io.github.vulpes.domain.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.MonthDay;

@Converter(autoApply = false)
public class MonthDayAttributeConverter implements AttributeConverter<MonthDay, String> {

    @Override
    public String convertToDatabaseColumn(MonthDay attribute) {
        if (attribute == null) return null;
        return String.format("%02d-%02d", attribute.getMonthValue(), attribute.getDayOfMonth());
    }

    @Override
    public MonthDay convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        String[] parts = dbData.split("-");
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        return MonthDay.of(month, day);
    }
}
