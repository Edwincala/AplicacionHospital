package com.hospital.proyectoHospital;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.Properties;

@SpringBootApplication
public class ProyectoHospitalApplication {

	public static void main(String[] args) {
		try {
			// Configuración más específica para cargar .env
			Dotenv dotenv = Dotenv.configure()
					.directory("./") // Especifica el directorio raíz
					.filename(".env") // Especifica el nombre del archivo
					.ignoreIfMissing() // No falla si no encuentra el archivo
					.load();

			// Imprime las variables para debug
			System.out.println("Cargando variables de entorno...");
			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
				System.out.println("Cargada variable: " + entry.getKey());
			});

			SpringApplication.run(ProyectoHospitalApplication.class, args);
		} catch (Exception e) {
			System.err.println("Error al iniciar la aplicación: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
