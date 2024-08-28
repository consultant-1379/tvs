package com.ericsson.gic.tms.tvs.infrastructure.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         28/09/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class DateDeserializerTest {

    private DateDeserializer deserializer = new DateDeserializer();

    @Mock
    private JsonParser jsonParser;

    private static final String DATE_TIME = "2016-09-28T07:15:03";
    private static final String DATE_TIME_MS_TZ = "2016-09-28T09:15:03.789" + "+02:00";
    private static final String DATE_TIME_Z = DATE_TIME + "Z";
    private static final String DATE_TIME_MS_Z = DATE_TIME + ".789Z";
    private static final String INVALID_DATE_TIME = "invalid";
    private static final long UNIX_TIMESTAMP = 1475046903789L;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private static final String EXPECTED = "2016-09-28T07:15:03.000";
    private static final String EXPECTED_WITH_MILLIS = "2016-09-28T07:15:03.789";


    @Before
    public void setUp() throws Exception {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void testDeserialize() throws Exception {

        verifyWithMillis(DATE_TIME_MS_Z);
        verifyWithMillis(DATE_TIME_MS_TZ);
        verify(DATE_TIME_Z);
        verify(DATE_TIME);

    }

    private void verify(String dateString) throws IOException {
        Date date = deserialize(dateString);
        assertThat(DATE_FORMAT.format(date)).isEqualTo(EXPECTED);
    }

    private void verifyWithMillis(String dateString) throws IOException {
        Date date = deserialize(dateString);
        assertThat(DATE_FORMAT.format(date)).isEqualTo(EXPECTED_WITH_MILLIS);
    }

    private Date deserialize(String dateString) throws IOException {
        Mockito.when(jsonParser.getText()).thenReturn(dateString);
        Mockito.when(jsonParser.getCurrentTokenId()).thenReturn(JsonTokenId.ID_STRING);
        return deserializer.deserialize(jsonParser, null);
    }

    @Test
    public void testDeserializeNumeric() throws Exception {
        Mockito.when(jsonParser.getCurrentTokenId()).thenReturn(JsonTokenId.ID_NUMBER_INT);
        Mockito.when(jsonParser.getLongValue()).thenReturn(UNIX_TIMESTAMP);

        Date date = deserializer.deserialize(jsonParser, null);

        assertThat(DATE_FORMAT.format(date)).isEqualTo(EXPECTED_WITH_MILLIS);
    }

    @Test
    public void testDeserializeInvalidDate() throws Exception {
        Throwable thrown = catchThrowable(() -> deserialize(INVALID_DATE_TIME));

        assertThat(thrown).isInstanceOf(RuntimeException.class)
            .hasRootCauseInstanceOf(ParseException.class);
    }
}
