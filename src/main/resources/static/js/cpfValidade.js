document.addEventListener('DOMContentLoaded', function () {
  // CSS para feedback visual
  const style = document.createElement('style');
  style.textContent = `
    .campo-invalido {
      border: 2px solid red;
    }

    .mensagem-erro {
      color: red;
      font-size: 0.875rem;
      margin-top: 4px;
      display: block;
    }
  `;
  document.head.appendChild(style);

  // Função de validação do CPF
  function validarCPF(cpf) {
    cpf = cpf.replace(/[^\d]+/g, '');
    if (!cpf || cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;
    let soma = 0;
    for (let i = 0; i < 9; i++) soma += parseInt(cpf[i]) * (10 - i);
    let resto = 11 - (soma % 11);
    if ((resto >= 10 ? 0 : resto) !== parseInt(cpf[9])) return false;
    soma = 0;
    for (let i = 0; i < 10; i++) soma += parseInt(cpf[i]) * (11 - i);
    resto = 11 - (soma % 11);
    return (resto >= 10 ? 0 : resto) === parseInt(cpf[10]);
  }

  // Validação ao sair do campo
  const cpfInputs = document.querySelectorAll('input.validar-cpf');
  cpfInputs.forEach(input => {
    input.addEventListener('blur', function () {
      validarCampoCPF(this);
    });
  });

  // Intercepta envio de formulário
  const forms = document.querySelectorAll('form');
  forms.forEach(form => {
    form.addEventListener('submit', function (e) {
      let valido = true;

      cpfInputs.forEach(input => {
        if (!validarCampoCPF(input)) {
          valido = false;
        }
      });

      if (!valido) {
        e.preventDefault(); // impede envio
      }
    });
  });

  // Valida um campo de CPF específico
  function validarCampoCPF(campo) {
    const cpf = campo.value;
    campo.classList.remove('campo-invalido');
    const erro = campo.parentNode.querySelector('.mensagem-erro');
    if (erro) erro.remove();

    if (!validarCPF(cpf)) {
      campo.classList.add('campo-invalido');
      const erroDiv = document.createElement('div');
      erroDiv.classList.add('mensagem-erro');
      erroDiv.innerText = 'CPF inválido.';
      campo.parentNode.appendChild(erroDiv);
      return false;
    }

    return true;
  }
});
