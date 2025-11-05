package devdojo.logic.database.task.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.*;

@Converter
public final class JsonStringConverter implements AttributeConverter<Object, String> {

  private final ObjectMapper objectMapper;

  public JsonStringConverter() {
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public String convertToDatabaseColumn(final Object attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("JSON write error", e);
    }
  }

  @Override
  public Object convertToEntityAttribute(final String dbData) {
    try {
      return objectMapper.readValue(dbData, Object.class);
    } catch (IOException e) {
      throw new IllegalArgumentException("JSON read error", e);
    }
  }
}


