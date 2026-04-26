# 🔐 Solução: Erro de Login com Email e Senha

## Problema Identificado

O erro ocorria porque as senhas existentes no banco de dados estavam em **texto plano**, mas o sistema de autenticação esperava senhas **criptografadas em SHA-256 + Base64**.

### Fluxo do Problema:
1. **Ao registrar um usuário**: A senha é criptografada no `UsersController` usando `PasswordEncryptor.hashPassword()`
2. **Ao fazer login**: O sistema tenta comparar a senha fornecida com a armazenada usando `PasswordEncryptor.checkPassword()`
3. **Se as senhas antigas estão em texto plano**: A comparação falha porque:
   - Senha fornecida: "123456" → hash em SHA-256
   - Senha armazenada: "123456" (texto plano) → ❌ não corresponde ao hash

---

## ✅ Solução Implementada

Foram criadas **3 soluções**:

### 1. **Classe PasswordMigration** (`model.utils.PasswordMigration`)
Utilitário que:
- ✓ Detecta automaticamente senhas em texto plano
- ✓ Criptografa senhas não criptografadas
- ✓ Atualiza o banco de dados
- ✓ Evita re-criptografar senhas já criptografadas

### 2. **Servlet de Migração** (`controller.PasswordMigrationServlet`)
Interface web para executar a migração com interface amigável:
- ✓ Migração: `http://localhost:8080/facebook/admin/migrate-passwords`
- ✓ Teste: `http://localhost:8080/facebook/admin/test-passwords`

### 3. **Melhorias no DAO** (`model.dao.MySQLUserDAO`)
- ✓ Comentários mais claros no método `update()`
- ✓ Lógica preservada para manter senha anterior se não fornecida nova

---

## 🚀 Como Usar

### Passo 1: Compilar o Projeto
```bash
# No Eclipse: Project > Clean & Build
# Ou via Maven:
mvn clean compile
```

### Passo 2: Reiniciar o Tomcat
1. Parar o servidor Tomcat
2. Iniciar o servidor Tomcat novamente

### Passo 3: Executar a Migração
Acesse uma das URLs:
- **Teste de senhas**: `http://localhost:8080/facebook/admin/test-passwords`
- **Migrar senhas**: `http://localhost:8080/facebook/admin/migrate-passwords`

### Passo 4: Fazer Login
Após a migração, você poderá fazer login normalmente com:
- Email: `(seu email cadastrado)`
- Senha: `(sua senha original em texto plano)`

---

## 📋 Como Funciona a Criptografia

### ✓ Senhas Criptografadas (SHA-256 + Base64)
```
Senha original: "minhasenha123"
↓ SHA-256
↓ Base64
Resultado: "x7Yz9Kp2Lm5Qn8Wv3Rx6Tj1Fs4Dh7Gb0Cz5Bw8Vn2Ml3Pq6Sr9Ux0Oa+Jm="
Comprimento: 44 caracteres
```

### Detecção Automática
O sistema detecta se uma senha é:
- **Texto plano**: qualquer comprimento diferente de 44, ou caracteres fora do padrão Base64
- **Criptografada**: exatamente 44 caracteres, contendo apenas `[A-Za-z0-9+/=]`

---

## ⚙️ Configuração Técnica

### Classe: `PasswordEncryptor`
```java
// Criptografa uma senha
String hashed = PasswordEncryptor.hashPassword("minhasenha");

// Verifica se uma senha corresponde ao hash
boolean isValid = PasswordEncryptor.checkPassword("minhasenha", hashedPassword);
```

### LoginController (sem mudanças)
```java
// Já está correto:
if (user != null && PasswordEncryptor.checkPassword(userPW, user.getPassword())) {
    // Login bem-sucedido
}
```

---

## 🔒 Segurança

### Depois da Migração
- ✓ Todas as senhas estão criptografadas com SHA-256
- ✓ Novas senhas registradas são automaticamente criptografadas
- ✓ Atualizações de senha funcionam corretamente

### Após a Migração (Passo Final)
**Recomendado**: Deletar ou desativar os servlets de migração:
1. Remova ou comente a anotação `@WebServlet` em `PasswordMigrationServlet.java`
2. Deleta o arquivo `PasswordMigration.java` (opcional)

Isso evita que alguém tente re-executar a migração.

---

## 🐛 Troubleshooting

### Login ainda não funciona?
1. Verifique se a migração foi executada: `http://localhost:8080/facebook/admin/test-passwords`
2. Confirme que todas as senhas têm 44 caracteres
3. Verifique os logs do Tomcat em `Servers/Tomcat v10.1 Server at localhost-config/`

### Mensagem de erro?
- Verifique se a conexão com o banco MySQL está funcionando
- Confirme que a tabela `users` existe
- Verifique as permissões de escrita no banco

---

## 📝 Notas Importantes

- ⚠️ Execute a migração **apenas UMA VEZ**
- ⚠️ Faça backup do banco de dados antes de executar
- ⚠️ Se houver erro durante a migração, o sistema tentará de novo na próxima execução
- ✓ O sistema detecta automaticamente senhas já criptografadas e as pula

---

**Problema resolvido!** 🎉
