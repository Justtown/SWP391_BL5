package com.example.argomachinemanagement.controller.request;

import com.example.argomachinemanagement.entity.Machine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.argomachinemanagement.dal.MachineDAO;
import com.example.argomachinemanagement.dal.MachineRentRequestDAO;
import com.example.argomachinemanagement.entity.MachineRentRequest;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/rent-request")
public class RentRequestController extends HttpServlet {

    private MachineRentRequestDAO dao = new MachineRentRequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Lấy danh sách máy ACTIVE từ MachineDAO
        MachineDAO machineDAO = new MachineDAO();
        List<Machine> machines = machineDAO.findByFilters("ACTIVE", null, null); // chỉ lấy ACTIVE
        req.setAttribute("machines", machines);

        req.getRequestDispatcher("/view/request/create-request.jsp")
                .forward(req, resp);
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Integer userId = (Integer) req.getSession().getAttribute("userId");

        MachineRentRequest r = MachineRentRequest.builder()
                .machineId(Integer.parseInt(req.getParameter("machineId")))
                .customerId(userId)
                .startDate(Date.valueOf(req.getParameter("startDate")))
                .endDate(Date.valueOf(req.getParameter("endDate")))
                .note(req.getParameter("note"))
                .status("PENDING")
                .build();

        dao.create(r);

        resp.sendRedirect(req.getContextPath() + "/home?request=sent");
    }
}

