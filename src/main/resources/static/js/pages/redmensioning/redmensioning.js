import { validateStep } from '/js/module/helper/validate.js';
import { date as current } from "/js/module/helper/local-storage-utils.js";

const reader = new FileReader();
let currentStepIndex = current.currentStep || 0;
const preview = document.getElementById("preview");
const fileInput = document.getElementById("formFile");
const dropZone = document.getElementById("dropZone");
const previewDiv = document.querySelector(".preview");

async function createImagePreview(file, index) {
  // Criação do contêiner principal para a imagem e seus elementos
  const mainContainer = document.createElement("div");
  mainContainer.classList.add("flex", "flex-col", "items-center", "p-4", "rounded-lg", "shadow-md", "bg-white", "m-2");

  // Título da imagem
  const imgTitle = document.createElement("h3");
  imgTitle.classList.add("font-semibold", "text-center", "text-gray-700", "mb-2");
  imgTitle.textContent = `Imagem ${index + 1}: ${file.name} (${file.type.split("/")[1].toUpperCase()})`;

  // Leitura e criação da imagem
  const img = await new Promise((resolve, reject) => {
    reader.onload = function (event) {
      const img = new Image();
      img.src = event.target.result;
      img.classList.add("w-full", "h-40", "object-cover", "rounded-lg", "mb-2");
      img.onload = () => resolve(img);
      img.onerror = reject;
    };
    reader.readAsDataURL(file);
  });

  // Dimensões da imagem
  const imgDimensions = document.createElement("p");
  imgDimensions.classList.add("text-sm", "text-gray-500", "text-center");
  imgDimensions.textContent = `Dimensões: ${img.width} x ${img.height} pixels`;

  // Adiciona os elementos ao contêiner principal
  mainContainer.appendChild(imgTitle);
  mainContainer.appendChild(img);
  mainContainer.appendChild(imgDimensions);

  return mainContainer;
}

function clearPreview() {
  preview.innerHTML = "";
  previewDiv.innerHTML = "";
}

async function previewImages() {
  const files = fileInput.files;
  if (!files || !preview) return;

  preview.innerHTML = ""; // Limpar pré-visualizações anteriores

  const fileArray = Array.from(files);
  for (const [index, file] of fileArray.entries()) {
    const imgContainer = await createImagePreview(file, index);
    preview.appendChild(imgContainer);
  }

  previewDiv.classList.remove("hidden");
}

function setupDragAndDrop() {
  ["dragenter", "dragover", "dragleave", "drop"].forEach((eventName) => {
    dropZone.addEventListener(eventName, preventDefaults, false);
    document.body.addEventListener(eventName, preventDefaults, false);
  });

  ["dragenter", "dragover"].forEach((eventName) => {
    dropZone.addEventListener(
      eventName,
      () => dropZone.classList.add("bg-gray-200", "border-violet-500"),
      false
    );
  });

  ["dragleave", "drop"].forEach((eventName) => {
    dropZone.addEventListener(
      eventName,
      () => dropZone.classList.remove("bg-gray-200", "border-violet-500"),
      false
    );
  });

  dropZone.addEventListener("drop", async (e) => {
    const files = e.dataTransfer.files;
    fileInput.files = files;
    await previewImages();
  });

  function preventDefaults(e) {
    e.preventDefault();
    e.stopPropagation();
  }
}

function nextStep() {
  const maxSteps = 5;

  if (currentStepIndex >= maxSteps) return;

  if (validateStep(currentStepIndex + 1)) {
    currentStepIndex++;
    const totalSteps = document.querySelectorAll(".step").length;

    if (currentStepIndex < totalSteps) {
      showStep(currentStepIndex + 1);
    }
  }
}

function previousStep() {
  if (currentStepIndex > 0) {
    currentStepIndex--;
    showStep(currentStepIndex + 1);
  }

  if (preview.innerHTML) {
    clearPreview();
  }
}

function showStep(step) {
  document.querySelectorAll(".step").forEach((el) => el.classList.add("hidden"));
  const currentStepElement = document.getElementById(`step-${step}`);
  if (currentStepElement) {
    currentStepElement.classList.remove("hidden");
    updateProgress(step);
  }
}

function restoreStep() {
  if (currentStepIndex > 0) {
    showStep(currentStepIndex + 1);
  }
}

function updateProgress(step) {
  const progress = Math.min((step - 1) * 33.33, 100);
  const progressBar = document.getElementById("progressBar");
  const progressText = document.getElementById("progressText");

  if (progressBar && progressText) {
    progressBar.style.width = `${progress}%`;
    progressText.textContent = `${progress.toFixed(0)}%`;
  }
}

function startLoading() {
  const progressBar = document.getElementById('progressBar');
  const progressText = document.getElementById('progressText');

  if (progressBar && progressText) {
    progressBar.style.width = '100%';
    progressText.textContent = '100%';
  }
}

export {
  nextStep,
  showStep,
  restoreStep,
  previousStep,
  clearPreview,
  previewImages,
  setupDragAndDrop,
  createImagePreview,
  startLoading,
  updateProgress,
};
