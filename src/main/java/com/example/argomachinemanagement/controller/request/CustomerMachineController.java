package com.example.argomachinemanagement.controller.request;

import com.example.argomachinemanagement.dal.MachineDAO;
import com.example.argomachinemanagement.entity.Machine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.argomachinemanagement.dal.RequestDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/customer/machines")
public class CustomerMachineController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        MachineDAO dao = new MachineDAO();

        if ("detail".equals(action)) {
            String idRaw = request.getParameter("id");
            if (idRaw != null) {
                int id = Integer.parseInt(idRaw);
                Machine machine = dao.findDetailForCustomer(id);

                if (machine != null) {
                    request.setAttribute("machine", machine);
                    request.getRequestDispatcher(
                            "/view/customer/customer-machine-detail.jsp"
                    ).forward(request, response);
                    return;
                }
            }
            response.sendRedirect(request.getContextPath() + "/customer/machines");
            return;
        }

        // ================== LIST (DEFAULT) ==================
        List<Machine> machines = dao.findAllForCustomer();
        request.setAttribute("machines", machines);
        request.getRequestDispatcher(
                "/view/customer/customer-machine-list.jsp"
        ).forward(request, response);
    }
}


