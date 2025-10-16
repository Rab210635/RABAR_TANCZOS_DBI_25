package spengergasse.at.sj2425scherzerrabar.foundation;
import org.springframework.stereotype.Component;
import spengergasse.at.sj2425scherzerrabar.domain.ApiKey;
import java.security.SecureRandom;

@Component
public class ApiKeyFactory {
    private static final char[] ALPHABET = "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXQZ".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    public ApiKeyFactory(){}

    public ApiKey generate(int lenght){
        char[] result = new char[lenght];
        for(int i = 0; i < lenght; i++){
            char pick = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
            result[i] = pick;
        }
        return new ApiKey(new String(result));
    }
}