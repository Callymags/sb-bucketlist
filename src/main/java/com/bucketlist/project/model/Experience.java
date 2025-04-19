package com.bucketlist.project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "experiences")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "experience_seq")
    @SequenceGenerator(name = "experience_seq", sequenceName = "experience_seq", allocationSize = 1)
    private Long experienceId;

    @NotBlank
    @Size(min = 3, message = "Experience name must contain at least three characters")
    private String experienceName;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId")
    private Category category;

    private String experienceImage;

    @NotBlank
    @Size(min = 3, message = "Experience description must contain at least three characters")
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "userId")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "last_modified_by", referencedColumnName = "userId")
    private User lastModifiedBy;

    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BucketListExperience> savedByUsers;

}
