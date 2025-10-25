package com.learn.kidstinyworld.service;

import com.learn.kidstinyworld.dto.ChildCreationRequest;
import com.learn.kidstinyworld.entity.Child;
import com.learn.kidstinyworld.entity.Parent;
import com.learn.kidstinyworld.exception.InvalidInputException;
import com.learn.kidstinyworld.exception.ResourceNotFoundException;
import com.learn.kidstinyworld.repository.ChildRepository;
import com.learn.kidstinyworld.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ParentRepository parentRepository;

    // -----------------------------------------------------------
    // Köməkçi Metod: Daxil Olmuş Valideyni Tapmaq
    // -----------------------------------------------------------
    private Parent getCurrentParent() {
        // Security Context-den cari istifadəçinin (Valideynin) adını alırıq
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Repozitori vasitəsilə Parent obyektini tapırıq
        return parentRepository.findByUsername(username);
    }

    // -----------------------------------------------------------
    // 1. Yeni Uşaq Yaratmaq
    // -----------------------------------------------------------
    public Child createChild(ChildCreationRequest request) {
        Parent currentParent = getCurrentParent();

        if (request.getAge() < 3) {
            throw new InvalidInputException("KidsTinyWorld platforması üçün uşağın minimum yaşı 3 olmalıdır.");
        }

        Child newChild = Child.builder()
                .name(request.getName())
                .age(request.getAge())
                .avatarUrl(request.getAvatarUrl())
                .parent(currentParent) // Cari valideynə bağlayırıq
                .totalPoints(0)
                .flameStreak(0)
                .build();

        return childRepository.save(newChild);
    }

    // -----------------------------------------------------------
    // 2. Valideynə Aid Bütün Uşaqları Gətirmək
    // -----------------------------------------------------------
    public List<Child> getAllChildrenForParent() {
        Parent currentParent = getCurrentParent();
        // Repository-dəki findAllByParentId metodundan istifadə
        return childRepository.findAllByParentId(currentParent.getId());
    }

    // -----------------------------------------------------------
    // 3. Uşaq Məlumatlarını Yeniləmək
    // -----------------------------------------------------------
    public Child updateChild(Long childId, ChildCreationRequest request) {
        Parent currentParent = getCurrentParent();

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Uşaq", "id", childId));

        // Təhlükəsizlik Yoxlaması: Uşağın cari valideynə aid olmasını təmin edir
        if (!child.getParent().getId().equals(currentParent.getId())) {
            throw new InvalidInputException("Bu uşaq profili sizin idarəetməniz altında deyil.");
        }

        // Məlumatları yenilə
        child.setName(request.getName());
        child.setAge(request.getAge());
        child.setAvatarUrl(request.getAvatarUrl());

        return childRepository.save(child);
    }

    // -----------------------------------------------------------
    // 4. Uşaq Profilini Silmək
    // -----------------------------------------------------------
    public void deleteChild(Long childId) {
        Parent currentParent = getCurrentParent();

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Uşaq", "id", childId));

        if (!child.getParent().getId().equals(currentParent.getId())) {
            throw new InvalidInputException("Silmə əməliyyatı üçün icazəniz yoxdur.");
        }

        childRepository.delete(child);
    }

    // -----------------------------------------------------------
    // 5. Köməkçi Metod: Uşağın Məlumatını Alıb, Valideyni Yoxlamaq (Digər servislər üçün)
    // -----------------------------------------------------------
    public Child getChildAndVerifyParent(Long childId) {
        Parent currentParent = getCurrentParent();

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Uşaq", "id", childId));

        if (!child.getParent().getId().equals(currentParent.getId())) {
            // 403 Forbidden yerinə 400 Bad Request atırıq
            throw new InvalidInputException("Seçilən uşaq cari valideynə aid deyil.");
        }
        return child;
    }
}