# ✅ SOLUÇÃO: Erro ao Fazer Update em Posts

## 🐛 Problema
```
java.lang.NumberFormatException: Cannot parse null string
at controller.PostsController.createPost(PostsController.java:100)
```

## 🔍 Causa
1. **Parâmetro `user_id` era null** - Quando o formulário era carregado, o `user_id` não era passado como parâmetro
2. **Tentativa de converter null** - `Integer.parseInt(null)` causa `NumberFormatException`
3. **Lista de usuários não carregada** - Ao editar um post, a lista de usuários não era carregada no `loadPost()`
4. **Nenhuma validação** - Faltava validação para parâmetros obrigatórios

## ✅ Correções Implementadas

### 1. PostsController.java - método `createPost()` (linhas 95-119)
```java
// ANTES (ERRO):
String postUser = req.getParameter("user_id");
int postUserId = Integer.parseInt(postUser);  // ❌ Causa erro se null

// DEPOIS (CORRIGIDO):
String postUser = req.getParameter("user_id");

// ✓ Validação
if (postUser == null || postUser.equals("")) {
    throw new IllegalArgumentException("Usuário (user_id) é obrigatório!");
}

int postUserId = Integer.parseInt(postUser);  // ✓ Agora seguro
```

**Melhorias:**
- ✓ Valida se `user_id` está presente
- ✓ Verifica se `postId` é null antes de comparar com `equals()`
- ✓ Mensagem de erro clara se parâmetro obrigatório estiver faltando

### 2. PostsController.java - método `loadPost()` (linhas 167-187)
```java
// ANTES (INCOMPLETO):
req.setAttribute("post", post);
// ❌ Não carregava usuários

// DEPOIS (CORRIGIDO):
req.setAttribute("post", post);
loadUsers(req);  // ✓ Carrega a lista de usuários
```

**Melhorias:**
- ✓ Agora carrega a lista de usuários ao editar um post
- ✓ O select no formulário terá opções disponíveis

### 3. form_post.jsp - Melhorias no Formulário
**Antes:**
```jsp
<h1>Novo post</h1>
<select name="user_id">  <!-- ❌ Sem validação -->
    <c:forEach var="usuario" items="${usuarios}">
        <option value="${usuario.id}">${usuario.name}</option>
    </c:forEach>
</select>
```

**Depois:**
```jsp
<h1>
    <c:if test="${post.getId() == 0}">Novo post</c:if>
    <c:if test="${post.getId() != 0}">Editar post</c:if>
</h1>

<select name="user_id" class="form-control" required>  <!-- ✓ Com required -->
    <option value="">-- Selecione um usuário --</option>
    <c:forEach var="usuario" items="${usuarios}">
        <option value="${usuario.id}" 
            <c:if test="${usuario.id == post.getUser().getId()}">selected</c:if>>
            ${usuario.name}
        </option>
    </c:forEach>
</select>
<a href="/facebook/posts" class="btn btn-secondary">Cancelar</a>
```

**Melhorias:**
- ✓ Título dinâmico (Novo post / Editar post)
- ✓ Opção padrão com placeholder
- ✓ Atributo `required` no select
- ✓ Pre-seleção do usuário ao editar
- ✓ Botão "Cancelar" para voltar
- ✓ Bootstrap classes para melhor visual

---

## 🚀 Como Testar

### 1. Novo Post
1. Clique em "Novo post"
2. Preencha o conteúdo
3. Selecione um usuário
4. Clique em "Enviar"
✓ Deve funcionar sem erro

### 2. Editar Post
1. Clique em "Editar" em um post existente
2. O formulário deve mostrar:
   - ✓ Título "Editar post"
   - ✓ Conteúdo pré-preenchido
   - ✓ Lista de usuários com o atual selecionado
3. Altere conforme necessário
4. Clique em "Enviar"
✓ Deve funcionar sem erro

### 3. Cancelar
1. Em qualquer formulário, clique em "Cancelar"
✓ Deve voltar para a lista de posts

---

## 📋 Arquivos Modificados

1. ✅ `PostsController.java` - Validações e carregamento de usuários
2. ✅ `form_post.jsp` - Melhorias no formulário

## 📋 Arquivos Criados (Problema de Login)

Anteriormente foram criados para resolver o problema de login:
- ✅ `PasswordMigration.java` - Migração de senhas
- ✅ `PasswordMigrationServlet.java` - Interface para migração
- ✅ `MIGRATION_README.md` - Documentação
- ✅ `FIX_LOGIN_QUICK.txt` - Guia rápido

---

## ✨ Próximos Passos

1. **Recompile o projeto**
   - Eclipse: `Project > Clean > Build Project`

2. **Reinicie o Tomcat**
   - Stop Tomcat
   - Start Tomcat

3. **Teste o fluxo completo**
   - Crie novo post
   - Edite post existente
   - Delete um post

---

**Problema resolvido!** 🎉
