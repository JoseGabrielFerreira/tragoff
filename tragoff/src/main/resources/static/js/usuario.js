document.addEventListener("DOMContentLoaded", function() {

    var formCadastro = document.getElementById("formCadastro");

    if (formCadastro) {
        carregarTiposCigarro();

        formCadastro.addEventListener("submit", function(event) {
            event.preventDefault();
            cadastrarUsuario();
        });
    }

    var formLogin = document.getElementById("formLogin");

    if (formLogin) {
        formLogin.addEventListener("submit", function(event) {
            event.preventDefault();
            fazerLogin();
        });
    }

});

function carregarTiposCigarro() {

    var select = document.getElementById("tipoCigarro");

    fetch("/api/tipocigarro")
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(dados) {

            if (dados.status !== "OK") {
                return;
            }

            dados.object.forEach(function(tipo) {

                var opcao = document.createElement("option");

                opcao.value = tipo.id;
                opcao.textContent = tipo.nome;

                select.appendChild(opcao);

            });

        })
        .catch(function() {

            mostrarMensagem(
                "mensagemCadastro",
                "Não foi possível carregar os tipos de cigarro."
            );

        });

}

function cadastrarUsuario() {

    var nome = document.getElementById("nome").value.trim();
    var email = document.getElementById("email").value.trim();
    var senha = document.getElementById("senha").value;
    var confirmarSenha = document.getElementById("confirmarSenha").value;

    if (senha !== confirmarSenha) {

        mostrarMensagem(
            "mensagemCadastro",
            "As senhas não são iguais."
        );

        return;
    }

    var dados = {
        nome: nome,
        email: email,
        senha: senha
    };

    fetch("/api/usuario", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(dados)
    })
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {

                mostrarMensagem(
                    "mensagemCadastro",
                    resultado.mensagemErro || "Não foi possível realizar o cadastro."
                );

                return;
            }

            alert("Cadastro realizado com sucesso!");

            window.location.replace("login.html");

        })
        .catch(function() {

            mostrarMensagem(
                "mensagemCadastro",
                "Erro ao conectar com o servidor."
            );

        });

}

function fazerLogin() {

    var email = document.getElementById("email").value.trim();
    var senha = document.getElementById("senha").value;

    var dados = {
        nome: "",
        email: email,
        senha: senha
    };

    fetch("/api/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(dados)
    })
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {

                mostrarMensagem(
                    "mensagemLogin",
                    resultado.mensagemErro || "E-mail ou senha inválidos."
                );

                return;
            }

            localStorage.setItem(
                "usuarioId",
                resultado.object.id
            );

            localStorage.setItem(
                "usuarioNome",
                resultado.object.nome
            );

            window.location.replace("dashboard.html");

        })
        .catch(function() {

            mostrarMensagem(
                "mensagemLogin",
                "Erro ao conectar com o servidor."
            );

        });

}

function mostrarMensagem(id, texto) {

    var elemento = document.getElementById(id);

    elemento.textContent = texto;
    elemento.style.display = "block";

}