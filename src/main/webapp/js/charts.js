/**
 * Online Personal Finance Management Platform - Chart.js Engine
 * Renders spending doughnut charts & 6-month expenditure trend line.
 * Monetary labels use Indian Rupee formatting via toLocaleString('en-IN').
 */

let categoryChartInstance = null;
let trendChartInstance = null;
let paymentModeChartInstance = null;

function inrTick(value) {
    return '₹' + Number(value).toLocaleString('en-IN');
}

function inrTooltip(label, raw) {
    const prefix = label ? ` ${label}: ` : ' ';
    return prefix + formatInr(raw);
}

function renderCategoryChart(dataLabels, dataValues) {
    const ctx = document.getElementById('categoryDoughnutChart');
    if (!ctx) return;

    if (categoryChartInstance) {
        categoryChartInstance.destroy();
    }

    const isDark = document.documentElement.getAttribute('data-bs-theme') === 'dark';
    const textColor = isDark ? '#cbd5e1' : '#475569';

    const colors = [
        '#10b981', '#059669', '#047857', '#34d399',
        '#0284c7', '#38bdf8', '#f59e0b', '#ef4444', '#8b5cf6'
    ];

    categoryChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: dataLabels,
            datasets: [{
                data: dataValues,
                backgroundColor: colors.slice(0, dataLabels.length),
                borderWidth: 2,
                borderColor: isDark ? '#141418' : '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: textColor,
                        boxWidth: 12,
                        padding: 14,
                        font: { family: 'Inter', size: 12 }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return inrTooltip(context.label, context.raw);
                        }
                    }
                }
            },
            cutout: '70%'
        }
    });
}

function renderPaymentModeChart(dataLabels, dataValues) {
    const ctx = document.getElementById('paymentModeDoughnutChart');
    if (!ctx) return;

    if (paymentModeChartInstance) {
        paymentModeChartInstance.destroy();
    }

    const isDark = document.documentElement.getAttribute('data-bs-theme') === 'dark';
    const textColor = isDark ? '#cbd5e1' : '#475569';

    const colors = [
        '#10b981', '#0284c7', '#f59e0b', '#8b5cf6',
        '#ef4444', '#14b8a6', '#6366f1', '#f97316'
    ];

    paymentModeChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: dataLabels,
            datasets: [{
                data: dataValues,
                backgroundColor: colors.slice(0, dataLabels.length),
                borderWidth: 2,
                borderColor: isDark ? '#141418' : '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: textColor,
                        boxWidth: 12,
                        padding: 14,
                        font: { family: 'Inter', size: 12 }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return inrTooltip(context.label, context.raw);
                        }
                    }
                }
            },
            cutout: '70%'
        }
    });
}

function renderTrendChart(trendLabels, trendValues) {
    const ctx = document.getElementById('monthlyTrendLineChart');
    if (!ctx) return;

    if (trendChartInstance) {
        trendChartInstance.destroy();
    }

    const isDark = document.documentElement.getAttribute('data-bs-theme') === 'dark';
    const textColor = isDark ? '#cbd5e1' : '#475569';
    const gridColor = isDark ? '#27272a' : '#f1f5f9';

    trendChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: trendLabels,
            datasets: [{
                label: 'Monthly Spending (₹)',
                data: trendValues,
                borderColor: '#10b981',
                backgroundColor: isDark ? 'rgba(16, 185, 129, 0.15)' : 'rgba(5, 150, 105, 0.08)',
                fill: true,
                tension: 0.35,
                borderWidth: 3,
                pointBackgroundColor: '#10b981',
                pointRadius: 4,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return ' Spent: ' + formatInr(context.raw);
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid: { color: gridColor },
                    ticks: { color: textColor, font: { family: 'Inter' } }
                },
                y: {
                    beginAtZero: true,
                    grid: { color: gridColor },
                    ticks: {
                        color: textColor,
                        font: { family: 'Inter' },
                        callback: function (val) {
                            return inrTick(val);
                        }
                    }
                }
            }
        }
    });
}
