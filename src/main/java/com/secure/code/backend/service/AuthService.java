package com.secure.code.backend.service;

import com.secure.code.backend.dto.AuthenticationRequest;
import com.secure.code.backend.dto.AuthenticationResponse;
import com.secure.code.backend.dto.RegisterRequest;
import com.secure.code.backend.model.Role;
import com.secure.code.backend.model.User;
import com.secure.code.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        public AuthenticationResponse register(RegisterRequest request) {
                var user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.USER)
                                .build();
                userRepository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));
                User user = userRepository.findByUsernameIgnoreCase(request.getUsername())
                                .orElseThrow();
                // Null check not strictly needed due to orElseThrow, but satisfying linter if
                // needed or just ignoring
                if (user == null)
                        throw new RuntimeException("User not found");
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }
}
