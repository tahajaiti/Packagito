package com.kyojin.packagito;

import com.kyojin.packagito.repository.ParcelRepository;
import com.kyojin.packagito.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("ci")
class PackagitoApplicationTests {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ParcelRepository parcelRepository;

	@Test
	void contextLoads() {
	}

}
