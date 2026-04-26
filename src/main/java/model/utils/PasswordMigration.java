package model.utils;

import java.util.ArrayList;
import java.util.List;
import model.ModelException;
import model.User;
import model.dao.DAOFactory;
import model.dao.DBHandler;
import model.dao.UserDAO;

/**
 * Utilitário para migrar senhas em texto plano para senhas criptografadas.
 * Execute este script uma única vez para criptografar todas as senhas existentes.
 */
public class PasswordMigration {

	/**
	 * Verifica se uma senha parece estar em Base64/SHA-256 (criptografada).
	 * Senhas criptografadas em Base64 têm padrão específico.
	 */
	private static boolean isEncrypted(String password) {
		if (password == null || password.isEmpty()) {
			return true; // Senha vazia é considerada ok
		}
		
		// SHA-256 em Base64 tem sempre 44 caracteres ou múltiplo específico
		// e contém apenas caracteres Base64: A-Z, a-z, 0-9, +, /, =
		if (password.length() != 44) {
			return false;
		}
		
		// Verifica se é válido Base64
		return password.matches("^[A-Za-z0-9+/]+=*$");
	}

	/**
	 * Realiza a migração de todas as senhas em texto plano para criptografadas.
	 */
	public static void migratePasswords() throws ModelException {
		System.out.println("Iniciando migração de senhas...");
		
		UserDAO dao = DAOFactory.createDAO(UserDAO.class);
		List<User> allUsers = dao.listAll();
		
		int migratedCount = 0;
		int errorCount = 0;
		
		for (User user : allUsers) {
			try {
				if (!isEncrypted(user.getPassword())) {
					System.out.println("Migrando usuário: " + user.getEmail());
					
					// Criptografa a senha
					String encryptedPassword = PasswordEncryptor.hashPassword(user.getPassword());
					user.setPassword(encryptedPassword);
					
					// Atualiza no banco
					dao.update(user);
					migratedCount++;
				} else {
					System.out.println("Usuário já criptografado: " + user.getEmail());
				}
			} catch (Exception e) {
				System.err.println("Erro ao migrar usuário ID " + user.getId() + ": " + e.getMessage());
				errorCount++;
				e.printStackTrace();
			}
		}
		
		System.out.println("Migração concluída!");
		System.out.println("Total migrado: " + migratedCount);
		System.out.println("Total com erro: " + errorCount);
	}

	/**
	 * Método para testar se uma senha específica está criptografada.
	 */
	public static void testPasswordStatus() throws ModelException {
		System.out.println("\n=== TESTE DE SENHAS ===");
		
		UserDAO dao = DAOFactory.createDAO(UserDAO.class);
		List<User> allUsers = dao.listAll();
		
		for (User user : allUsers) {
			String status = isEncrypted(user.getPassword()) ? "CRIPTOGRAFADA" : "TEXTO PLANO";
			System.out.println("Usuário: " + user.getEmail() + 
							   " | Status: " + status + 
							   " | Senha: " + user.getPassword());
		}
	}
}
