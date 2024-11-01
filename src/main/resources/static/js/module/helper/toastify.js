function showDownloadNotification(message = "Seu download está pronto!", duration = 1000, backgroundColor = "#4CAF50") {
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

export { showDownloadNotification }