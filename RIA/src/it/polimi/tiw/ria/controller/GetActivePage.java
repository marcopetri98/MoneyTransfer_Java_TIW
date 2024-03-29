package it.polimi.tiw.ria.controller;

import it.polimi.tiw.ria.beans.Account;
import it.polimi.tiw.ria.beans.User;
import it.polimi.tiw.ria.dao.UserDAO;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

@WebServlet("/activePage")
public class GetActivePage extends HttpServletThymeleaf {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ResourceBundle lang = findLanguage(req);
		ServletContext context = getServletContext();
		WebContext webContext = new WebContext(req,resp,context);
		String page = "activePage";
		UserDAO userDAO = new UserDAO(conn,lang.getLocale().getLanguage(),lang.getLocale().getCountry());
		User user = (User) req.getSession().getAttribute("user");
		List<Account> accounts;

		try {
			accounts = userDAO.findAccounts(user.getCode());
			webContext.setVariable("lang",lang);
			webContext.setVariable("user",user);
			webContext.setVariable("accounts",accounts);
			thymeleaf.process(page,webContext,resp.getWriter());
		} catch (SQLException e) {
			e.printStackTrace();
			resp.sendRedirect("/error?code=500");
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
