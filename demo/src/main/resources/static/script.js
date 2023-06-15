document.addEventListener('DOMContentLoaded', () => {
  const fetchDataBtn = document.getElementById('fetchDataBtn');
  fetchDataBtn.addEventListener('click', (event) => {
    event.preventDefault(); // Prevent form submission
    fetchStockData();
  });
});

async function fetchStockData() {
  const stockTicker = document.getElementById('stockTicker').value;
  const startDate = document.getElementById('startDate').value;
  const endDate = document.getElementById('endDate').value;

  // Validate input fields
  if (!stockTicker || !startDate || !endDate) {
    console.error('Please provide all input values.');
    return;
  }

  const url = `http://localhost:8080/refreshCharts?stockTicker=${encodeURIComponent(
    stockTicker
  )}&startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(
    endDate
  )}`;

  try {
    const response = await fetch(url);
    const data = await response.json();
    console.log(data);

    // Process the data
  const labels = data.map(item => moment(item.date).format('ll'));
  const ohlcData = data.map(item => ({
    t: moment(item.date).toDate(),
    o: item.open,
    h: item.high,
    l: item.low,
    c: item.close
  }));


    // Update the OHLC chart
    updateOHLCChart(labels, ohlcData);
  } catch (error) {
    console.error('Error fetching stock data:', error);
  }
}

function updateOHLCChart(labels, ohlcData) {
  const chartContainer = document.getElementById('chartContainer');

  if (!chartContainer) {
    console.error('Chart container not found.');
    return;
  }

  // Clear the chart container
  chartContainer.innerHTML = '';

  // Create a canvas element for the chart
  const canvas = document.createElement('canvas');
  canvas.id = 'ohlcChart';
  chartContainer.appendChild(canvas);

  // Process the data and create the OHLC chart dataset
  const ohlcDataset = {
    label: 'Stock Prices',
    data: ohlcData,
    borderColor: 'blue',
    backgroundColor: 'rgba(0, 0, 255, 0.2)',
    borderWidth: 1,
    pointRadius: 0,
    type: 'candlestick',
  };

  // Create the OHLC chart using Chart.js with Luxon as the date adapter
  const ctx = canvas.getContext('2d');
  new Chart(ctx, {
    type: 'candlestick',
    data: {
      labels: labels,
      datasets: [ohlcDataset],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          type: 'time',
          adapters: {
            date: luxonAdapter, // Use Luxon as the date adapter
          },
          time: {
            tooltipFormat: 'll',
            displayFormats: {
              day: 'll',
            },
          },
        },
        y: {
          beginAtZero: false,
        },
      },
    },
  });
}
