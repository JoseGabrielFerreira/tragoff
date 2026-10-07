document.addEventListener("DOMContentLoaded", function() {

    var formLoginAdmin =
        document.getElementById(
            "formLoginAdmin"
        );

    if (formLoginAdmin) {

        formLoginAdmin.addEventListener(
            "submit",
            function(event) {

                event.preventDefault();

                fazerLoginAdmin();

            }
        );

        return;

    }

    verificarLoginAdmin();

    var formTipo =
        document.getElementById(
            "formTipo"
        );

    if (formTipo) {

        formTipo.addEventListener(
            "submit",
            function(event) {

                event.preventDefault();

                cadastrarTipo();

            }
        );

    }

    var formGatilho =
        document.getElementById(
            "formGatilho"
        );

    if (formGatilho) {

        formGatilho.addEventListener(
            "submit",
            function(event) {

                event.preventDefault();

                cadastrarGatilho();

            }
        );

    }

    carregarTiposAdmin();
    carregarGatilhosAdmin();
    carregarUsuarios();

});

function fazerLoginAdmin() {

    var email =
        document.getElementById(
            "email"
        ).value.trim();

    var senha =
        document.getElementById(
            "senha"
        ).value;

    var dados = {

        nome: "",

        email: email,

        senha: senha

    };

    fetch("/api/administrador/login", {

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

                mostrarMensagemAdmin(
                    resultado.mensagemErro ||
                    "E-mail ou senha inválidos."
                );

                return;

            }

            localStorage.setItem(
                "adminId",
                resultado.object.id
            );

            window.location.replace(
                "admin.html"
            );

        })
        .catch(function() {

            mostrarMensagemAdmin(
                "Erro ao conectar com o servidor."
            );

        });

}

function cadastrarTipo() {

    var nome =
        document.getElementById(
            "nomeTipo"
        ).value.trim();

    var preco =
        Number(
            document.getElementById(
                "precoTipo"
            ).value
        );

    var dados = {

        nome: nome,

        precoUnitario: preco

    };

    fetch("/api/tipocigarro", {

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

                mostrarMensagemTipo(
                    resultado.mensagemErro ||
                    "Não foi possível cadastrar o tipo."
                );

                return;

            }

            document.getElementById(
                "formTipo"
            ).reset();

            carregarTiposAdmin();

        })
        .catch(function() {

            mostrarMensagemTipo(
                "Erro ao conectar com o servidor."
            );

        });

}

function carregarTiposAdmin() {

    var elemento =
        document.getElementById(
            "listaTipos"
        );

    if (!elemento) {
        return;
    }

    fetch("/api/tipocigarro")
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {
                return;
            }

            var tipos =
                resultado.object;

            if (!tipos || tipos.length === 0) {

                elemento.innerHTML =
                    "<p class='lista-vazia'>Nenhum tipo cadastrado.</p>";

                return;

            }

            var html = "";

            tipos.forEach(function(tipo) {

                html +=

                    "<div class='item-lista'>" +

                    "<div>" +

                    "<strong>" +
                    tipo.nome +
                    "</strong>" +

                    "<p>" +
                    formatarDinheiro(
                        tipo.precoUnitario
                    ) +
                    "</p>" +

                    "</div>" +

                    "<div class='botoes-acoes'>" +

                    "<button " +
                    "class='botao-pequeno botao-excluir' " +
                    "onclick='excluirTipo(" +
                    tipo.id +
                    ")'>" +

                    "Excluir" +

                    "</button>" +

                    "</div>" +

                    "</div>";

            });

            elemento.innerHTML = html;

        });

}

function excluirTipo(id) {

    if (!confirm("Deseja excluir este tipo?")) {
        return;
    }

    fetch("/api/tipocigarro/" + id, {

        method: "DELETE"

    })
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {

                alert(
                    resultado.mensagemErro ||
                    "Não foi possível excluir."
                );

                return;

            }

            carregarTiposAdmin();

        });

}

function cadastrarGatilho() {

    var nome =
        document.getElementById(
            "nomeGatilho"
        ).value.trim();

    var dados = {

        nome: nome

    };

    fetch("/api/gatilho", {

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

                mostrarMensagemGatilho(
                    resultado.mensagemErro ||
                    "Não foi possível cadastrar o gatilho."
                );

                return;

            }

            document.getElementById(
                "formGatilho"
            ).reset();

            carregarGatilhosAdmin();

        })
        .catch(function() {

            mostrarMensagemGatilho(
                "Erro ao conectar com o servidor."
            );

        });

}

function carregarGatilhosAdmin() {

    var elemento =
        document.getElementById(
            "listaGatilhos"
        );

    if (!elemento) {
        return;
    }

    fetch("/api/gatilho")
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {
                return;
            }

            var gatilhos =
                resultado.object;

            if (!gatilhos || gatilhos.length === 0) {

                elemento.innerHTML =
                    "<p class='lista-vazia'>Nenhum gatilho cadastrado.</p>";

                return;

            }

            var html = "";

            gatilhos.forEach(function(gatilho) {

                html +=

                    "<div class='item-lista'>" +

                    "<strong>" +
                    gatilho.nome +
                    "</strong>" +

                    "<button " +
                    "class='botao-pequeno botao-excluir' " +
                    "onclick='excluirGatilho(" +
                    gatilho.id +
                    ")'>" +

                    "Excluir" +

                    "</button>" +

                    "</div>";

            });

            elemento.innerHTML = html;

        });

}

function excluirGatilho(id) {

    if (!confirm("Deseja excluir este gatilho?")) {
        return;
    }

    fetch("/api/gatilho/" + id, {

        method: "DELETE"

    })
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {

                alert(
                    resultado.mensagemErro ||
                    "Não foi possível excluir."
                );

                return;

            }

            carregarGatilhosAdmin();

        });

}

function carregarUsuarios() {

    var tabela =
        document.getElementById(
            "tabelaUsuarios"
        );

    if (!tabela) {
        return;
    }

    fetch("/api/usuario")
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {

                tabela.innerHTML =
                    "<tr><td colspan='4'>Não foi possível carregar os usuários.</td></tr>";

                return;

            }

            var usuarios =
                resultado.object;

            if (!usuarios || usuarios.length === 0) {

                tabela.innerHTML =
                    "<tr><td colspan='4'>Nenhum usuário encontrado.</td></tr>";

                return;

            }

            var html = "";

            usuarios.forEach(function(usuario) {

                html +=

                    "<tr>" +

                    "<td>" +
                    usuario.id +
                    "</td>" +

                    "<td>" +
                    usuario.nome +
                    "</td>" +

                    "<td>" +
                    usuario.email +
                    "</td>" +

                    "<td>" +
                    formatarData(
                        usuario.dataCadastro
                    ) +
                    "</td>" +

                    "</tr>";

            });

            tabela.innerHTML = html;

        });

}

function mostrarMensagemAdmin(texto) {

    var elemento =
        document.getElementById(
            "mensagemAdmin"
        );

    elemento.textContent = texto;
    elemento.style.display = "block";

}

function mostrarMensagemTipo(texto) {

    var elemento =
        document.getElementById(
            "mensagemTipo"
        );

    elemento.textContent = texto;
    elemento.style.display = "block";

}

function mostrarMensagemGatilho(texto) {

    var elemento =
        document.getElementById(
            "mensagemGatilho"
        );

    elemento.textContent = texto;
    elemento.style.display = "block";

}