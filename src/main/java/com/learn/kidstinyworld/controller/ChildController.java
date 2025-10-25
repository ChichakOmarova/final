package com.learn.kidstinyworld.controller;

import com.learn.kidstinyworld.dto.ChildCreationRequest;
import com.learn.kidstinyworld.entity.Child;
import com.learn.kidstinyworld.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/children")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    // 1. Yeni Uşaq Profili Yaratmaq (Valideynin Özü üçün)
    // POST /api/children
    @PostMapping
    public ResponseEntity<Child> createChild(@Valid @RequestBody ChildCreationRequest request) {
        // Avtorizasiya Security Filter terefinden hell olunub, ancaq burada rol yoxlamasi lazim deyil.
        Child newChild = childService.createChild(request);
        return new ResponseEntity<>(newChild, HttpStatus.CREATED);
    }

    // 2. Valideynə Aid Bütün Uşaqların Siyahısını Gətirmək (Read All)
    // GET /api/children
    @GetMapping
    public ResponseEntity<List<Child>> getAllChildren() {
        List<Child> children = childService.getAllChildrenForParent();
        return ResponseEntity.ok(children);
    }

    // 3. Uşaq Profilini Yeniləmək (Update)
    // PUT /api/children/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Child> updateChild(
            @PathVariable Long id,
            @Valid @RequestBody ChildCreationRequest request) {

        Child updatedChild = childService.updateChild(id, request);
        return ResponseEntity.ok(updatedChild);
    }

    // 4. Uşaq Profilini Silmək (Delete)
    // DELETE /api/children/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChild(@PathVariable Long id) {
        childService.deleteChild(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}