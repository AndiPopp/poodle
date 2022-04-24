package de.andipopp.poodle.views.servlets;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.views.components.PoodleAvatar;

@WebServlet(urlPatterns = "/" + AvatarImageServlet.SUB_FOLDER, name = "DynamicContentServlet")
public class AvatarImageServlet extends HttpServlet {

	public static final String SUB_FOLDER = "avatar";
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("image/png");
        resp.setStatus(404);
        
        String name = req.getParameter("name");
        String type = req.getParameter("type");
        if (name == null || type == null) return;
        
        String fileName = "";
        
        switch (PoodleAvatar.Type.valueOf(type.toUpperCase())) {
        case USER:
        	fileName = Config.getCurrent().getUserImagePath();
        	break;
        case POLL:
        	fileName = Config.getCurrent().getPollImagePath();
        	break;
        default:	
        	return;
        }
        fileName += System.getProperty("file.separator") + name;
        
        try {
			FileInputStream in =  new FileInputStream(fileName);
			IOUtils.copy(in, resp.getOutputStream());	
			in.close();
		} catch (IOException e) {
			return;
		}
        
        resp.setStatus(200);
        
        

    }
}