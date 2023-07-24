package umc.animore.model.reservation;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceTime {

    @Id
    private int serviceTimeId;

    @ManyToOne
    @JoinColumn(name = "serviceId")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "timeSlot")
    private TimeSlot timeSlot;
}
