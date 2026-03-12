export const useAlertStore = () => {
  const showSuccess = (message: string) => console.log('[SUCCESS]', message);
  const showError = (title: string, message?: string) => console.error('[ERROR]', title, message);
  const showWarning = () => Promise.resolve({ isConfirmed: true });
  return { showSuccess, showError, showWarning };
};
