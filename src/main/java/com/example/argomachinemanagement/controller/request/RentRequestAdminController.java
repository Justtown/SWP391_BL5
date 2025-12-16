package com.example.argomachinemanagement.controller.request;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.argomachinemanagement.dal.MachineRentRequestDAO;
import java.io.IOException;

@WebServlet("/admin/rent-requests")
public class RentRequestAdminController extends HttpServlet {

    private MachineRentRequestDAO dao = new MachineRentRequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("requests", dao.findAll());
        req.getRequestDispatcher("/view/request/list-request.jsp")
                .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int id = Integer.parseInt(req.getParameter("id"));
        String action = req.getParameter("action");
        int reviewerId = (Integer) req.getSession().getAttribute("userId");

        dao.updateStatus(
                id,
                action.equals("approve") ? "APPROVED" : "REJECTED",
                reviewerId
        );

        resp.sendRedirect(req.getContextPath() + "/admin/rent-requests");
    }
}

