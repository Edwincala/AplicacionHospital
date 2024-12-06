package com.hospital.proyectoHospital.services;

import com.hospital.proyectoHospital.exceptions.UsuarioNotFoundException;
import com.hospital.proyectoHospital.models.Doctor;
import com.hospital.proyectoHospital.models.Paciente;
import com.hospital.proyectoHospital.models.PerfilDto;
import com.hospital.proyectoHospital.models.Usuario;
import com.hospital.proyectoHospital.repositories.DoctorRepository;
import com.hospital.proyectoHospital.repositories.UsuarioRepository;
import com.hospital.proyectoHospital.repositories.PacientesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PacientesRepository pacienteRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, PacientesRepository pacienteRepository, DoctorRepository doctorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.pacienteRepository = pacienteRepository;
        this.doctorRepository = doctorRepository;
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario saveUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findById(UUID id) {
        return usuarioRepository.findById(id);
    }

    public boolean deleteUsuario(UUID id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByUsername(String username) {
        return usuarioRepository.findByUsername(username).isPresent();
    }

    public boolean existsById(UUID id) {
        return usuarioRepository.existsById(id);
    }

    public void updateUserProfile(PerfilDto perfilDto, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));

        usuario.setNombre(perfilDto.getNombre());
        usuario.setApellido(perfilDto.getApellido());

        if (perfilDto.getPassword() != null && !perfilDto.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(perfilDto.getPassword()));
        }

        if (usuario instanceof Paciente paciente) {
            paciente.setTelefono(perfilDto.getTelefono());
            paciente.setDireccion(perfilDto.getDireccion());
        }

        if (usuario instanceof Doctor doctor) {
            doctor.setEspecialidad(perfilDto.getEspecialidad());
            doctor.setHorarios(perfilDto.getHorarios());
        }

        usuarioRepository.save(usuario);
    }

    public Usuario createUserFromPerfilDto(PerfilDto perfilDto) {
        Usuario usuario;

        switch (perfilDto.getRole()) {
            case PACIENTE:
                usuario = new Paciente();
                ((Paciente) usuario).setTelefono(perfilDto.getTelefono());
                ((Paciente) usuario).setDireccion(perfilDto.getDireccion());
                break;

            case DOCTOR:
                usuario = new Doctor();
                ((Doctor) usuario).setEspecialidad(perfilDto.getEspecialidad());
                ((Doctor) usuario).setHorarios(perfilDto.getHorarios());
                break;

            default:
                usuario = new Usuario();
                break;
        }

        usuario.setNombre(perfilDto.getNombre());
        usuario.setApellido(perfilDto.getApellido());
        usuario.setUsername(perfilDto.getUsername());

        String generatedPassword = generatePassword(perfilDto.getNombre());
        usuario.setPassword(passwordEncoder.encode(generatedPassword));

        // Guardar usuario
        return usuarioRepository.save(usuario);
    }

    private String generatePassword(String base) {

        String password = base + "123@Aa";
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        if (!hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecialChar) {
            throw new IllegalArgumentException("La contraseña generada no cumple los requisitos de seguridad.");
        }

        return password;
    }

    public PerfilDto getUserProfile(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
        return convertToPerfilDto(usuario);
    }

    private PerfilDto convertToPerfilDto(Usuario usuario) {
        PerfilDto perfil = new PerfilDto();
        String username = usuario.getUsername();
        perfil.setNombre(usuario.getNombre());
        perfil.setApellido(usuario.getApellido());
        if (usuario.getRol().equals(Usuario.Rol.PACIENTE)) {
            Paciente paciente = pacienteRepository.findByUsername(username)
                    .orElseThrow(() -> new UsuarioNotFoundException("Paciente no encontrado"));
            perfil.setTelefono(paciente.getTelefono());
            perfil.setDireccion(paciente.getDireccion());
        } else if (usuario.getRol().equals(Usuario.Rol.DOCTOR)) {
            Doctor doctor = (Doctor) doctorRepository.findByUsername(usuario)
                    .orElseThrow(() -> new UsuarioNotFoundException("Doctor no encontrado"));
            perfil.setEspecialidad(doctor.getEspecialidad());
            perfil.setHorarios(doctor.getHorarios());
        }
        return perfil;
    }
}
