package com.coffeeshop.config;

import com.coffeeshop.entity.Role;
import com.coffeeshop.entity.User;
import com.coffeeshop.repository.RoleRepository;
import com.coffeeshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(Role.builder()
                .name("ROLE_ADMIN")
                .description("Quản trị viên / Chủ quán")
                .build()));

        Role staffRole = roleRepository.findByName("ROLE_STAFF").orElseGet(() -> roleRepository.save(Role.builder()
                .name("ROLE_STAFF")
                .description("Nhân viên thu ngân / Bán hàng")
                .build()));

        userRepository.findByUsername("admin").ifPresentOrElse(admin -> {
            admin.setFullName("Quản trị viên");
            admin.setRole(adminRole);
            admin.setIsActive(true);
            userRepository.save(admin);
        }, () -> {
            userRepository.save(User.builder()
                    .username("admin")
                    .passwordHash("123456")
                    .fullName("Quản trị viên")
                    .phone("0901234567")
                    .email("admin@coffeeshop.com")
                    .role(adminRole)
                    .isActive(true)
                    .build());
        });

        userRepository.findByUsername("staff").ifPresentOrElse(staff -> {
            staff.setFullName("Nhân viên thu ngân");
            staff.setRole(staffRole);
            staff.setIsActive(true);
            userRepository.save(staff);
        }, () -> {
            userRepository.save(User.builder()
                    .username("staff")
                    .passwordHash("123456")
                    .fullName("Nhân viên thu ngân")
                    .phone("0908765432")
                    .email("staff@coffeeshop.com")
                    .role(staffRole)
                    .isActive(true)
                    .build());
        });

    }
}
