package umc.animore.repository;

import org.springframework.data.repository.CrudRepository;
import umc.animore.redis.RefreshToken;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, Long> {

    RefreshToken findByRefreshToken(String refreshToken);
}
