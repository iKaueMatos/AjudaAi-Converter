import { validateStep } from '/js/module/helper/validate.js';
import { date as current } from "/js/module/helper/local-storage-utils.js";

const reader = new FileReader();
let currentSessionStorage = parseInt(sessionStorage.getItem("currentStepIndex")) || current.currentStep;
// const location = document.body.getAttribute("data-page");
const preview = document.getElementById("preview");
const fileInput = document.getElementById("formFile");
const dropZone = document.getElementById("dropZone");
const previewDiv = document.querySelector(".preview");

async function createImagePreview(file, index) {
  const imgContainer = document.createElement("div");
  imgContainer.classList.add(
    "border", "border-gray-300", "p-4", "rounded-lg", "bg-white",
    "flex", "flex-col", "items-center", "shadow-md"
  );

  const imgTitle = document.createElement("h3");
  imgTitle.classList.add("font-semibold", "text-center", "text-gray-700", "mb-2");
  imgTitle.textContent = `Imagem ${index + 1}: ${file.name} (${file.type.split("/")[1].toUpperCase()})`;

  const img = await new Promise((resolve, reject) => {
    reader.onload = function (event) {
      const img = new Image();
      img.src = event.target.result;
      img.classList.add("w-48", "h-48", "object-cover", "rounded-lg", "mb-2");
      img.onload = () => resolve(img);
      img.onerror = reject;
    };
    reader.readAsDataURL(file);
  });

  const imgDimensions = document.createElement("p");
  imgDimensions.classList.add("text-sm", "text-gray-500", "text-center");
  imgDimensions.textContent = `Dimensões: ${img.width} x ${img.height} pixels`;

  imgContainer.appendChild(imgTitle);
  imgContainer.appendChild(img);
  imgContainer.appendChild(imgDimensions);

  return imgContainer;
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

  if (currentSessionStorage >= maxSteps) return;

  if (validateStep(currentSessionStorage + 1)) {
    currentSessionStorage++;
    const totalSteps = document.querySelectorAll(".step").length;

    if (currentSessionStorage < totalSteps) {
      showStep(currentSessionStorage + 1);
      sessionStorage.setItem("currentStepIndex", currentSessionStorage);
    }
  }
}

function previousStep() {
  if (currentSessionStorage > 0) {
    currentSessionStorage--;
    showStep(currentSessionStorage + 1);
    sessionStorage.setItem("currentStepIndex", currentSessionStorage);
  }

  if (preview.innerHTML) {
    clearPreview();
  }
}

function showStep(step) {
  document.querySelectorAll(".step").forEach((el) => el.classList.add("hidden"));
  const currentSessionStorageStep = document.getElementById(`step-${step}`);
  if (currentSessionStorageStep) {
    currentSessionStorageStep.classList.remove("hidden");
    updateProgress(step);
  }
}

function restoreStep() {
  if (currentSessionStorage > 0) {
    showStep(currentSessionStorage + 1);
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
