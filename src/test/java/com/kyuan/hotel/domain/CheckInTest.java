package com.kyuan.hotel.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.kyuan.hotel.web.rest.TestUtil;

public class CheckInTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CheckIn.class);
        CheckIn checkIn1 = new CheckIn();
        checkIn1.setId(1L);
        CheckIn checkIn2 = new CheckIn();
        checkIn2.setId(checkIn1.getId());
        assertThat(checkIn1).isEqualTo(checkIn2);
        checkIn2.setId(2L);
        assertThat(checkIn1).isNotEqualTo(checkIn2);
        checkIn1.setId(null);
        assertThat(checkIn1).isNotEqualTo(checkIn2);
    }
}
