document.addEventListener("DOMContentLoaded", function() {

    verificarLogin();

    carregarUsuario();
    carregarPerfil();

    var formPerfil =
        document.getElementById("formPerfil");

    var formFumante =
        document.getElementById("formFumante");

    formPerfil.addEventListener(
        "submit",
        function(event) {

            event.preventDefault();

            salvarUsuario();

        }
    );

    formFumante.addEventListener(
        "submit",
        function(event) {

            event.preventDefault();

            salvarPerfil();

        }
    );

});

function carregarUsuario() {

    var usuarioId =
        localStorage.getItem("usuarioId");

    fetch("/api/usuario/" + usuarioId)
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {
                return;
            }

            var usuario =
                resultado.object;

            document.getElementById(
                "nomePerfil"
            ).value = usuario.nome || "";

            document.getElementById(
                "emailPerfil"
            ).value = usuario.email || "";

        });

}

function carregarPerfil() {

    var usuarioId =
        localStorage.getItem("usuarioId");

    fetch("/api/perfil/" + usuarioId)
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {
                return;
            }

            if (!resultado.object) {
                return;
            }

            var perfil =
                resultado.object;

            document.getElementById(
                "anosFumando"
            ).value =
                perfil.anosFumando || "";

            document.getElementById(
                "cigarrosInicial"
            ).value =
                perfil.cigarrosPorDiaInicial || "";

            document.getElementById(
                "motivo"
            ).value =
                perfil.motivoParaParar || "";

        });

}

function salvarUsuario() {

    var usuarioId =
        localStorage.getItem("usuarioId");

    var nome =
        document.getElementById(
            "nomePerfil"
        ).value.trim();

    var email =
        document.getElementById(
            "emailPerfil"
        ).value.trim();

    var senha =
        document.getElementById(
            "senhaPerfil"
        ).value;

    var dados = {

        nome: nome,

        email: email,

        senha: senha

    };

    fetch("/api/usuario/" + usuarioId, {

        method: "PUT",

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

                alert(
                    resultado.mensagemErro ||
                    "Não foi possível atualizar os dados."
                );

                return;

            }

            localStorage.setItem(
                "usuarioNome",
                nome
            );

            alert(
                "Dados atualizados com sucesso!"
            );

        })
        .catch(function() {

            alert(
                "Erro ao conectar com o servidor."
            );

        });

}

function salvarPerfil() {

    var usuarioId =
        localStorage.getItem("usuarioId");

    var anosFumando =
        Number(
            document.getElementById(
                "anosFumando"
            ).value
        );

    var cigarrosPorDiaInicial =
        Number(
            document.getElementById(
                "cigarrosInicial"
            ).value
        );

    var motivoParaParar =
        document.getElementById(
            "motivo"
        ).value.trim();

    var dados = {

        anosFumando: anosFumando,

        cigarrosPorDiaInicial:
        cigarrosPorDiaInicial,

        motivoParaParar:
        motivoParaParar

    };

    fetch("/api/perfil/" + usuarioId, {

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

                mostrarMensagemPerfil(
                    resultado.mensagemErro ||
                    "Não foi possível salvar o perfil."
                );

                return;

            }

            alert(
                "Perfil salvo com sucesso!"
            );

        })
        .catch(function() {

            mostrarMensagemPerfil(
                "Erro ao conectar com o servidor."
            );

        });

}

function mostrarMensagemPerfil(texto) {

    var elemento =
        document.getElementById(
            "mensagemPerfil"
        );

    elemento.textContent = texto;
    elemento.style.display = "block";

}