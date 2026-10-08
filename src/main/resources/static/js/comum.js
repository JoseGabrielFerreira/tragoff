// Funções usadas por todas as telas.

// Chama a API e devolve o "object" do Retorno. Se o status não for "OK" (ou o servidor
// não responder), lança um erro com a mensagem, que cada tela mostra no .catch().
function chamarApi(url, metodo, dados) {
    var opcoes = { method: metodo, headers: { "Content-Type": "application/json" } };
    if (dados) {
        opcoes.body = JSON.stringify(dados);
    }
    return fetch(url, opcoes)
        .then(res => res.json())
        .catch(() => { throw new Error("Erro ao conectar com o servidor."); })
        .then(res => {
            if (res.status !== "OK") {
                throw new Error(res.mensagemErro || "Ocorreu um erro.");
            }
            return res.object;
        });
}

// 12.5 -> "R$ 12,50"
function moeda(valor, casas) {
    if (casas == null) {
        casas = 2;
    }
    return "R$ " + Number(valor).toFixed(casas).replace(".", ",");
}

function dataHora(data) {
    return new Date(data).toLocaleString("pt-BR");
}

function soData(data) {
    return new Date(data).toLocaleDateString("pt-BR");
}

// (5, "cigarros") -> "5 cigarros"    (1, "tragadas") -> "1 tragada"
function textoUnidade(quantidade, unidade) {
    if (!unidade) {
        return quantidade;
    }
    if (quantidade == 1) {
        unidade = unidade.slice(0, -1);
    }
    return quantidade + " " + unidade;
}

// Data e hora de agora no formato do <input type="datetime-local">
function agoraLocal() {
    var agora = new Date();
    agora.setMinutes(agora.getMinutes() - agora.getTimezoneOffset());
    return agora.toISOString().slice(0, 16);
}
