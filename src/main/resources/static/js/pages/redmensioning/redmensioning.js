import { validateStep } from "/js/module/helper/validate.js";
import { showErrorNotification, showNotification } from "/js/module/helper/toastify.js";

let currentStepIndex = 0;
let form = document.getElementById('multiStepForm');
const preview = document.getElementById("preview");
const fileInput = document.getElementById("formFile");
const dropZone = document.getElementById("dropZone");
const previewDiv = document.querySelector(".preview");

async function createImagePreview(files, index) {
  if (!validateImages(files)) {
    return false;
  }

  const mainContainer = document.createElement("div");
  mainContainer.classList.add("flex", "flex-col", "items-center", "p-4", "rounded-lg", "shadow-md", "bg-white", "m-2", "preview-container");

  const removeBtn = document.createElement("button");
  const imgTitle = document.createElement("h3");
  removeBtn.textContent = "x";
  removeBtn.classList.add("text-red-500", "text-xl", "hover:text-red-700", "font-bold");
  imgTitle.classList.add("font-semibold", "text-center", "text-gray-700", "mb-2");
  imgTitle.textContent = `Imagem ${index + 1}: ${files.name} (${files.type.split("/")[1].toUpperCase()})`;

  const reader = new FileReader(); // Defina o FileReader
  const img = await new Promise((resolve, reject) => {
    reader.onload = function (event) {
      const img = new Image();
      img.src = event.target.result;
      img.classList.add("w-full", "h-40", "object-cover", "rounded-lg", "mb-2");
      img.onload = () => resolve(img);
      img.onerror = reject;
    };
    reader.readAsDataURL(files);
  });

  const imgContainer = document.createElement("div");
  imgContainer.classList.add("relative", "w-full", "h-40", "mb-2");

  removeBtn.addEventListener("click", () => {
    removeImage(index);
  });

  imgContainer.appendChild(removeBtn);
  imgContainer.appendChild(img);

  const imgDimensions = document.createElement("p");
  imgDimensions.classList.add("text-sm", "text-gray-500", "text-center");
  imgDimensions.textContent = `Dimensões: ${img.width} x ${img.height} pixels`;

  mainContainer.appendChild(imgTitle);
  mainContainer.appendChild(imgContainer);
  mainContainer.appendChild(imgDimensions);

  if (files.length >= 20) {
    displayImages(files);
  }

  return mainContainer;
}

function displayImages(files) {
  const imageContainer = document.getElementById("image-container");
  const batchSize = 20; 

  let currentBatchIndex = 0;

  function loadImageBatch() {
    const start = currentBatchIndex * batchSize;
    const end = start + batchSize;
    const batch = files.slice(start, end);

    batch.forEach((file, index) => {
      createImagePreview(file, start + index).then((imagePreview) => {
        imageContainer.appendChild(imagePreview);
      });
    });

    currentBatchIndex++;
  }

  loadImageBatch();

  if (files.length > batchSize) {
    const loadMoreBtn = document.createElement("button");
    loadMoreBtn.textContent = "Mostrar Mais";
    loadMoreBtn.classList.add("mt-4", "px-4", "py-2", "bg-blue-500", "text-white", "rounded", "hover:bg-blue-700");
    loadMoreBtn.addEventListener("click", () => {
      loadImageBatch();
      if (currentBatchIndex * batchSize >= files.length) {
        loadMoreBtn.style.display = "none";
      }
    });
    imageContainer.appendChild(loadMoreBtn);
  }
}

function validateImages(file) {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];
  return allowedTypes.includes(file.type);
}

function removeImage(index) {
  const imgContainers = preview.querySelectorAll('.preview-container');
  if (imgContainers[index]) {
    imgContainers[index].remove();
    showNotification(`Imagem ${index + 1} removida.`);
  }
}

function clearPreview() {
  preview.innerHTML = "";
  previewDiv.innerHTML = "";
}

async function previewImages() {
  const files = fileInput.files;
  if (!files || !preview) return;

  preview.innerHTML = "";

  const fileArray = Array.from(files);
  let invalidFilesDetected = false;

  for (const [index, file] of fileArray.entries()) {

    if (validateImages(file)) {
      const imgContainer = await createImagePreview(file, index);
      preview.appendChild(imgContainer);
    } else {
      removeInvalidImage(index);
      invalidFilesDetected = true; 
    }
  }

  if (invalidFilesDetected) {
    showNotification('Foram identificadas imagens que não eram suportadas e foram removidas.');
  }

  previewDiv.classList.remove("hidden");
}

function setupDragAndDrop() {
  const allowedTypes = ["image/jpeg", "image/png", "image/gif", "image/webp"]; // Tipos de arquivo permitidos

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

    const validFiles = Array.from(files).filter((file) =>
      allowedTypes.includes(file.type)
    );

    if (validFiles.length === 0) {
      showErrorNotification("Por favor, adicione apenas arquivos de imagem (JPEG, PNG ou WebP).");
      return;
    }

    fileInput.files = validFiles;
    await previewImages();
  });

  function preventDefaults(e) {
    e.preventDefault();
    e.stopPropagation();
  }
}

function removeInvalidImage(index) {
  const imgContainers = preview.querySelectorAll('.flex');
  if (imgContainers[index]) {
    imgContainers[index].remove();
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
  if (currentStepIndex === 0) {
      showStep(1);
  }
  showStep(currentStepIndex + 1);
}

function resetForm() {
  currentStepIndex = 0;

  form.reset();
  const fileInputs = form.querySelectorAll('input[type="file"]');
  fileInputs.forEach(input => input.value = '');
  const steps = document.querySelectorAll('.step');
  steps.forEach(step => step.classList.add('hidden'));
  document.getElementById('step-1').classList.remove('hidden');
  
  const previewContainer = document.getElementById('preview');
  if (previewContainer) {
      previewContainer.innerHTML = '';
  }

  resetProgress();
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

function resetProgress() {
  progressBar.style.width = '0%';
  progressText.textContent = '0%';
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
  resetForm
};
