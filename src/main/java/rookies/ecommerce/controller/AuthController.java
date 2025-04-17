package rookies.ecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rookies.ecommerce.dto.request.auth.RefreshTokenRequest;
import rookies.ecommerce.dto.request.auth.SignInRequest;
import rookies.ecommerce.dto.request.user.CreateCustomerRequest;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.dto.response.auth.SignInResponse;
import rookies.ecommerce.service.auth.AuthService;
import rookies.ecommerce.service.user.UserService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    UserService userService;
    AuthService authService;

    @Operation(summary = "Sign up new account", description = "Create a new account by providing valid user details.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Sign up successfully",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Sign up successfully\"}")
                                    })),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input provided",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1002, \"status\": \"fail\", \"message\": \"Wrong email format\"}")
                                    })),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json"))
            })
    @PostMapping("/sign-up")
    public ResponseEntity<AppApiResponse<Void>> signUp(@RequestBody @Valid CreateCustomerRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(201)
                .body(AppApiResponse.<Void>builder()
                        .code(1000)
                        .status(ApiStatus.SUCCESS)
                        .message("Sign up successfully")
                        .build());
    }

    @Operation(summary = "Sign in", description = "Authenticate user and return access and refresh tokens.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sign In successful",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Sign In successful. Welcome back!\", \"data\": {\"access_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"refresh_token\": \"dGhpcyBpcyBhIHNhbXBsZSB0b2tlbg==\", \"user\": {\"id\": \"12345\", \"username\": \"john_doe\"}}}")
                                    })),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1001, \"status\": \"fail\", \"message\": \"Invalid credentials\"}")
                                    })),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json"))
            })
    @PostMapping("/sign-in")
    public ResponseEntity<AppApiResponse<SignInResponse>> login(@RequestBody @Valid SignInRequest request) {
        SignInResponse signinResponse = authService.authenticateUser(request);
        return ResponseEntity.ok(AppApiResponse.<SignInResponse>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Sign In successful. Welcome back!")
                .data(signinResponse)
                .build());
    }

    @Operation(summary = "Refresh Token", description = "Generate a new access token using a valid refresh token.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token refreshed successfully",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Refresh token successfully\", \"data\": {\"access_token\": \"new_access_token\", \"refresh_token\": \"same_refresh_token\"}}")
                                    })),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid refresh token",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1013, \"status\": \"fail\", \"message\": \"Invalid refresh token\"}")
                                    })),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json"))
            })
    @PostMapping("/refresh-token")
    public ResponseEntity<AppApiResponse<Object>> refreshToken(@RequestBody RefreshTokenRequest request) {
        Map<String, Object> response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(AppApiResponse.builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Refresh token successfully")
                .data(response)
                .build());
    }

    @Operation(summary = "Logout", description = "Invalidate the user's authentication token and refresh token.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout successfully",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1000, \"status\": \"success\", \"message\": \"Logout successfully\"}")
                                    })),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Token already invalid",
                            content =
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    value =
                                                            "{\"code\": 1015, \"status\": \"fail\", \"message\": \"Token has been disabled!\"}")
                                    })),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json"))
            })
    @PostMapping("/logout")
    public ResponseEntity<AppApiResponse<Void>> logout(@RequestHeader("Authorization") String token) {
        authService.logoutUser(token);
        return ResponseEntity.ok(AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Logout successfully")
                .build());
    }
}
