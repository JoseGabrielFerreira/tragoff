document.addEventListener("DOMContentLoaded", function() {

    verificarLogin();

    var formRegistro = document.getElementById("formRegistro");

    if (formRegistro) {

        colocarDataHoraAtual("dataHora");

        carregarTipos();
        carregarGatilhos();

        formRegistro.addEventListener("submit", function(event) {

            event.preventDefault();

            registrarConsumo();

        });

    }

    var tabelaHistorico =
        document.getElementById("tabelaHistorico");

    if (tabelaHistorico) {

        carregarHistorico();

    }

});

function carregarTipos() {

    var select =
        document.getElementById("tipoCigarro");

    fetch("/api/tipocigarro")
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {
                return;
            }

            resultado.object.forEach(function(tipo) {

                var opcao =
                    document.createElement("option");

                opcao.value = tipo.id;
                opcao.textContent =
                    tipo.nome + " - " +
                    formatarDinheiro(tipo.precoUnitario);

                select.appendChild(opcao);

            });

        });

}

function carregarGatilhos() {

    var select =
        document.getElementById("gatilho");

    fetch("/api/gatilho")
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {
                return;
            }

            resultado.object.forEach(function(gatilho) {

                var opcao =
                    document.createElement("option");

                opcao.value = gatilho.id;
                opcao.textContent = gatilho.nome;

                select.appendChild(opcao);

            });

        });

}

function registrarConsumo() {

    var usuarioId =
        localStorage.getItem("usuarioId");

    var tipoId =
        document.getElementById("tipoCigarro").value;

    var gatilhoId =
        document.getElementById("gatilho").value;

    var quantidade =
        Number(document.getElementById("quantidade").value);

    var dataHora =
        document.getElementById("dataHora").value;

    if (!tipoId) {

        mostrarMensagemRegistro(
            "Selecione o tipo de cigarro."
        );

        return;

    }

    if (!quantidade || quantidade < 1) {

        mostrarMensagemRegistro(
            "Informe uma quantidade válida."
        );

        return;

    }

    var dados = {

        quantidade: quantidade,

        dataHora: dataHora,

        usuario: {
            id: Number(usuarioId)
        },

        tipoCigarro: {
            id: Number(tipoId)
        },

        gatilho: gatilhoId
            ? {
                id: Number(gatilhoId)
            }
            : null

    };

    fetch("/api/registro", {

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

                mostrarMensagemRegistro(
                    resultado.mensagemErro ||
                    "Não foi possível registrar o consumo."
                );

                return;

            }

            alert("Consumo registrado com sucesso!");

            window.location.replace(
                "historico.html"
            );

        })
        .catch(function() {

            mostrarMensagemRegistro(
                "Erro ao conectar com o servidor."
            );

        });

}

function carregarHistorico() {

    var usuarioId =
        localStorage.getItem("usuarioId");

    var tabela =
        document.getElementById("tabelaHistorico");

    fetch("/api/registro/usuario/" + usuarioId)
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {

                tabela.innerHTML =
                    "<tr><td colspan='6'>Não foi possível carregar o histórico.</td></tr>";

                return;

            }

            var registros = resultado.object;

            if (!registros || registros.length === 0) {

                tabela.innerHTML =
                    "<tr><td colspan='6'>Nenhum registro encontrado.</td></tr>";

                return;

            }

            var html = "";

            registros.forEach(function(registro) {

                var tipoNome = "-";
                var gatilhoNome = "-";
                var preco = 0;

                if (registro.tipoCigarro) {

                    tipoNome =
                        registro.tipoCigarro.nome;

                    preco =
                        Number(
                            registro.tipoCigarro.precoUnitario
                        );

                }

                if (registro.gatilho) {

                    gatilhoNome =
                        registro.gatilho.nome;

                }

                var gasto =
                    Number(registro.quantidade) *
                    preco;

                html +=

                    "<tr>" +

                    "<td>" +
                    formatarDataHora(
                        registro.dataHora
                    ) +
                    "</td>" +

                    "<td>" +
                    tipoNome +
                    "</td>" +

                    "<td>" +
                    gatilhoNome +
                    "</td>" +

                    "<td>" +
                    registro.quantidade +
                    "</td>" +

                    "<td>" +
                    formatarDinheiro(gasto) +
                    "</td>" +

                    "<td>" +

                    "<button " +
                    "class='botao-pequeno botao-excluir' " +
                    "onclick='excluirRegistro(" +
                    registro.id +
                    ")'>" +

                    "Excluir" +

                    "</button>" +

                    "</td>" +

                    "</tr>";

            });

            tabela.innerHTML = html;

        })
        .catch(function() {

            tabela.innerHTML =
                "<tr><td colspan='6'>Erro ao carregar o histórico.</td></tr>";

        });

}

function excluirRegistro(id) {

    var confirmar =
        confirm(
            "Deseja realmente excluir este registro?"
        );

    if (!confirmar) {
        return;
    }

    fetch("/api/registro/" + id, {

        method: "DELETE"

    })
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {

                alert(
                    resultado.mensagemErro ||
                    "Não foi possível excluir o registro."
                );

                return;

            }

            carregarHistorico();

        })
        .catch(function() {

            alert(
                "Erro ao conectar com o servidor."
            );

        });

}

function mostrarMensagemRegistro(texto) {

    var elemento =
        document.getElementById(
            "mensagemRegistro"
        );

    elemento.textContent = texto;
    elemento.style.display = "block";

}