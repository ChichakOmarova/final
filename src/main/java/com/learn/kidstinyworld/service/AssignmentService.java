package com.learn.kidstinyworld.service;

import com.learn.kidstinyworld.dto.AssignmentRequest;
import com.learn.kidstinyworld.dto.TaskCompletedEvent;
import com.learn.kidstinyworld.entity.Activity;
import com.learn.kidstinyworld.entity.Assignment;
import com.learn.kidstinyworld.entity.Child;
import com.learn.kidstinyworld.enums.Status;
import com.learn.kidstinyworld.exception.ResourceNotFoundException;
import com.learn.kidstinyworld.kafka.TaskProducer;
import com.learn.kidstinyworld.repository.ActivityRepository;
import com.learn.kidstinyworld.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final ChildService childService; // ChildService-den usaq melumatini alir
    private final ActivityRepository activityRepository;
    private final TaskProducer taskProducer; // Kafka Producer

    // -----------------------------------------------------------
    // 1. assignTask: Tapşırığın Təyin Edilməsi (ActivityController çağırır)
    // -----------------------------------------------------------
    public Assignment assignTask(AssignmentRequest request) {

        // 1. Uşağın valideynə aid olmasını yoxla (ChildService-in komekcisi ile)
        Child child = childService.getChildAndVerifyParent(request.getChildId());

        // 2. Activity-i tap
        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("Fəaliyyət", "id", request.getActivityId()));

        // 3. Yeni Assignment yarat
        Assignment newAssignment = Assignment.builder()
                .child(child)
                .activity(activity)
                .assignedDate(LocalDate.now())
                .status(Status.PENDING)
                .pointsAwarded(0) // Başlanğıcda 0 bal
                .build();

        return assignmentRepository.save(newAssignment);
    }

    // -----------------------------------------------------------
    // 2. completeTask: Tapşırığın Tamamlanması (ActivityController çağırır)
    // -----------------------------------------------------------
    public void completeTask(Long assignmentId) {
        // 1. Assignment-i tap (ve valideynin icazesini yoxla)
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Tapşırıq Təyinatı", "id", assignmentId);
        }

        Assignment assignment = assignmentOpt.get();

        // Təhlükəsizlik: Uşağın cari valideynə aid olmasını yoxlayır
        // getChildAndVerifyParent metodu icazeni yoxlamali idi.
        // ChildService-de icaze yoxlanilmasi Child-in id-si ile gedir. Burada yoxlanisi sade edirik:
        Child child = childService.getChildAndVerifyParent(assignment.getChild().getId());

        // 2. Tapşırığı tamamlandı kimi qeyd et (DB Update)
        assignment.setStatus(Status.COMPLETED);
        assignment.setCompletionTime(LocalTime.now());
        assignment.setPointsAwarded(assignment.getActivity().getPointsValue()); // Əvvəlcədən təyin olunmuş balı ver

        assignmentRepository.save(assignment);

        // 3. KAFKA: TaskCompletedEvent hadisesini gonder (Asinxron islemek ucun)
        TaskCompletedEvent event = new TaskCompletedEvent();
        event.setAssignmentId(assignment.getId());
        event.setChildId(child.getId());
        event.setParentId(child.getParent().getId());
        event.setPointsAwarded(assignment.getPointsAwarded());
        event.setCompletionTime(assignment.getCompletionTime());

        taskProducer.sendCompletedEvent(event);

        // Controller-e cavab tez qaytarilir.
    }
}