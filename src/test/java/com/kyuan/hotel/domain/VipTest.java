package com.kyuan.hotel.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.kyuan.hotel.web.rest.TestUtil;

public class VipTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vip.class);
        Vip vip1 = new Vip();
        vip1.setId(1L);
        Vip vip2 = new Vip();
        vip2.setId(vip1.getId());
        assertThat(vip1).isEqualTo(vip2);
        vip2.setId(2L);
        assertThat(vip1).isNotEqualTo(vip2);
        vip1.setId(null);
        assertThat(vip1).isNotEqualTo(vip2);
    }
}
