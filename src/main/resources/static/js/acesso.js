// Login e cadastro do usuário.

function fazerLogin() {
    var dados = {
        email: document.getElementById("email").value.trim(),
        senha: document.getElementById("senha").value
    };

    chamarApi("/api/login", "POST", dados)
        .then(usuario => {
            localStorage.setItem("usuarioId", usuario.id);
            localStorage.setItem("usuarioNome", usuario.nome);
            window.location.replace("app.html");
        })
        .catch(erro => alert(erro.message));
}

function cadastrarUsuario() {
    var senha = document.getElementById("senha").value;

    if (senha !== document.getElementById("confirmarSenha").value) {
        alert("As senhas não são iguais.");
        return;
    }

    var dados = {
        nome: document.getElementById("nome").value.trim(),
        email: document.getElementById("email").value.trim(),
        senha: senha
    };

    chamarApi("/api/usuario", "POST", dados)
        .then(() => {
            alert("Cadastro realizado com sucesso!");
            window.location.replace("login.html");
        })
        .catch(erro => alert(erro.message));
}
