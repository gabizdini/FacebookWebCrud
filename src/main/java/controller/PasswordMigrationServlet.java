package controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.ModelException;
import model.utils.PasswordMigration;

/**
 * Servlet que executa a migração de senhas do banco de dados.
 * Acesso: http://localhost:8080/facebook/admin/migrate-passwords
 * 
 * IMPORTANTE: Execute apenas UMA VEZ! Após isso, você pode desativar este servlet
 * removendo a anotação @WebServlet ou deletando o arquivo.
 */
@WebServlet(urlPatterns = {"/admin/migrate-passwords", "/admin/test-passwords"})
public class PasswordMigrationServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		
		String action = req.getRequestURI();
		
		try {
			out.println("<html><head><meta charset='UTF-8'><style>");
			out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
			out.println(".success { color: green; background: #e8f5e9; padding: 10px; margin: 10px 0; border-left: 4px solid green; }");
			out.println(".info { color: #1976d2; background: #e3f2fd; padding: 10px; margin: 10px 0; border-left: 4px solid #1976d2; }");
			out.println(".error { color: red; background: #ffebee; padding: 10px; margin: 10px 0; border-left: 4px solid red; }");
			out.println("pre { background: #f5f5f5; padding: 10px; overflow-x: auto; }");
			out.println("</style></head><body>");
			
			if (action.contains("/admin/migrate-passwords")) {
				out.println("<h1>🔐 Migração de Senhas</h1>");
				out.println("<div class='info'><strong>Iniciando migração...</strong></div>");
				
				PasswordMigration.migratePasswords();
				
				out.println("<div class='success'><strong>✓ Migração concluída com sucesso!</strong></div>");
				out.println("<p>Todas as senhas foram criptografadas. Você pode agora fazer login normalmente.</p>");
				out.println("<a href='/facebook/login.jsp'>Voltar ao Login</a>");
				
			} else if (action.contains("/admin/test-passwords")) {
				out.println("<h1>🔍 Teste de Status de Senhas</h1>");
				out.println("<div class='info'><strong>Verificando status das senhas...</strong></div>");
				
				out.println("<pre>");
				PasswordMigration.testPasswordStatus();
				out.println("</pre>");
				
				out.println("<a href='/facebook/login.jsp'>Voltar ao Login</a>");
			}
			
			out.println("</body></html>");
			
		} catch (ModelException e) {
			out.println("<div class='error'>");
			out.println("<strong>❌ Erro na migração:</strong><br>");
			out.println(e.getMessage() + "<br>");
			e.printStackTrace(out);
			out.println("</div>");
			e.printStackTrace();
		}
	}
}
