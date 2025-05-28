// alertas.js
function alertaSucesso(mensagem) {
  Swal.fire({
    icon: 'success',
    title: 'Sucesso',
    text: mensagem,
    timer: 3000,
    timerProgressBar: true,
    showConfirmButton: false
  });
}

function alertaErro(mensagem) {
  Swal.fire({
    icon: 'error',
    title: 'Erro',
    text: mensagem,
    timer: 4000,
    timerProgressBar: true,
    showConfirmButton: false
  });
}

/**
 * Exibe um alerta de confirmação para exclusão.
 * @param {Function} callback - Função chamada se o usuário confirmar a exclusão.
 */
function confirmarExclusao(callback) {
  Swal.fire({
    title: 'Tem certeza?',
    text: "Esta ação não pode ser desfeita!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Sim, excluir',
    cancelButtonText: 'Cancelar'
  }).then((result) => {
    if (result.isConfirmed) {
      callback();
    }
  });
}
