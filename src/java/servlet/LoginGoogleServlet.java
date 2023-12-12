/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import data.dao.UserDAO;
import data.dto.UserDTO;
import java.io.IOException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import logingoogle.GoogleUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

@WebServlet(name = "LoginGoogleServlet", urlPatterns = {"/LoginGoogleServlet"})
public class LoginGoogleServlet extends HttpServlet {

    private final Logger log = Logger.getLogger(this.getClass());
    private static final String ERROR = "login.jsp";
    private static final String SUCCESS = "HomeController";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = ERROR;
        try {
            URL urlLof4j = this.getClass().getResource("/log4j/Log4j.properties");
            PropertyConfigurator.configure(urlLof4j);
            HttpSession session = request.getSession();
            String code = request.getParameter("code");
            if (code == null || code.isEmpty()) {
                request.setAttribute("ERROR", "Incorrect Email or Password.");
                url = ERROR;
            } else {
                String accessToken = GoogleUtils.getToken(code);
                UserDTO userGoogle = GoogleUtils.getUserInfo(accessToken);
                UserDTO loginUser = new UserDAO().getUserByEmail(userGoogle.getEmail());
                if (loginUser == null) {
                    String email = userGoogle.getEmail();
                    int index = email.lastIndexOf("@");
                    String name = email.substring(0, index);
                    new UserDAO().createUserByGoogle(userGoogle.getEmail(), name);
                    loginUser = new UserDAO().getUserByEmail(userGoogle.getEmail());
                }
                session.setAttribute("LOGIN_USER", loginUser);
                url = SUCCESS;
            }

        } catch (Exception e) {
            log("Error at LoginGoogleServlet" + e.toString());
            log.info(e.getMessage(), e);
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }

    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
