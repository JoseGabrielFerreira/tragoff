document.addEventListener("DOMContentLoaded", function() {

    verificarLogin();

    var formMeta =
        document.getElementById("formMeta");

    if (formMeta) {

        document.getElementById("dataInicio").value =
            dataHoje();

        formMeta.addEventListener(
            "submit",
            function(event) {

                event.preventDefault();

                criarMeta();

            }
        );

    }

    carregarMetas();

});

function criarMeta() {

    var usuarioId =
        localStorage.getItem("usuarioId");

    var inicial =
        Number(
            document.getElementById(
                "cigarrosInicial"
            ).value
        );

    var meta =
        Number(
            document.getElementById(
                "cigarrosMeta"
            ).value
        );

    var dataInicio =
        document.getElementById(
            "dataInicio"
        ).value;

    var dataFim =
        document.getElementById(
            "dataFim"
        ).value;

    if (meta >= inicial) {

        mostrarMensagemMeta(
            "A meta deve ser menor que o consumo inicial."
        );

        return;

    }

    if (dataFim < dataInicio) {

        mostrarMensagemMeta(
            "A data final deve ser posterior à data inicial."
        );

        return;

    }

    var dados = {

        cigarrosPorDiaInicial: inicial,

        cigarrosPorDiaMeta: meta,

        dataInicio: dataInicio,

        dataFim: dataFim,

        usuario: {
            id: Number(usuarioId)
        }

    };

    fetch("/api/meta", {

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

                mostrarMensagemMeta(
                    resultado.mensagemErro ||
                    "Não foi possível criar a meta."
                );

                return;

            }

            alert("Meta criada com sucesso!");

            document.getElementById(
                "formMeta"
            ).reset();

            document.getElementById(
                "dataInicio"
            ).value = dataHoje();

            carregarMetas();

        })
        .catch(function() {

            mostrarMensagemMeta(
                "Erro ao conectar com o servidor."
            );

        });

}

function carregarMetas() {

    var usuarioId =
        localStorage.getItem("usuarioId");

    var elemento =
        document.getElementById(
            "listaMetas"
        );

    if (!elemento) {
        return;
    }

    fetch("/api/meta/usuario/" + usuarioId)
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {

                elemento.innerHTML =
                    "<p class='lista-vazia'>Não foi possível carregar as metas.</p>";

                return;

            }

            var metas =
                resultado.object;

            if (!metas || metas.length === 0) {

                elemento.innerHTML =
                    "<p class='lista-vazia'>Nenhuma meta cadastrada.</p>";

                return;

            }

            var html = "";

            metas.forEach(function(meta) {

                var statusClasse =
                    "status";

                if (meta.status === "ATIVA") {
                    statusClasse +=
                        " status-ativa";
                }

                if (meta.status === "CONCLUIDA") {
                    statusClasse +=
                        " status-concluida";
                }

                if (meta.status === "CANCELADA") {
                    statusClasse +=
                        " status-cancelada";
                }

                html +=

                    "<div class='item-lista'>" +

                    "<div>" +

                    "<strong>" +
                    "Reduzir para " +
                    meta.cigarrosPorDiaMeta +
                    " cigarros por dia" +
                    "</strong>" +

                    "<p>" +

                    "Início: " +
                    formatarData(meta.dataInicio) +

                    " | Fim: " +

                    formatarData(meta.dataFim) +

                    "</p>" +

                    "</div>" +

                    "<div>" +

                    "<span class='" +
                    statusClasse +
                    "'>" +

                    (meta.status || "ATIVA") +

                    "</span>" +

                    "</div>" +

                    "</div>";

            });

            elemento.innerHTML = html;

        })
        .catch(function() {

            elemento.innerHTML =
                "<p class='lista-vazia'>Erro ao carregar as metas.</p>";

        });

}

function mostrarMensagemMeta(texto) {

    var elemento =
        document.getElementById(
            "mensagemMeta"
        );

    elemento.textContent = texto;
    elemento.style.display = "block";

}