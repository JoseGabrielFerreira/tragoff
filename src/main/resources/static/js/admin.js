// Área administrativa: produtos, gatilhos e lista de usuários.

function iniciarAdmin() {
    var logado = localStorage.getItem("adminId");
    document.getElementById("admin_login").style.display = logado ? "none" : "block";
    document.getElementById("admin_painel").style.display = logado ? "block" : "none";
    if (logado) {
        carregarTipos();
        carregarGatilhos();
        carregarUsuarios();
    }
}

function loginAdmin() {
    var dados = {
        nome: "",
        email: document.getElementById("admin_email").value.trim(),
        senha: document.getElementById("admin_senha").value
    };

    chamarApi("/api/administrador/login", "POST", dados)
        .then(admin => {
            localStorage.setItem("adminId", admin.id);
            iniciarAdmin();
        })
        .catch(erro => alert(erro.message));
}

function sairAdmin() {
    localStorage.removeItem("adminId");
    window.location.replace("index.html");
}

// ---------- Produtos ----------

function cadastrarTipo() {
    var dados = {
        nome: document.getElementById("tipo_nome").value.trim(),
        unidade: document.getElementById("tipo_unidade").value,
        precoUnitario: Number(document.getElementById("tipo_preco").value)
    };

    chamarApi("/api/tipocigarro", "POST", dados)
        .then(() => {
            document.getElementById("tipo_nome").value = "";
            carregarTipos();
        })
        .catch(erro => alert(erro.message));
}

function carregarTipos() {
    chamarApi("/api/tipocigarro", "GET")
        .then(lista => {
            var html = "<table><tr><th>Nome</th><th>Unidade</th><th>Preço padrão</th><th></th></tr>";
            for (var i = 0; i < lista.length; i++) {
                var t = lista[i];
                html += "<tr><td>" + t.nome + "</td><td>" + t.unidade + "</td>" +
                    "<td>" + moeda(t.precoUnitario) + "</td>" +
                    '<td><a href="javascript:void(0)" onclick="excluirTipo(' + t.id + ')">Excluir</a></td></tr>';
            }
            document.getElementById("listaTipos").innerHTML = html + "</table>";
        })
        .catch(erro => alert(erro.message));
}

function excluirTipo(id) {
    if (!confirm("Deseja excluir este produto?")) {
        return;
    }
    chamarApi("/api/tipocigarro/" + id, "DELETE")
        .then(() => carregarTipos())
        .catch(erro => alert(erro.message));
}

// ---------- Gatilhos ----------

function cadastrarGatilho() {
    var dados = { nome: document.getElementById("gatilho_nome").value.trim() };

    chamarApi("/api/gatilho", "POST", dados)
        .then(() => {
            document.getElementById("gatilho_nome").value = "";
            carregarGatilhos();
        })
        .catch(erro => alert(erro.message));
}

function carregarGatilhos() {
    chamarApi("/api/gatilho", "GET")
        .then(lista => {
            var html = "<table>";
            for (var i = 0; i < lista.length; i++) {
                html += "<tr><td>" + lista[i].nome + "</td>" +
                    '<td><a href="javascript:void(0)" onclick="excluirGatilho(' + lista[i].id + ')">Excluir</a></td></tr>';
            }
            document.getElementById("listaGatilhos").innerHTML = html + "</table>";
        })
        .catch(erro => alert(erro.message));
}

function excluirGatilho(id) {
    if (!confirm("Deseja excluir este gatilho?")) {
        return;
    }
    chamarApi("/api/gatilho/" + id, "DELETE")
        .then(() => carregarGatilhos())
        .catch(erro => alert(erro.message));
}

// ---------- Usuários ----------

function carregarUsuarios() {
    chamarApi("/api/usuario", "GET")
        .then(lista => {
            var html = "<table><tr><th>ID</th><th>Nome</th><th>E-mail</th><th>Cadastro</th></tr>";
            for (var i = 0; i < lista.length; i++) {
                var u = lista[i];
                html += "<tr><td>" + u.id + "</td><td>" + u.nome + "</td><td>" + u.email + "</td>" +
                    "<td>" + soData(u.dataCadastro) + "</td></tr>";
            }
            document.getElementById("listaUsuarios").innerHTML = html + "</table>";
        })
        .catch(erro => alert(erro.message));
}
