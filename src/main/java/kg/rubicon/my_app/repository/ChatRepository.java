package kg.rubicon.my_app.repository;

import kg.rubicon.my_app.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Chat> findByIdAndUserId(Long chatId, Long userId);
}
