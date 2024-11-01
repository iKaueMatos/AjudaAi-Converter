import * as redmensioning from '/js/pages/redmensioning/redmensioning.js';
import * as pages from '/js/pages/pages.js';
import * as form from '/js/components/form-redmensiong.js'

window.nextStep = redmensioning.nextStep;
window.previousStep = redmensioning.previousStep;
window.closeMenu = pages.closeMenu;
window.openMenu = pages.openMenu;
window.startLoading = redmensioning.startLoading;
window.clearForm = form.resetForm;
window.previewImages = redmensioning.previewImages;

document.addEventListener("DOMContentLoaded", () => {
  if (location === "redmensioning") {
    redmensioning.inicializeDragAndDrop();
  }
});