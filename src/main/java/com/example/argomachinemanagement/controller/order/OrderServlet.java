package com.example.argomachinemanagement.controller.order;

import com.example.argomachinemanagement.dal.OrderDAO;
import com.example.argomachinemanagement.entity.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "OrderServlet", urlPatterns = {"/orders"})
public class OrderServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.equals("list")) {
            List<Order> orders = orderDAO.findAll();
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/view/order/order-list.jsp")
                    .forward(request, response);
        } else if (action.equals("create")) {
            request.getRequestDispatcher("/view/order/order-create.jsp")
                    .forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/orders?action=list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "create";

        if (action.equals("create")) {
            String contractCode = request.getParameter("contractCode");
            String customerName = request.getParameter("customerName");
            String customerPhone = request.getParameter("customerPhone");
            String customerAddress = request.getParameter("customerAddress");
            String machineIdRaw = request.getParameter("machineId");
            String serviceDescription = request.getParameter("serviceDescription");
            String startDateRaw = request.getParameter("startDate");
            String endDateRaw = request.getParameter("endDate");
            String status = request.getParameter("status");
            String totalCostRaw = request.getParameter("totalCost");

            Order o = new Order();
            o.setContractCode(contractCode);
            o.setCustomerName(customerName);
            o.setCustomerPhone(customerPhone);
            o.setCustomerAddress(customerAddress);

            if (machineIdRaw != null && !machineIdRaw.isBlank()) {
                o.setMachineId(Integer.parseInt(machineIdRaw));
            }
            o.setServiceDescription(serviceDescription);

            if (startDateRaw != null && !startDateRaw.isBlank()) {
                o.setStartDate(Date.valueOf(startDateRaw));
            }
            if (endDateRaw != null && !endDateRaw.isBlank()) {
                o.setEndDate(Date.valueOf(endDateRaw));
            }

            if (status == null || status.isBlank()) {
                status = "PENDING";
            }
            o.setStatus(status);

            if (totalCostRaw != null && !totalCostRaw.isBlank()) {
                o.setTotalCost(Double.parseDouble(totalCostRaw));
            }

            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                o.setCreatedBy((Integer) session.getAttribute("userId"));
            }

            orderDAO.create(o);
            response.sendRedirect(request.getContextPath() + "/orders?action=list");
        }
    }
}
