package com.hospital.proyectoHospital;


import com.hospital.proyectoHospital.repositories.UsuarioRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@SpringBootApplication
public class ProyectoHospitalApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
				.directory("./")
				.filename(".env")
				.ignoreIfMissing()
				.load();

		// Conversión de las variables a MapPropertySource para Spring
		MapPropertySource dotenvSource = new MapPropertySource("dotenv", dotenv.entries().stream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));

		// Inicialización del contexto de Spring
		SpringApplication app = new SpringApplication(ProyectoHospitalApplication.class);
		app.addInitializers(applicationContext -> {
			applicationContext.getEnvironment().getPropertySources().addLast(dotenvSource);
		});

		ConfigurableApplicationContext context = app.run(args);

		// Validación de un usuario administrativo
		validarUsuarioAdmin(context);

		// Ejemplo de encriptación
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println("Contraseña cifrada: " + encoder.encode("admin123"));
	}

	private static void validarUsuarioAdmin(ConfigurableApplicationContext context) {
		UsuarioRepository usuarioRepository = context.getBean(UsuarioRepository.class);
		usuarioRepository.findByUsername("admin")
				.ifPresentOrElse(
						user -> System.out.println("Usuario encontrado: " + user),
						() -> System.out.println("Usuario administrativo no encontrado")
				);
	}

}
