import { showErrorNotification } from "/js/module/helper/toastify.js";

function validateStep(step) {
  switch (step) {
    case 1:
      const titleElement = document.getElementById("title");
      if (!titleElement || !titleElement.value.trim()) {
        showErrorNotification("Por favor, preencha o título do arquivo.");
        return false;
      }
      break;

    case 2:
      const widthElement = document.getElementById("width");
      const heightElement = document.getElementById("height");
      const width = widthElement ? parseFloat(widthElement.value) : null;
      const height = heightElement ? parseFloat(heightElement.value) : null;
      
      if (!width || !height) {
        showErrorNotification("Por favor, preencha ambos os campos de largura e altura.");
        return false;
      }
      if (width <= 0 || height <= 0) {
        showErrorNotification("A largura e a altura devem ser maiores que zero.");
        return false;
      }
      break;

    case 3:
      const formatElement = document.getElementById("format");
      const compressionElement = document.getElementById("compression");
      if (!formatElement || !formatElement.value || !compressionElement || !compressionElement.value) {
        showErrorNotification("Por favor, selecione um formato de conversão e um nível de compressão.");
        return false;
      }
      break;

    case 4:
      const filesElement = document.getElementById("formFile");
      if (!filesElement || filesElement.files.length === 0) {
        showErrorNotification("Por favor, selecione pelo menos uma imagem para upload.");
        return false;
      }
      break;

    case 5:
      const downloadButton = document.getElementById("downloadButton");
      const fileId = downloadButton ? downloadButton.getAttribute("href") : null;
      if (!fileId || !downloadButton) {
        showErrorNotification("O botão de download está faltando ou o arquivo não está pronto para download.");
        return false;
      }
      break;

    default:
      console.warn("Etapa desconhecida:", step);
      return false;
  }
  return true;
}

export { validateStep };
