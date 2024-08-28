package com.ericsson.gic.tms.tvs.infrastructure.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         28/09/2016
 */
public class DateDeserializer extends JsonDeserializer<Date> {

    private String[] iso8601FormatStrings = {
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss"
    };

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException {

        if (parser.getCurrentTokenId() == JsonTokenId.ID_STRING) {
            String dateString = parser.getText();
            ParseException parseException = null;

            for (String formatString : iso8601FormatStrings) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
                    dateFormat.setTimeZone(UTC);
                    return dateFormat.parse(dateString);
                } catch (ParseException e) {
                    parseException = e;
                }
            }
            if (parseException != null) {
                Throwables.propagate(parseException);
            }
        } else if (parser.getCurrentTokenId() == JsonTokenId.ID_NUMBER_INT) {
            return new Date(parser.getLongValue());
        }
        return null;
    }
}
