package de.andipopp.poodle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

/**
 * The entry point of the Spring Boot application.
 * 
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "poodle")
@PWA(name = "Poodle", shortName = "Poodle", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class PoodleApplication extends SpringBootServletInitializer implements AppShellConfigurator {

	/**
	 * The serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * App name
	 */
	public static final String name = "Poodle";
	
	/**
	 * App version
	 */
	public static final String version = "0.0";

	/**
	 * App name and version as string
	 * @return app name and version
	 */
	public static final String nameAndVersion() {
		return name + " v" + version;
	}
	
    public static void main(String[] args) {
    	SpringApplication.run(PoodleApplication.class, args);
    }

}
