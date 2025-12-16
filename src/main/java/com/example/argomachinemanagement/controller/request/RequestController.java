package com.example.argomachinemanagement.controller.request;

import com.example.argomachinemanagement.dal.RequestDAO;
import com.example.argomachinemanagement.entity.Request;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/requests")
public class RequestController extends HttpServlet {

    private final RequestDAO dao = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect("login");
            return;
        }

        String role = (String) session.getAttribute("roleName");
        Integer userId = (Integer) session.getAttribute("userId");
        String action = req.getParameter("action");

        /* ================= CREATE FORM ================= */
        if ("create".equals(action)) {
            if (!"customer".equals(role)) {
                resp.sendError(403);
                return;
            }
            req.getRequestDispatcher("/view/request/request-create.jsp").forward(req, resp);
            return;
        }

        /* ================= SALE EDIT ================= */
        if ("edit".equals(action)) {
            if (!"sale".equals(role)) {
                resp.sendError(403);
                return;
            }

            int id = Integer.parseInt(req.getParameter("id"));
            Request r = dao.getById(id);

            if (r == null) {
                resp.sendError(404);
                return;
            }

            req.setAttribute("request", r);
            req.getRequestDispatcher("/view/request/request-detail.jsp").forward(req, resp);
            return;
        }

        /* ================= LIST ================= */
        List<Request> requests;

        if ("sale".equals(role)) {
            requests = dao.getAll();
        } else if ("customer".equals(role)) {
            requests = dao.getByCustomerId(userId);
        } else {
            resp.sendError(403);
            return;
        }

        req.setAttribute("requests", requests);
        req.getRequestDispatcher("/view/request/request-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();
        String action = req.getParameter("action");
        String role = (String) session.getAttribute("roleName");

        if ("create".equals(action) && "customer".equals(role)) {
            create(req, session);
        }

        if ("review".equals(action) && "sale".equals(role)) {
            review(req, session);
        }

        resp.sendRedirect("requests");
    }

    private void create(HttpServletRequest req, HttpSession session) {
        Request r = new Request();
        r.setTitle(req.getParameter("title"));
        r.setDescription(req.getParameter("description"));
        r.setCustomerId((int) session.getAttribute("userId"));

        dao.create(r);
    }

    private void review(HttpServletRequest req, HttpSession session) {
        dao.review(
                Integer.parseInt(req.getParameter("id")),
                req.getParameter("status"),
                req.getParameter("feedback"),
                (int) session.getAttribute("userId")
        );
    }
}

