async function renderTotalDistanceTravelledColumnChart(element) {
  const data = await getData('total_distance_column_chart.json')
  
  const options = {
    title: {
      text: 'Total Distance Travelled by Player',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Total Distance (m)',
      },
    },
  }
  
  new ApexCharts(element, options).render();
}
