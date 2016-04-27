package com.toast.votree.serializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.toast.votree.util.DbgUtil;

public class CustomDateDeserializer extends JsonDeserializer<Date>{
  @Override
  public Date deserialize(JsonParser jsonparser,
          DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
      String date = jsonparser.getText();
      try {
        // Timestamp인 경우
        if(jsonparser.getValueAsLong() > 0){
          Date timestampDate = new Date(Long.parseLong(date));
          return format.parse(format.format(timestampDate).toString());
        }
        return format.parse(date);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return null;
  }
}
