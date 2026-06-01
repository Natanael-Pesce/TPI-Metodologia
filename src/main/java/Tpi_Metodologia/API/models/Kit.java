package Tpi_Metodologia.API.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Kit")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Kit extends Producto {

    @ManyToMany
    @JoinTable(
        name = "kit_producto",
        joinColumns = @JoinColumn(name = "productoID"),
        inverseJoinColumns = @JoinColumn(name = "componenteID")
    )
    private List<Producto> productos;
}