import showDownloadNotification from '/js/module/helper/toastify.js';
import { clearPreview, nextStep } from '/js/pages/redmensioning/redmensioning.js';
import { validateStep } from '/js/module/helper/validate.js';

const form = document.getElementById('multiStepForm');
const downloadButton = document.getElementById('downloadButton');
const progressBar = document.getElementById('progressBar');
const progressText = document.getElementById('progressText');

window.addEventListener('load', () => {
    const formTitle = sessionStorage.getItem('formTitle');
    const formDimension = sessionStorage.getItem('formDimension');
    const formFormat = sessionStorage.getItem('formFormat');
    const formCompression = sessionStorage.getItem('formCompression');

    if (!formTitle || !formDimension || !formFormat || !formCompression) {
        resetForm();    // Reset the form and return to the beginning
        return;
    }

    form.title.value = formTitle;
    form.dimension.value = formDimension;
    form.format.value = formFormat;
    form.compression.value = formCompression;
});

form.addEventListener('submit', (event) => {
    event.preventDefault();

    if (!validateStep(currentStep)) {
        return;
    }

    const formData = new FormData(event.target);

    sessionStorage.setItem('formTitle', formData.get('title') || '');
    sessionStorage.setItem('formDimension', formData.get('dimension') || '');
    sessionStorage.setItem('formFormat', formData.get('format') || '');
    sessionStorage.setItem('formCompression', formData.get('compression') || '');

    progressBar.style.width = '0%';
    progressText.textContent = '0%';

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/upload');

    xhr.upload.addEventListener('progress', (e) => {
        if (e.lengthComputable) {
            const percentComplete = (e.loaded / e.total) * 100;
            progressBar.style.width = `${percentComplete}%`;
            progressText.textContent = `${Math.round(percentComplete)}%`;
        }
    });

    xhr.onload = () => {
        if (xhr.status >= 200 && xhr.status < 300) {
            const blob = new Blob([xhr.response], { type: 'application/zip' });
            const url = window.URL.createObjectURL(blob);

            downloadButton.href = url;
            downloadButton.download = (formData.get('title') || 'download') + '.zip';
            downloadButton.classList.remove('hidden');

            nextStep(5);
            clearPreview();
            showDownloadNotification('Imagens processadas com sucesso!', 'success');
        } else {
            const errorMessage = JSON.parse(xhr.response).message || 'Erro ao processar a solicitação';
            showDownloadNotification(`Erro: ${errorMessage}`, 'error');
        }
    };

    xhr.send(formData);
});

function resetForm() {
    const form = document.getElementById('multiStepForm');
    form.reset();

    sessionStorage.removeItem('currentStepIndex');
    sessionStorage.removeItem('formTitle');
    sessionStorage.removeItem('formDimension');
    sessionStorage.removeItem('formFormat');
    sessionStorage.removeItem('formCompression');

    const steps = document.querySelectorAll('.step');
    steps.forEach(step => step.classList.add('hidden'));
    document.getElementById('step-1').classList.remove('hidden');

    const previewContainer = document.getElementById('preview');
    if (previewContainer) {
        previewContainer.innerHTML = '';
    }
}

export { resetForm };
