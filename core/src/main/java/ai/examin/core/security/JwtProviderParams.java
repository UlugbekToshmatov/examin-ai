package ai.examin.core.security;

public class JwtProviderParams {
    public static final String SECRET_KEY = "3C63CB44CD9BAD71AEF3B91EFC163WWLKFGPO25MTO36IOY232O3KRM23ROFI";
    public static final long ACCESS_TOKEN_EXPIRATION = 1_800_000;      // 1 800 000 millis = 30 minutes
    public static final long REFRESH_TOKEN_EXPIRATION = 432_000_000;   // 432 000 000 millis = 5 days
    public static final long ACCOUNT_VERIFICATION_TOKEN_EXPIRATION = 600_000;      // 600 000 millis = 10 minutes
    public static final long RESET_PASSWORD_TOKEN_EXPIRATION = 600_000;      // 600 000 millis = 10 minutes
}
