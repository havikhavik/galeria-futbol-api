package com.galeriafutbol.api.config;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.galeriafutbol.api.model.User;
import com.galeriafutbol.api.repository.UserRepository;

@Component
public class AuditorAwareImpl implements AuditorAware<User> {

    private static final Logger logger = LoggerFactory.getLogger(AuditorAwareImpl.class);

    private final UserRepository userRepository;

    public AuditorAwareImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            logger.debug("No authentication found");
            return Optional.empty();
        }
        String email = auth.getName();
        logger.debug("Resolving auditor for email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            logger.warn("Auditor user not found for email: {}", email);
        }
        return user;
    }
}
