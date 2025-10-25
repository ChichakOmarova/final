package com.learn.kidstinyworld.service;

import com.learn.kidstinyworld.dto.TaskCompletedEvent;
import com.learn.kidstinyworld.entity.Parent;
import com.learn.kidstinyworld.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender; // SILINDI
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    // private final JavaMailSender mailSender; // SILINDI
    private final ParentRepository parentRepository;

    // StatsConsumer terefinden cagirilir
    public void sendCompletionNotification(Long parentId, TaskCompletedEvent event) {

        // ParentRepository-de parent-i tapmaq ve email-ini goturmek ucun
        Parent parent = parentRepository.findById(parentId).orElse(null);
        if (parent == null) {
            System.err.println("Email gonderilmedi: Valideyn tapilmadi. ID: " + parentId);
            return;
        }

        // Simulyasiya, çünki mail konfiqurasiyası yoxdur.
        System.out.printf("-> EMAIL SERVICE: Bildiris gonderildi. Valideyn: %s (Simulyasiya)\n",
                parent.getUsername());
    }
}