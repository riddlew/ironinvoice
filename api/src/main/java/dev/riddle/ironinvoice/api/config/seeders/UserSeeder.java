package dev.riddle.ironinvoice.api.config.seeders;

import dev.riddle.ironinvoice.api.features.users.persistence.UserEntity;
import dev.riddle.ironinvoice.api.features.users.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
@Profile("!production")
@RequiredArgsConstructor
public class UserSeeder implements ApplicationRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (userRepository.count() > 0) return;

		System.out.println("Seeding users...");

		UserEntity testUser = new UserEntity();
		testUser.setDisplayName("Test User");
		testUser.setEmail("test@example.com");
		testUser.setEnabled(true);
		testUser.setPasswordHash(passwordEncoder.encode("test"));
		testUser.setRoles(List.of("ROLE_USER"));
		userRepository.save(testUser);
	}
}
