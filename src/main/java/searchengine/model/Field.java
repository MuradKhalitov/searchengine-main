package searchengine.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "field")
@Data
@NoArgsConstructor
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String selector;
    @NotNull
    float weight;
}
