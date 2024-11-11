function showDownloadNotification(message = "Seu download está pronto!", duration = 2000, backgroundColor = "#4CAF50") {
    Toastify({
      text: message,
      duration: duration,
      close: true,
      gravity: "bottom",
      position: 'right',
      backgroundColor: backgroundColor,
      stopOnFocus: true,
      onClick: () => {
        
      },
      className: "toastify-custom",
    }).showToast();
}

function showErrorNotification(message, duration = 2000, backgroundColor = "#f44336") {
  Toastify({
    text: message,
    duration: duration,
    close: true,
    gravity: "bottom",
    position: 'right',
    backgroundColor: backgroundColor,
    stopOnFocus: true,
    onClick: () => {
      
    },
    className: "toastify-custom",
  }).showToast();
}

function showNotification(message, duration = 4000, backgroundColor = "#ffa500") {
  Toastify({
    text: message,
    duration: duration,
    close: true,
    gravity: "bottom",
    position: 'right',
    backgroundColor: backgroundColor,
    stopOnFocus: true,
    onClick: () => {},
    className: "toastify-custom",
  }).showToast();
}

export { showDownloadNotification, showErrorNotification, showNotification }