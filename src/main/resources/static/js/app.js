// Tela principal : dashboard, registro, compras, histórico, metas e perfil.

var usuarioId = localStorage.getItem("usuarioId");
var tipos = [];   // produtos vindos do servidor (cigarro, tabaco, vape...)
var compras = [];  // últimas compras do usuário

var paginas = ["pagina_dashboard", "pagina_registrar", "pagina_compras",
               "pagina_historico", "pagina_metas", "pagina_perfil"];

function iniciar() {
    if (!usuarioId) {
        window.location.replace("login.html");
        return;
    }
    document.getElementById("saudacao").innerText = "Olá, " + localStorage.getItem("usuarioNome") + "!";
    document.getElementById("registrar_data").value = agoraLocal();
    document.getElementById("meta_inicio").value = agoraLocal().slice(0, 10);
    carregarTipos();
    carregarGatilhos();
    carregarComprasAtuais();
    carregarPerfil();
    mostrarPagina("pagina_dashboard");
}

// Mostra só uma página e carrega os dados dela
function mostrarPagina(pagina) {
    for (var i = 0; i < paginas.length; i++) {
        document.getElementById(paginas[i]).style.display = (paginas[i] == pagina) ? "block" : "none";
    }
    if (pagina == "pagina_dashboard") carregarDashboard();
    if (pagina == "pagina_historico") carregarHistorico();
    if (pagina == "pagina_compras") carregarCompras();
    if (pagina == "pagina_metas") carregarMetas();
    if (pagina == "pagina_registrar") carregarComprasAtuais();

    for (var j = 0; j < paginas.length; j++) {
        var link = document.getElementById("link_" + paginas[j].replace("pagina_", ""));
        if (link) link.className = (paginas[j] == pagina) ? "ativo" : "";
    }
}

function sair() {
    localStorage.removeItem("usuarioId");
    localStorage.removeItem("usuarioNome");
    window.location.replace("index.html");
}

// ---------- Produtos e gatilhos ----------

function carregarTipos() {
    chamarApi("/api/tipocigarro", "GET")
        .then(lista => {
            tipos = lista;
            var html = '<option value="">Selecione</option>';
            for (var i = 0; i < lista.length; i++) {
                html += '<option value="' + lista[i].id + '">' + lista[i].nome + '</option>';
            }
            document.getElementById("registrar_tipo").innerHTML = html;
            document.getElementById("compra_tipo").innerHTML = html;
        })
        .catch(erro => alert(erro.message));
}

function carregarComprasAtuais() {
    chamarApi("/api/compra/usuario/" + usuarioId, "GET")
        .then(lista => {
            compras = lista;
            mostrarCompraAtual();
        })
        .catch(() => {});
}

function mostrarCompraAtual() {
    var select = document.getElementById("registrar_tipo");
    var aviso = document.getElementById("compraAtual");
    if (!select || !aviso || !select.value) {
        return;
    }

    var tipo = null;
    for (var i = 0; i < tipos.length; i++) {
        if (tipos[i].id == select.value) {
            tipo = tipos[i];
            break;
        }
    }

    if (!tipo) return;

    // A compra em uso é a mais antiga que ainda tem unidades
    // (a lista vem da mais nova para a mais velha, então a última que achar é a mais antiga)
    var emUso = null;
    var jaComprou = false;
    for (var j = 0; j < compras.length; j++) {
        if (compras[j].tipoCigarro && compras[j].tipoCigarro.id == tipo.id) {
            jaComprou = true;
            if (compras[j].restante > 0) {
                emUso = compras[j];
            }
        }
    }

    if (emUso) {
        var preco = moeda(emUso.precoPago / emUso.quantidadeTotal, 3);
        aviso.innerHTML = "<strong>Compra em uso: " + tipo.nome + "</strong>" +
            "<p>Restam " + textoUnidade(emUso.restante, tipo.unidade) + " de " + emUso.quantidadeTotal +
            " (" + preco + " por " + (tipo.unidade == "tragadas" ? "tragada" : "unidade") + ").</p>";
    } else if (jaComprou) {
        aviso.innerHTML = "<strong>Seu estoque de " + tipo.nome + " acabou</strong>" +
            "<p>Registre uma nova compra em <b>Compras</b> para continuar.</p>";
    } else {
        aviso.innerHTML = "<strong>Nenhuma compra registrada para este produto</strong>" +
            "<p>Registre uma compra em <b>Compras</b> antes de registrar o consumo.</p>";
    }
}

function carregarGatilhos() {
    chamarApi("/api/gatilho", "GET")
        .then(lista => {
            var html = '<option value="">Nenhum</option>';
            for (var i = 0; i < lista.length; i++) {
                html += '<option value="' + lista[i].id + '">' + lista[i].nome + '</option>';
            }
            document.getElementById("registrar_gatilho").innerHTML = html;
        })
        .catch(erro => alert(erro.message));
}

// Mostra a unidade do produto escolhido ao lado do campo de quantidade
function mostrarUnidade(idSelect, idSpan) {
    var id = document.getElementById(idSelect).value;
    var texto = "";
    for (var i = 0; i < tipos.length; i++) {
        if (tipos[i].id == id) {
            texto = "(" + tipos[i].unidade + ")";
        }
    }
    document.getElementById(idSpan).innerText = texto;
}

// ---------- Registrar consumo e histórico ----------

function registrarConsumo() {
    var tipoId = document.getElementById("registrar_tipo").value;
    var gatilhoId = document.getElementById("registrar_gatilho").value;
    var data = document.getElementById("registrar_data").value;
    var quantidade = Number(document.getElementById("registrar_quantidade").value);

    if (tipoId == "") {
        alert("Selecione o produto.");
        return;
    }
    if (data == "") {
        alert("Informe a data e a hora.");
        return;
    }
    if (!quantidade || quantidade < 1) {
        alert("Informe uma quantidade válida.");
        return;
    }

    var dados = {
        dataHora: new Date(data).toISOString(),
        quantidade: quantidade,
        usuario: { id: Number(usuarioId) },
        tipoCigarro: { id: Number(tipoId) },
        gatilho: (gatilhoId == "") ? null : { id: Number(gatilhoId) }
    };

    chamarApi("/api/registro", "POST", dados)
        .then(() => {
            alert("Consumo registrado com sucesso!");
            document.getElementById("registrar_quantidade").value = 1;
            document.getElementById("registrar_data").value = agoraLocal();
            mostrarPagina("pagina_historico");
        })
        .catch(erro => alert(erro.message));
}

function carregarHistorico() {
    chamarApi("/api/registro/usuario/" + usuarioId, "GET")
        .then(lista => {
            if (lista.length == 0) {
                document.getElementById("tabelaHistorico").innerHTML = "<p>Nenhum registro encontrado.</p>";
                return;
            }
            var html = "<table><tr><th>Data e hora</th><th>Produto</th><th>Gatilho</th>" +
                       "<th>Consumo</th><th>Gasto</th><th></th></tr>";
            for (var i = 0; i < lista.length; i++) {
                var r = lista[i];
                var gatilho = r.gatilho ? r.gatilho.nome : "-";
                html += "<tr>" +
                    "<td>" + dataHora(r.dataHora) + "</td>" +
                    "<td>" + r.tipoCigarro.nome + "</td>" +
                    "<td>" + gatilho + "</td>" +
                    "<td>" + textoUnidade(r.quantidade, r.tipoCigarro.unidade) + "</td>" +
                    "<td>" + moeda(r.valorGasto) + "</td>" +
                    '<td><a href="javascript:void(0)" onclick="excluirRegistro(' + r.id + ')">Excluir</a></td>' +
                    "</tr>";
            }
            document.getElementById("tabelaHistorico").innerHTML = html + "</table>";
        })
        .catch(erro => alert(erro.message));
}

function excluirRegistro(id) {
    if (!confirm("Deseja realmente excluir este registro?")) {
        return;
    }
    chamarApi("/api/registro/" + id, "DELETE")
        .then(() => carregarHistorico())
        .catch(erro => alert(erro.message));
}

// ---------- Compras ----------

function registrarCompra() {
    var tipoId = document.getElementById("compra_tipo").value;

    if (tipoId == "") {
        alert("Selecione o produto.");
        return;
    }

    var dados = {
        precoPago: Number(document.getElementById("compra_preco").value),
        quantidadeTotal: Number(document.getElementById("compra_quantidade").value),
        usuario: { id: Number(usuarioId) },
        tipoCigarro: { id: Number(tipoId) }
    };

    chamarApi("/api/compra", "POST", dados)
        .then(() => {
            alert("Compra registrada com sucesso!");
            document.getElementById("compra_preco").value = "";
            document.getElementById("compra_quantidade").value = "";
            carregarCompras();
        })
        .catch(erro => alert(erro.message));
}

function carregarCompras() {
    chamarApi("/api/compra/usuario/" + usuarioId, "GET")
        .then(lista => {
            compras = lista;
            if (lista.length == 0) {
                document.getElementById("tabelaCompras").innerHTML = "<p>Nenhuma compra registrada.</p>";
                return;
            }
            var html = "<table><tr><th>Data</th><th>Produto</th><th>Preço pago</th>" +
                       "<th>Quantidade</th><th>Restante</th><th>Preço por unidade</th></tr>";
            for (var i = 0; i < lista.length; i++) {
                var c = lista[i];
                html += "<tr>" +
                    "<td>" + soData(c.dataCompra) + "</td>" +
                    "<td>" + c.tipoCigarro.nome + "</td>" +
                    "<td>" + moeda(c.precoPago) + "</td>" +
                    "<td>" + textoUnidade(c.quantidadeTotal, c.tipoCigarro.unidade) + "</td>" +
                    "<td>" + textoUnidade(c.restante, c.tipoCigarro.unidade) + "</td>" +
                    "<td>" + moeda(c.precoPago / c.quantidadeTotal, 3) + "</td>" +
                    "</tr>";
            }
            document.getElementById("tabelaCompras").innerHTML = html + "</table>";
        })
        .catch(erro => alert(erro.message));
}

// ---------- Dashboard ----------

function caixa(titulo, valor) {
    return '<div class="card"><small>' + titulo + '</small><div class="numero">' + valor + '</div></div>';
}

function carregarDashboard() {
    chamarApi("/api/dashboard/" + usuarioId, "GET")
        .then(d => {
            document.getElementById("gastos").innerHTML =
                caixa("Gasto hoje", moeda(d.gastoHoje)) +
                caixa("Gasto na semana", moeda(d.gastoSemana)) +
                caixa("Gasto no mês", moeda(d.gastoMes)) +
                caixa("Gasto total", moeda(d.gastoTotal));

            // Cada produto na sua própria unidade (nunca soma cigarro com tragada)
            var html = "<table><tr><th>Produto</th><th>Hoje</th><th>Total</th><th>Média por dia</th></tr>";
            for (var i = 0; i < d.porTipo.length; i++) {
                var t = d.porTipo[i];
                html += "<tr><td>" + t.nome + "</td>" +
                    "<td>" + textoUnidade(t.hoje, t.unidade) + "</td>" +
                    "<td>" + textoUnidade(t.total, t.unidade) + "</td>" +
                    "<td>" + textoUnidade(t.media, t.unidade) + "</td></tr>";
            }
            document.getElementById("porTipo").innerHTML = html + "</table>";


            html = "<table><tr><th>Dia</th><th>Cigarros</th><th>Tragadas</th></tr>";
            for (i = 0; i < d.ultimosDias.length; i++) {
                var dia = d.ultimosDias[i];
                html += "<tr><td>" + dia.data.substring(8, 10) + "/" + dia.data.substring(5, 7) + "</td>" +
                    "<td>" + dia.cigarros + "</td><td>" + dia.tragadas + "</td></tr>";
            }
            document.getElementById("ultimosDias").innerHTML = html + "</table>";

            html = "";
            for (i = 0; i < d.metas.length; i++) {
                var m = d.metas[i];
                html += "<p>Meta: até " + textoUnidade(m.meta, m.unidade) + " por dia (hoje: " + m.hoje + ")</p>" +
                    '<div class="barra"><div style="width: ' + m.progresso + '%"></div></div>' +
                    "<small>Progresso: " + m.progresso + "%</small><br><br>";
            }
            document.getElementById("metasAtivas").innerHTML = html || "<p>Nenhuma meta ativa.</p>";

            html = "";
            for (i = 0; i < d.gatilhos.length; i++) {
                html += "<p>" + d.gatilhos[i].nome + ": " + d.gatilhos[i].total + "</p>";
            }
            document.getElementById("gatilhos").innerHTML = html || "<p>Nenhum gatilho registrado.</p>";

            var horario = (d.horarioPico < 0) ? "-" : d.horarioPico + "h";
            document.getElementById("infos").innerHTML =
                "<p>Horário com mais registros: " + horario + "</p>" +
                "<p>Produto mais usado: " + (d.tipoMaisUsado || "-") + "</p>";
        })
        .catch(erro => alert(erro.message));
}

// ---------- Metas ----------

function criarMeta() {
    var inicio = document.getElementById("meta_inicio").value;
    var fim = document.getElementById("meta_fim").value;

    if (inicio == "" || fim == "") {
        alert("Informe as datas de início e final.");
        return;
    }

    if (Number(document.getElementById("meta_inicial").value) <= 0) {
        alert("Informe quanto você consome por dia hoje.");
        return;
    }

    if (Number(document.getElementById("meta_desejada").value) < 0) {
        alert("A meta não pode ser negativa.");
        return;
    }

    if (fim < inicio) {
        alert("A data final não pode ser anterior à data de início.");
        return;
    }

    var dados = {
        unidade: document.getElementById("meta_unidade").value,
        cigarrosPorDiaInicial: Number(document.getElementById("meta_inicial").value),
        cigarrosPorDiaMeta: Number(document.getElementById("meta_desejada").value),
        dataInicio: new Date(inicio + "T00:00:00").toISOString(),
        dataFim: new Date(fim + "T00:00:00").toISOString(),
        usuario: { id: Number(usuarioId) }
    };

    chamarApi("/api/meta", "POST", dados)
        .then(() => {
            alert("Meta criada com sucesso!");
            carregarMetas();
        })
        .catch(erro => alert(erro.message));
}

function carregarMetas() {
    chamarApi("/api/meta/usuario/" + usuarioId, "GET")
        .then(lista => {
            if (lista.length == 0) {
                document.getElementById("listaMetas").innerHTML = "<p>Nenhuma meta cadastrada.</p>";
                return;
            }
            var html = "<table><tr><th>Meta por dia</th><th>Início</th><th>Fim</th><th>Status</th></tr>";
            for (var i = 0; i < lista.length; i++) {
                var m = lista[i];
                html += "<tr>" +
                    "<td>" + textoUnidade(m.cigarrosPorDiaMeta, m.unidade || "cigarros") + "</td>" +
                    "<td>" + soData(m.dataInicio) + "</td>" +
                    "<td>" + soData(m.dataFim) + "</td>" +
                    "<td>" + m.status + "</td></tr>";
            }
            document.getElementById("listaMetas").innerHTML = html + "</table>";
        })
        .catch(erro => alert(erro.message));
}

// ---------- Perfil ----------

// Troca o texto do campo conforme a unidade escolhida (cigarros ou tragadas)
function atualizarRotuloPerfil() {
    var unidade = document.getElementById("perfil_unidade").value;
    var rotulo = document.getElementById("perfil_rotulo");
    if (unidade == "tragadas") {
        rotulo.innerText = "Tragadas por dia antes de começar";
    } else {
        rotulo.innerText = "Cigarros por dia antes de começar";
    }
}

function carregarPerfil() {
    chamarApi("/api/usuario/" + usuarioId, "GET")
        .then(u => {
            document.getElementById("perfil_nome").value = u.nome;
            document.getElementById("perfil_email").value = u.email;
        })
        .catch(erro => alert(erro.message));

    chamarApi("/api/perfil/" + usuarioId, "GET")
        .then(p => {
            if (p) {
                document.getElementById("perfil_anos").value = p.anosFumando;
                document.getElementById("perfil_cigarros").value = p.cigarrosPorDiaInicial;
                document.getElementById("perfil_unidade").value = p.unidade || "cigarros";
                atualizarRotuloPerfil();
                document.getElementById("perfil_motivo").value = p.motivoParaParar || "";
            }
        })
        .catch(erro => alert(erro.message));
}

function salvarUsuario() {
    var nome = document.getElementById("perfil_nome").value.trim();
    var dados = {
        nome: nome,
        email: document.getElementById("perfil_email").value.trim(),
        senha: document.getElementById("perfil_senha").value
    };

    chamarApi("/api/usuario/" + usuarioId, "PUT", dados)
        .then(() => {
            localStorage.setItem("usuarioNome", nome);
            document.getElementById("saudacao").innerText = "Olá, " + nome + "!";
            document.getElementById("perfil_senha").value = "";
            alert("Dados atualizados com sucesso!");
        })
        .catch(erro => alert(erro.message));
}

function salvarPerfil() {
    var dados = {
        anosFumando: Number(document.getElementById("perfil_anos").value),
        unidade: document.getElementById("perfil_unidade").value,
        cigarrosPorDiaInicial: Number(document.getElementById("perfil_cigarros").value),
        motivoParaParar: document.getElementById("perfil_motivo").value.trim()
    };

    chamarApi("/api/perfil/" + usuarioId, "POST", dados)
        .then(() => alert("Perfil salvo com sucesso!"))
        .catch(erro => alert(erro.message));
}