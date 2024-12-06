package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.models.Cita;
import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.HorarioDoctor;
import com.hospital.proyectoHospital.repositories.CitaRepository;
import com.hospital.proyectoHospital.repositories.HorarioDoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HorarioDoctorService {

    @Autowired
    private HorarioDoctorRepository horarioDoctorRepository;

    @Autowired
    private CitaRepository citaRepository;

    public List<HorarioDoctor> obtenerHorariosPorDoctor(Doctor doctor) {
        return horarioDoctorRepository.findByDoctorAndDisponibleTrue(doctor);
    }

    public HorarioDoctor crearHorario(Doctor doctor, HorarioDoctor horarioDoctor, LocalDateTime inicio, LocalDateTime fin) {
        validarHorario(inicio, fin);

        HorarioDoctor horario = new HorarioDoctor();
        horario.setDoctor(doctor);
        horario.setInicio(inicio);
        horario.setFin(fin);
        horario.setDisponible(true);

        return horarioDoctorRepository.save(horario);
    }

    public boolean eliminarHorario(UUID horarioId) {
        Optional<HorarioDoctor> horarioOpt = horarioDoctorRepository.findById(horarioId);
        if (horarioOpt.isPresent()) {
            List<Cita> citasEnHorario = citaRepository.findByDoctorAndFechaHoraBetween(
                    horarioOpt.get().getDoctor(),
                    horarioOpt.get().getInicio(),
                    horarioOpt.get().getFin()
            );

            if (citasEnHorario.isEmpty()) {
                horarioDoctorRepository.deleteById(horarioId);
                return true;
            }
        }
        return false;
    }

    public List<HorarioDoctor> obtenerHorariosDisponibles(Doctor doctor, LocalDateTime fecha) {
        LocalDateTime inicioDia = fecha.toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        List<HorarioDoctor> horarios = horarioDoctorRepository.findByDoctorAndDisponibleTrue(doctor);

        return horarios.stream()
                .filter(h -> !h.getInicio().isBefore(inicioDia) && !h.getFin().isAfter(finDia))
                .collect(Collectors.toList());
    }

    private void validarHorario(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de inicio debe ser futura");
        }
    }
}
