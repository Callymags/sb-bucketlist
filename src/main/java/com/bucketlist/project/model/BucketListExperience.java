package com.bucketlist.project.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bucketlist_experiences")
public class BucketListExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "experience_id")
    private Experience experience;

    @Column(nullable = false)
    private LocalDate dateSaved;

    @Column(nullable = false)
    private boolean completed;
}
