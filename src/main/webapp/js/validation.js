/**
 * Online Personal Finance Management Platform - Validation & Password Strength
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Password Strength Meter
    const passwordInput = document.getElementById('regPassword');
    const strengthFill = document.getElementById('strengthFill');
    const strengthText = document.getElementById('strengthText');

    if (passwordInput && strengthFill && strengthText) {
        passwordInput.addEventListener('input', function () {
            const val = passwordInput.value;
            let score = 0;

            if (val.length >= 6) score += 25;
            if (val.length >= 10) score += 25;
            if (/[0-9]/.test(val)) score += 20;
            if (/[A-Z]/.test(val)) score += 15;
            if (/[^A-Za-z0-9]/.test(val)) score += 15;

            strengthFill.style.width = score + '%';

            if (score <= 25) {
                strengthFill.style.backgroundColor = '#ef4444';
                strengthText.textContent = 'Weak';
                strengthText.className = 'text-danger small';
            } else if (score <= 65) {
                strengthFill.style.backgroundColor = '#f59e0b';
                strengthText.textContent = 'Medium';
                strengthText.className = 'text-warning small';
            } else {
                strengthFill.style.backgroundColor = '#10b981';
                strengthText.textContent = 'Strong';
                strengthText.className = 'text-success small';
            }
        });
    }

    // 2. Real-time Password Confirmation Matching
    const confirmInput = document.getElementById('regConfirmPassword');
    const matchStatus = document.getElementById('matchStatus');

    if (passwordInput && confirmInput && matchStatus) {
        function checkMatch() {
            if (!confirmInput.value) {
                matchStatus.textContent = '';
                return;
            }
            if (passwordInput.value === confirmInput.value) {
                matchStatus.textContent = 'Passwords match ✓';
                matchStatus.className = 'text-success small';
            } else {
                matchStatus.textContent = 'Passwords do not match ✗';
                matchStatus.className = 'text-danger small';
            }
        }

        confirmInput.addEventListener('input', checkMatch);
        passwordInput.addEventListener('input', checkMatch);
    }
});
