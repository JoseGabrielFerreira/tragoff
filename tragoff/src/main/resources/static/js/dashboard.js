document.addEventListener("DOMContentLoaded", function() {

    verificarLogin();

    var usuarioNome = localStorage.getItem("usuarioNome");

    if (usuarioNome) {

        document.getElementById("saudacao").textContent =
            "Olá, " + usuarioNome + "!";

    }

    carregarDashboard();

});

function carregarDashboard() {

    var usuarioId = localStorage.getItem("usuarioId");

    if (!usuarioId) {
        return;
    }

    fetch("/api/dashboard/" + usuarioId)
        .then(function(resposta) {
            return resposta.json();
        })
        .then(function(resultado) {

            if (resultado.status !== "OK") {
                mostrarErroDashboard();
                return;
            }

            var dados = resultado.object;

            document.getElementById("cigarrosHoje").textContent =
                dados.cigarrosHoje || 0;

            document.getElementById("gastoHoje").textContent =
                formatarDinheiro(dados.gastoHoje || 0);

            document.getElementById("mediaDiaria").textContent =
                dados.mediaDiaria || 0;

            document.getElementById("gastoTotal").textContent =
                formatarDinheiro(dados.gastoTotal || 0);

            document.getElementById("horarioMaiorConsumo").textContent =
                dados.horarioMaiorConsumo || "-";

            if (dados.tipoMaisConsumido) {

                document.getElementById("tipoMaisConsumido").textContent =
                    dados.tipoMaisConsumido.nome;

            } else {

                document.getElementById("tipoMaisConsumido").textContent =
                    "-";

            }

            document.getElementById("totalCigarros").textContent =
                dados.totalCigarros || 0;

            carregarUltimosSeteDias(
                dados.ultimosSeteDias
            );

            carregarMeta(dados);

        })
        .catch(function() {

            mostrarErroDashboard();

        });

}

function carregarUltimosSeteDias(dias) {

    var elemento = document.getElementById("graficoSeteDias");

    if (!dias || dias.length === 0) {

        elemento.innerHTML =
            "<p class='lista-vazia'>Nenhum registro encontrado.</p>";

        return;
    }

    var maiorValor = 0;

    dias.forEach(function(dia) {

        if (dia.quantidade > maiorValor) {
            maiorValor = dia.quantidade;
        }

    });

    var html = "";

    dias.forEach(function(dia) {

        var porcentagem = 0;

        if (maiorValor > 0) {
            porcentagem =
                (dia.quantidade / maiorValor) * 100;
        }

        var data = dia.data;

        if (data && data.length >= 10) {

            data =
                data.substring(8, 10) +
                "/" +
                data.substring(5, 7);

        }

        html +=
            '<div class="barra-item">' +

            '<div class="barra-info">' +

            '<span>' +
            data +
            '</span>' +

            '<strong>' +
            dia.quantidade +
            '</strong>' +

            '</div>' +

            '<div class="barra-fundo">' +

            '<div class="barra-preenchida" style="width: ' +
            porcentagem +
            '%"></div>' +

            '</div>' +

            '</div>';

    });

    elemento.innerHTML = html;

}

function carregarMeta(dados) {

    var elemento = document.getElementById("metaAtual");

    if (!dados.metaAtiva) {

        elemento.innerHTML =
            "<p class='lista-vazia'>Nenhuma meta ativa.</p>";

        return;
    }

    var meta = dados.metaAtiva;

    var progresso = dados.progressoMeta || 0;

    elemento.innerHTML =

        '<div class="meta-info">' +

        '<span>Meta diária</span>' +

        '<strong>' +
        meta.cigarrosPorDiaMeta +
        ' cigarros' +
        '</strong>' +

        '</div>' +

        '<div class="meta-info">' +

        '<span>Progresso</span>' +

        '<strong>' +
        progresso +
        '%' +
        '</strong>' +

        '</div>' +

        '<div class="barra-fundo">' +

        '<div class="barra-preenchida" style="width: ' +
        progresso +
        '%"></div>' +

        '</div>';

}

function mostrarErroDashboard() {

    document.getElementById("graficoSeteDias").innerHTML =
        "<p class='lista-vazia'>Não foi possível carregar os dados.</p>";

    document.getElementById("metaAtual").innerHTML =
        "<p class='lista-vazia'>Não foi possível carregar os dados.</p>";

}