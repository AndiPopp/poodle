package de.andipopp.poodle.views.servlets;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class ServletRegister {

	@Bean("pngImageServlet")
	public ServletRegistrationBean<AvatarImageServlet> exampleServletBean() {
	    ServletRegistrationBean<AvatarImageServlet>  bean = new ServletRegistrationBean<>(new AvatarImageServlet(), "/"+AvatarImageServlet.SUB_FOLDER+"/*");
	    return bean;
	}
}
