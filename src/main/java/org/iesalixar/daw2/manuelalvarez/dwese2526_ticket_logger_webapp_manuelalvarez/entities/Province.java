package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "provinces")
public class Province {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "code", nullable = false, length = 10)
    private String code;


    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "{msg_province.region.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

}

